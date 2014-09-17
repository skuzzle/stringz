package de.skuzzle.stringz;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses format Strings and checks them against expected conversion characters.
 *
 * @author Simon Taddiken
 * @since 0.3.0
 */
public class FormatStringValidator {

    // %[argument_index$][flags][width][.precision][t]conversion
    private static final String formatSpecifier =
            "%(\\d+\\$)?([-#+ 0,(\\<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])";

    private static final Pattern fsPattern = Pattern.compile(formatSpecifier);
    private static final int INDEX_GROUP = 1;
    private static final int CONVERSION_GROUP = 6;


    public void parseFormatString(String f, String[] expectedConversion) {
        final List<String> conversions = listConversions(f);

        if (expectedConversion.length == 0 && conversions.isEmpty()) {
            // nothing expected, nothing to validate
            return;
        }

        final int commonMax = Math.min(conversions.size(), expectedConversion.length);

        for (int i = 0; i < commonMax; ++i) {
            final String expected = expectedConversion[i];
            final String actual = conversions.get(i);

            if (actual == null) {
                throw new FormatValidationException(String.format(
                        "Missing format specifier for argument index %d", i));
            }

            if (!expected.equalsIgnoreCase(actual)) {
                throw new FormatValidationException(String.format(
                        "Argument %d with conversion character '%s' does not match the expected conversion character '%s'",
                        i, actual, expected));
            }
        }

        final int difference = Math.abs(expectedConversion.length - conversions.size());
        if (expectedConversion.length > conversions.size()) {
            throw new FormatValidationException(String.format(
                    "The validated String lacks %d argument(s)", difference));
        } else if (conversions.size() > expectedConversion.length) {
            throw new FormatValidationException(String.format(
                    "The validated String contains %d more argument(s) than expected",
                    difference));
        }
    }

    private List<String> listConversions(String f) {
        final Matcher m = fsPattern.matcher(f);
        final List<String> conversions = new ArrayList<>();

        int nextIndex = 0;
        while (m.find()) {
            final MatchResult mr = m.toMatchResult();
            final String conversion = mr.group(CONVERSION_GROUP);

            final int index;
            if (mr.group(INDEX_GROUP) != null) {
                String groupMatch = mr.group(INDEX_GROUP);
                // strip off trailing '$'
                groupMatch = groupMatch.substring(0, groupMatch.length() - 1);

                // NOTE: index in format specifier is 1 based
                index = Integer.parseInt(groupMatch) - 1;
            } else {
                index = nextIndex++;
            }

            ensureIndexExists(conversions, index);
            final String existingConversion = conversions.get(index);

            if (existingConversion != null &&
                    !existingConversion.equalsIgnoreCase(conversion)) {
                // two format strings referencing the same argument
                // ... with two different conversions
                throw new FormatValidationException(String.format(
                        "Argument with index %d is referenced through at least two different conversion characters: '%s' and '%s'",
                        index, conversion, existingConversion));
            }

            conversions.set(index, conversion);
        }
        return conversions;
    }

    private void ensureIndexExists(List<String> list, int index) {
        final int desiredSize = index + 1;
        while (list.size() < desiredSize) {
            list.add(null);
        }
    }
}

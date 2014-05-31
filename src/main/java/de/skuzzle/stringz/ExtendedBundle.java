package de.skuzzle.stringz;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ResourceBundle implementation that decorates another ResourceBundle and provides
 * inheritance of multiple specified bundles and key substitution.
 * 
 * @author Simon Taddiken
 */
class ExtendedBundle extends ResourceBundle {

    private final static class CacheKey {
        private final String baseName;
        private final Locale locale;

        public CacheKey(String baseName, Locale locale) {
            this.baseName = baseName;
            this.locale = locale;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.baseName, this.locale);
        }

        @Override
        public String toString() {
            return String.format("baseName=%s, locale=%s", this.baseName,
                    this.locale);
        }
    }

    private final static String INCLUDE_KEY = "@include";
    private final static Map<CacheKey, ResourceBundle> cache = new HashMap<>();

    public static ResourceBundle getBundle(String baseName,
            Locale targetLocale, ClassLoader loader, Control control) {
        final CacheKey cacheKey = new CacheKey(baseName, targetLocale);

        synchronized (cache) {
            final ResourceBundle cached = cache.get(cacheKey);
            if (cached != null) {
                return cached;
            }
        }
        final ResourceBundle bundle = ResourceBundle.getBundle(baseName,
                targetLocale, loader, control);
        synchronized (cache) {
            cache.put(cacheKey, bundle);
        }
        return new ExtendedBundle(bundle, baseName, targetLocale, control);
    }

    private final static Pattern REPLACE = Pattern.compile("\\$\\{([^}]+)\\}");
    private final ResourceBundle wrapped;
    private final Control control;
    private final Locale locale;
    private final String baseName;
    private final List<ResourceBundle> includes;

    private ExtendedBundle(ResourceBundle wrapped, String baseName,
            Locale locale,
            Control control) {
        if (wrapped == null) {
            throw new IllegalArgumentException("wrapped is null");
        } else if (control == null) {
            throw new IllegalArgumentException("control is null");
        } else if (locale == null) {
            throw new IllegalArgumentException("locale is null");
        }

        this.baseName = baseName;
        this.wrapped = wrapped;
        this.control = control;
        this.locale = locale;
        this.includes = new ArrayList<>();

        if (this.wrapped.containsKey(INCLUDE_KEY)) {
            final String includeString = this.wrapped.getString(INCLUDE_KEY);
            final String[] includes = includeString.split(";");
            for (final String includeName : includes) {
                final ResourceBundle include = getBundle(includeName,
                        this.locale, this.getClass().getClassLoader(),
                        this.control);
                this.includes.add(include);
            }
        }
    }

    private Object findObject(String key) {
        try {
            return this.wrapped.getObject(key);
        } catch (MissingResourceException e) {
            // search includes
            for (final ResourceBundle bundle : this.includes) {
                try {
                    return bundle.getObject(key);
                } catch (MissingResourceException e1) {
                    // ignore and go on
                }
            }
        }
        throw new MissingResourceException(String.format(
                "Could not locate resource %s", key),
                getClass().getName(), key);
    }

    @Override
    protected Object handleGetObject(String key) {
        final Object value = findObject(key);
        if (value == null || !(value instanceof String)) {
            return value;
        }
        String s = (String) value;

        boolean replaced;
        do {
            replaced = false;
            Matcher m = REPLACE.matcher(s);
            final StringBuilder temp = new StringBuilder(s.length());

            MatchResult lastMatch = null;
            while (m.find()) {
                if (lastMatch == null) {
                    // first match. Append prefix
                    temp.append(s.substring(0, m.start()));
                } else {
                    // append all between last match and current
                    temp.append(s.substring(lastMatch.end(), m.start()));
                }

                final String subkey = m.group(1);
                final Object subvalue = findObject(subkey);
                temp.append(subvalue);
                lastMatch = m.toMatchResult();
                replaced = true;
            }

            if (lastMatch != null) {
                // append postfix
                temp.append(s.substring(lastMatch.end(), s.length()));
            }
            s = replaced ? temp.toString() : s;
        } while (replaced);
        return s;
    }

    @Override
    public Enumeration<String> getKeys() {
        return this.wrapped.getKeys();
    }

    @Override
    public String toString() {
        return String.format("baseName=%s, includes=%s", this.baseName,
                this.includes.toString());
    }
}

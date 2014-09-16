package de.skuzzle.stringz;

import org.junit.Before;
import org.junit.Test;

public class FormatStringValidatorTest {

    private FormatStringValidator validator;

    @Before
    public void setup() {
        this.validator = new FormatStringValidator();
    }

    private String[] expected(String... c) {
        return c;
    }

    @Test
    public void testIsValid1() {
        this.validator.parseFormatString("%s %2$s", expected("s", "s"));
    }

    @Test
    public void testIsValid2() {
        this.validator.parseFormatString("%s %d", expected("s", "d"));
    }

    @Test
    public void testIsValid3() {
        this.validator.parseFormatString("%2$d %s", expected("s", "d"));
    }

    @Test
    public void testIsValid4() {
        this.validator.parseFormatString("%3$d %1$s %2$f", expected("s", "f", "d"));
    }

    @Test
    public void testIsValid5() {
        this.validator.parseFormatString("no arguments", expected());
    }

    @Test
    public void testIsValid6() {
        this.validator.parseFormatString("%1$s %1$s", expected("s"));
    }

    @Test(expected = FormatValidationException.class)
    public void testInvalidSimple() {
        this.validator.parseFormatString("%s", expected("d"));
    }

    @Test(expected = FormatValidationException.class)
    public void testInvalidMoreExpected() {
        this.validator.parseFormatString("%s", expected("s", "d"));
    }

    @Test(expected = FormatValidationException.class)
    public void testInvalidLessExpected() {
        this.validator.parseFormatString("%s %d", expected("s"));
    }

    @Test(expected = FormatValidationException.class)
    public void testInvalidMissingArgument() {
        this.validator.parseFormatString("%2$s %3$d", expected("s", "d"));
    }

    @Test(expected = FormatValidationException.class)
    public void testInvalidDoubleReference() {
        this.validator.parseFormatString("%1$s %1$d", expected("d"));
    }
}

//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generator;

/**
 * Helper function for processing strings.
 */
public class Strings {

    private Strings() {
        // Do not create instances
    }

    /**
     * Returns string with leading and trailing whitespace removed
     * <p>
     * For empty strings or {@code null}, {@code null} is returned.
     * </p>
     *
     * @param value string to trim or {@code null}
     * @return trimmed string
     */
    public static String trimmed(String value) {
        if (value == null)
            return null;
        value = value.trim();
        if (value.length() == 0)
            return null;
        return value;
    }

    /**
     * Returns string without white space
     *
     * @param value string to process (non null)
     * @return resulting string with all whitespace removed
     */
    public static String whiteSpaceRemoved(String value) {
        return value.replaceAll(" ", "");
    }

    /**
     * Tests if a string is null or empty
     * <p>
     * A string consisting of all whitespace is considered empty.
     * </p>
     *
     * @param value string to test
     * @return boolean indicating if string is null or empty
     */
    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }
}

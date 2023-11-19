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
        if (value.isEmpty())
            return null;
        return value;
    }

    /**
     * Returns string without white space
     *
     * @param value string to process (non-null)
     * @return resulting string with all whitespace removed
     */
    public static String whiteSpaceRemoved(String value) {
        return value.replace(" ", "");
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
        return value == null || value.trim().isEmpty();
    }

    /**
     * Returns a string without leading and trailing spaces and with all
     * consecutive space characters replaced by a single space.
     * <p>
     * Whitespace other than space characters are not cleaned.
     * </p>
     *
     * @param value string to clean (non-null)
     * @return cleaned string
     */
    public static String spacesCleaned(String value) {
        value = value.trim();

        int len = value.length();
        boolean inWhitespace = false;
        StringBuilder sb = null;

        for (int i = 0; i < len; i += 1) {
            char ch = value.charAt(i);
            if (ch == ' ' && inWhitespace && sb == null) {
                sb = new StringBuilder(value.length());
                sb.append(value, 0, i);
            } else if (ch != ' ' || !inWhitespace) {
                inWhitespace = ch == ' ';
                if (sb != null)
                    sb.append(ch);
            }
        }

        return sb != null ? sb.toString() : value;
    }
}

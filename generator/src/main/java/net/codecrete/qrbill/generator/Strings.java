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
class Strings {


    private Strings() {
        // Do not create instances
    }

    /**
     * Returns string with leading and trailing whitespace removed
     * <p>
     *   For empty strings, {@code null} is returned.
     * </p>
     * @param value string to trim
     * @return trimmed string
     */
    static String trimmed(String value) {
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
     * @param value string to process
     * @return resulting string with all whitespace removed
     */
    static String whiteSpaceRemoved(String value) {
        StringBuilder sb = null;
        int len = value.length();
        int lastCopied = 0;
        for (int i = 0; i < len; i++) {
            char ch = value.charAt(i);
            if (ch == ' ') {
                if (i > lastCopied) {
                    if (sb == null)
                        sb = new StringBuilder();
                    sb.append(value, lastCopied, i);
                }
                lastCopied = i + 1;
            }
        }

        if (sb == null)
            return value;

        if (len > lastCopied)
            sb.append(value, lastCopied, len);
        return sb.toString();
    }


    /**
     * Tests if a string is null or empty
     * <p>
     *    A string consisting of all whitespace is
     *    considered empty.
     * </p>
     * 
     * @param value string to test
     * @return boolean indicating if string is null or empty
     */
    static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }

	/**
	 * Convert a string to an integer and calculate modulo 97 according to ISO11649 and IBAN
	 * checksum standard
	 * @param reference
	 * @return
	 */
    static int calculateMod97(String reference) throws IllegalArgumentException {
        String rearranged = reference.substring(4) + reference.substring(0, 4);
        int len = rearranged.length();
        int sum = 0;
        for (int i = 0; i < len; i++) {
            char ch = rearranged.charAt(i);
            if (ch >= '0' && ch <= '9') {
                sum = sum * 10 + (ch - '0');
            } else if (ch >= 'A' && ch <= 'Z') {
                sum = sum * 100 + (ch - 'A' + 10);
            } else if (ch >= 'a' && ch <= 'z') {
                sum = sum * 100 + (ch - 'a' + 10);
            } else {
                throw new IllegalArgumentException("Invalid character in reference: " + ch);
            }
            if (sum > 9999999)
                sum = sum % 97;
        }

        sum = sum % 97;
        return sum;
    }
}
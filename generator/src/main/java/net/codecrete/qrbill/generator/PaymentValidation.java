//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generator;

import java.text.Normalizer;


/**
 * Field validations related to Swiss Payment standards
 */
class PaymentValidation {

    private PaymentValidation() {
        // Do not create instances
    }

    /** Result of cleaning a string value */
    static class CleaningResult {
        /** Cleaned string */
        String cleanedString;
        /** Flag indicating that unsupported characters have been replaced */
        boolean replacedUnsupportedChars;
    }


    /**
     * Cleans a string value.
     * <p>
     *     Unsupported characters (according to Swiss Payment Standars 2018, ch. 2.4.1 and appendix D)
     *     are replaced with spaces (unsupported whitespace)
     *     or dots (all other unsupported characters). Leading and trailing
     *     whitespace is removed.
     * </p>
     * <p>
     *     If characters beyond 0xff are detected, the string is first normalized
     *     such that letters with umlauts or accents expressed with two code points
     *     are merged into a single code point (if possible), some of which might
     *     become valid.
     * </p>
     * <p>
     *     If the resulting strings is all white space, {@code null} is
     *     returned and no warning is added.
     * </p>
     * @param value string value to clean
     * @param result result to be filled with cleaned string and flag 
     */
    static void cleanValue(String value, CleaningResult result) {
        result.cleanedString = null;
        result.replacedUnsupportedChars = false;
        cleanValue(value, result, false);
        if (result.cleanedString != null && result.cleanedString.length() == 0)
            result.cleanedString = null;
    }

    private static void cleanValue(String value, CleaningResult result, boolean isNormalized) {

        /* This code has cognitive complexity 30. Deal with it. */

        if (value == null)
            return;

        int len = value.length(); // length of value
        boolean justProcessedSpace = false; // flag indicating whether we've just processed a space character
        StringBuilder sb = null; // String builder for result
        int lastCopiedPos = 0; // last position (excluding) copied to the result

        // String processing pattern: Iterate all characters and focus on runs of valid characters
        // that can simply be copied. If all characters are valid, no memory is allocated.
        int pos = 0;
        while (pos < len) {
            char ch = value.charAt(pos); // current character

            if (PaymentValidation.isValidQRBillCharacter(ch)) {
                justProcessedSpace = ch == ' ';
                pos++;
                continue;
            }

            // Check for normalization
            if (ch > 0xff && !isNormalized) {
                isNormalized = Normalizer.isNormalized(value, Normalizer.Form.NFC);
                if (!isNormalized) {
                    // Normalize string and start over
                    value = Normalizer.normalize(value, Normalizer.Form.NFC);
                    cleanValue(value, result, true);
                    return;
                }
            }

            if (sb == null)
                sb = new StringBuilder(value.length());

            // copy processed characters to result before taking care of the invalid character
            if (pos > lastCopiedPos)
                sb.append(value, lastCopiedPos, pos);

            if (Character.isHighSurrogate(ch)) {
                // Proper Unicode handling to prevent surrogates and combining characters
                // from being replaced with multiples periods.
                int codePoint = value.codePointAt(pos);
                if (Character.getType(codePoint) != Character.COMBINING_SPACING_MARK)
                    sb.append('.');
                justProcessedSpace = false;
                pos++;
            } else {
                if (Character.isWhitespace(ch)) {
                    if (!justProcessedSpace)
                        sb.append(' ');
                    justProcessedSpace = true;
                } else {
                    sb.append('.');
                    justProcessedSpace = false;
                }
            }
            pos++;
            lastCopiedPos = pos;
        }

        if (sb == null) {
            result.cleanedString = value.trim();
            return;
        }

        if (lastCopiedPos < len)
            sb.append(value, lastCopiedPos, len);

        result.cleanedString = sb.toString().trim();
        result.replacedUnsupportedChars = true;
    }


    /**
     * Validates if the string is a valid IBAN number
     * <p>
     *   All whitespace must have been removed before the
     *   the validation is performed.
     * </p>
     * <p>
     *   The string is checked for valid characters, valid length
     *   and for a valid check digit.
     * </p>
     */
    static boolean isValidIBAN(String iban) {
        if (iban.length() < 5)
            return false;
        if (!isAlphaNumeric(iban))
            return false;
        if (!Character.isLetter(iban.charAt(0)) || !Character.isLetter(iban.charAt(1))
                || !Character.isDigit(iban.charAt(2)) || !Character.isDigit(iban.charAt(3)))
            return false;

        return hasValidMod97CheckDigits(iban);
    }
    
    private static final int[] MOD_10 = { 0, 9, 4, 6, 8, 2, 7, 1, 3, 5 };

    /**
     * Validates if the string is a valid QR reference number.
     * <p>
     *   A valid QR reference is a valid ISR reference number.
     * </p>
     * <p>
     *   All whitespace must have been removed before the
     *   the validation is performed.
     * </p>
     * <p>
     *   The string is checked for valid characters, valid length
     *   and a valid check digit.
     * </p>
     */
    static boolean isValidQRReferenceNo(String referenceNo) {
        if (!isNumeric(referenceNo))
            return false;

        int carry = 0;
        int len = referenceNo.length();
        if (len != 27)
            return false;

        for (int i = 0; i < len; i++) {
            int digit = referenceNo.charAt(i) - '0';
            carry = MOD_10[(carry + digit) % 10];
        }

        return carry == 0;
    }

    /**
     * Validates if the string is a valid ISO 11649 reference number.
     * <p>
     *   All whitespace must have been removed before the
     *   the validation is performed.
     * </p>
     * <p>
     *   The string is checked for valid characters, valid length
     *   and a valid check digit.
     * </p>
     */
    static boolean isValidISO11649ReferenceNo(String referenceNo) {
        if (referenceNo.length() < 5 || referenceNo.length() > 25)
            return false;

        if (!isAlphaNumeric(referenceNo))
            return false;

        if (!Character.isDigit(referenceNo.charAt(2)) || !Character.isDigit(referenceNo.charAt(3)))
            return false;

        return hasValidMod97CheckDigits(referenceNo);
    }

    private static boolean hasValidMod97CheckDigits(String number) {
        try {
            return Strings.calculateMod97(number) == 1;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static boolean isNumeric(String value) {
        int len = value.length();
        for (int i = 0; i < len; i++) {
            char ch = value.charAt(i);
            if (ch < '0' || ch > '9')
                return false;
        }
        return true;
    }

    static boolean isAlphaNumeric(String value) {
        int len = value.length();
        for (int i = 0; i < len; i++) {
            char ch = value.charAt(i);
            if (ch >= '0' && ch <= '9')
                continue;
            if (ch >= 'A' && ch <= 'Z')
                continue;
            if (ch >= 'a' && ch <= 'z')
                continue;
            return false;
        }
        return true;
    }

    private static boolean isValidQRBillCharacter(char ch) {
        if (ch < 0x20)
            return false;
        if (ch == 0x5e)
            return false;
        if (ch <= 0x7e)
            return true;
        if (ch == 0xa3 || ch == 0xb4)
            return true;
        if (ch < 0xc0 || ch > 0xfd)
            return false;
        if (ch == 0xc3 || ch == 0xc5 || ch == 0xc6)
            return false;
        if (ch == 0xd0 || ch == 0xd5 || ch == 0xd7 || ch == 0xd8)
            return false;
        if (ch == 0xdd || ch == 0xde)
            return false;
        if (ch == 0xe3 || ch == 0xe5 || ch == 0xe6)
            return false;
        if (ch == 0xf0 || ch == 0xf5 || ch == 0xf8)
            return false;
        return true;
    }
}

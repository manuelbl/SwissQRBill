//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generator;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;

import static java.lang.Character.UnicodeBlock.COMBINING_DIACRITICAL_MARKS;

/**
 * Field validations related to Swiss Payment standards
 */
public class Payments {

    private Payments() {
        // Do not create instances
    }

    /**
     * Returns a cleaned text valid for the Swiss Payment Standards 2018.
     * <p>
     * Unsupported characters (according to Swiss Payment Standards 2018, ch. 2.4.1 and appendix D) are
     * replaced with supported characters, either with the same character without accent (e.g. A instead of Ă),
     * with characters of similar meaning (e.g. TM instead of ™, ij instead of ĳ), with a space
     * (for unsupported whitespace characters) or with a dot.
     * </p>
     * <p>
     * Some valid letters can be represented either with a single Unicode code point or with two code points,
     * e.g. the letter A with umlaut can be represented either with the single code point U+00C4 (latin capital
     * letter A with diaeresis) or with the two code points U+0041 U+0308 (latin capital letter A,
     * combining diaeresis). This will be recognized and converted to the valid single code point form.
     * </p>
     * <p>
     * If {@code text} is {@code null} or the resulting string would be empty, {@code null} is returned.
     * </p>
     *
     * @param text string to clean
     * @return valid text for Swiss payments
     */
    public static String cleanedText(String text) {
        CleaningResult result = new CleaningResult();
        cleanText(text, false, result);
        return result.cleanedString;
    }

    /**
     * Returns a cleaned and trimmed text valid for the Swiss Payment Standards 2018.
     * <p>
     * Unsupported characters (according to Swiss Payment Standards 2018, ch. 2.4.1 and appendix D) are
     * replaced with supported characters, either with the same character without accent (e.g. A instead of Ă),
     * with characters of similar meaning (e.g. TM instead of ™, ij instead of ĳ), with a space
     * (for unsupported whitespace characters) or with a dot.
     * </p>
     * <p>
     * Leading and trailing whitespace is removed. Multiple consecutive spaces are replaced with a single whitespace.
     * </p>
     * <p>
     * Some valid letters can be represented either with a single Unicode code point or with two code points,
     * e.g. the letter A with umlaut can be represented either with the single code point U+00C4 (latin capital
     * letter A with diaeresis) or with the two code points U+0041 U+0308 (latin capital letter A,
     * combining diaeresis). This will be recognized and converted to the valid single code point form.
     * </p>
     * <p>
     * If {@code text} is {@code null} or the resulting string would be empty, {@code null} is returned.
     * </p>
     *
     * @param text string to clean
     * @return valid text for Swiss payments
     */
    public static String cleanedAndTrimmedText(String text) {
        CleaningResult result = new CleaningResult();
        cleanText(text, true, result);
        return result.cleanedString;
    }

    /**
     * Indicates if the text consists only of characters allowed in Swiss payments.
     * <p>
     * The valid character set is defined in Swiss Payment Standards 2018, ch. 2.4.1 and appendix D
     * </p>
     * <p>
     * This method does not attempt to deal with accents and umlauts built from two code points. It will
     * return {@code false} if the text contains such characters.
     * </p>
     * @param text text to check
     * @return {@code true} if the text is valid, {@code false} otherwise
     */
    public static boolean isValidText(String text) {
        int len = text.length();
        for (int i = 0; i < len; i++) {
            if (!isValidCharacter(text.charAt(i)))
                return false;
        }
        return true;
    }

    /**
     * Returns if the character is a valid for a Swiss payment.
     * <p>
     * Valid characters are defined in Swiss Payment Standards 2022,
     * Customer Credit Transfer Initiation (pain.001), ch. 3.1 and appendix C.
     * </p>
     *
     * @param ch character to test
     * @return {@code true} if it is valid, {@code false} otherwise
     */
    @SuppressWarnings({"java:S1126", "RedundantIfStatement"})
    public static boolean isValidCharacter(char ch) {
        // Basic Latin
        if (ch >= 0x0020 && ch <= 0x007E)
            return true;

        // Latin-1 Supplement and Latin Extended-A
        if (ch >= 0x00A0 && ch <= 0x017F)
            return true;

        // Additional characters
        if (ch >= 0x0218 && ch <= 0x021B)
            return true;

        if (ch == 0x20AC)
            return true;

        return false;
    }

    /**
     * Returns if the code point is a valid for a Swiss payment.
     * <p>
     * Valid characters are defined in Swiss Payment Standards 2022,
     * Customer Credit Transfer Initiation (pain.001), ch. 3.1 and appendix C.
     * </p>
     *
     * @param codePoint code point to test
     * @return {@code true} if it is valid, {@code false} otherwise
     */
    public static boolean isValidCodePoint(int codePoint) {
        return codePoint <= 0xFFFF && isValidCharacter((char) codePoint);
    }

    static void cleanText(String text, boolean trimWhitespace, CleaningResult result) {
        result.cleanedString = null;
        result.replacedUnsupportedChars = false;

        if (text == null)
            return;

        // step 1: quick test for valid text
        boolean isValidString = isValidText(text);

        if (!isValidString) {
            // step 2: normalize string (to deal with accents built from two code points) and test again
            if (!Normalizer.isNormalized(text, Normalizer.Form.NFC)) {
                text = Normalizer.normalize(text, Normalizer.Form.NFC);
                isValidString = isValidText(text);
            }

            // step 3: replace invalid characters
            if (!isValidString) {
                text = replaceInvalidCharacters(text);
                result.replacedUnsupportedChars = true;
            }
        }

        if (trimWhitespace)
            text = Strings.spacesCleaned(text);
        if (text.isEmpty())
            text = null;

        result.cleanedString = text;
    }

    private static String replaceInvalidCharacters(String text) {
        StringBuilder sb = new StringBuilder();
        int len = text.length();
        int offset = 0;
        boolean inFallback = false;
        while (offset < len) {
            final int codePoint = text.codePointAt(offset);

            if (isValidCodePoint(codePoint)) {
                // valid code point
                sb.append((char) codePoint);
                inFallback = false;
            } else if (replaceCodePoint(codePoint, sb)) {
                // good replacement
                inFallback = false;
            } else if (!inFallback) {
                // no replacement found and not consecutive fallback
                sb.append('.');
                inFallback = true;
            }

            offset += Character.charCount(codePoint);
        }
        return sb.toString();
    }

    // sorted by code point (BMP only, no surrogates)
    private static final char[] ACCENTED_CHARS =
            "ƠơƯưǍǎǏǐǑǒǓǔǕǖǗǘǙǚǛǜǞǟǠǡǢǣǦǧǨǩǪǫǬǭǰǴǵǸǹǺǻǼǽǾǿȀȁȂȃȄȅȆȇȈȉȊȋȌȍȎȏȐȑȒȓȔȕȖȗȞȟȦȧȨȩȪȫȬȭȮȯȰȱȲȳ΅ḀḁḂḃḄḅḆḇḈḉḊḋḌḍḎḏḐḑḒḓḔḕḖḗḘḙḚḛḜḝḞḟḠḡḢḣḤḥḦḧḨḩḪḫḬḭḮḯḰḱḲḳḴḵḶḷḸḹḺḻḼḽḾḿṀṁṂṃṄṅṆṇṈṉṊṋṌṍṎṏṐṑṒṓṔṕṖṗṘṙṚṛṜṝṞṟṠṡṢṣṤṥṦṧṨṩṪṫṬṭṮṯṰṱṲṳṴṵṶṷṸṹṺṻṼṽṾṿẀẁẂẃẄẅẆẇẈẉẊẋẌẍẎẏẐẑẒẓẔẕẖẗẘẙẛẠạẢảẤấẦầẨẩẪẫẬậẮắẰằẲẳẴẵẶặẸẹẺẻẼẽẾếỀềỂểỄễỆệỈỉỊịỌọỎỏỐốỒồỔổỖỗỘộỚớỜờỞởỠỡỢợỤụỦủỨứỪừỬửỮữỰựỲỳỴỵỶỷỸỹ῁῭΅Å≠≮≯"
                    .toCharArray();

    private static final char[] REPLACEMENT_CHARS =
            "OoUuAaIiOoUuUuUuUuUuAaAaÆæGgKkOoOojGgNnAaÆæØøAaAaEeEeIiIiOoOoRrRrUuUuHhAaEeOoOoOoOoYy¨AaBbBbBbCcDdDdDdDdDdEeEeEeEeEeFfGgHhHhHhHhHhIiIiKkKkKkLlLlLlLlMmMmMmNnNnNnNnOoOoOoOoPpPpRrRrRrRrSsSsSsSsSsTtTtTtTtUuUuUuUuUuVvVvWwWwWwWwWwXxXxYyZzZzZzhtwyſAaAaAaAaAaAaAaAaAaAaAaAaEeEeEeEeEeEeEeEeIiIiOoOoOoOoOoOoOoOoOoOoOoOoUuUuUuUuUuUuUuYyYyYyYy¨¨¨A=<>"
                    .toCharArray();

    private static boolean replaceCodePoint(int codePoint, StringBuilder sb) {
        // whitespace is replaced with a space
        if (Character.isWhitespace(codePoint)) {
            sb.append(' ');
            return true;
        }

        // check if code point is a valid character without accent (precomputed case)
        if (codePoint <= 0xFFFF) {
            int pos = Arrays.binarySearch(ACCENTED_CHARS, (char) codePoint);
            if (pos >= 0) {
                sb.append(REPLACEMENT_CHARS[pos]);
                return true;
            }
        }

        // check if code point is a valid character with accent (using canonical decomposition)
        String codePointString = new String(new int[] { codePoint }, 0, 1);
        String decomposed1 = Normalizer.normalize(codePointString, Normalizer.Form.NFD);
        int firstCodePoint = decomposed1.codePointAt(0);
        if (decomposed1.length() > 1 && isValidCodePoint(firstCodePoint)) {
            int secondCodePoint = decomposed1.codePointAt(1);
            if (Character.UnicodeBlock.of(secondCodePoint) != COMBINING_DIACRITICAL_MARKS) {
                sb.append((char) firstCodePoint);
                return true;
            }
        }

        // check if compatibility decomposition results in valid substring
        String decomposed2 = decomposedString(codePointString);
        if (decomposed2 != null) {
            sb.append(decomposed2);
            return true;
        }

        // no good replacement
        return false;
    }

    private static String decomposedString(String codePointString) {
        String decomposedString = Normalizer.normalize(codePointString, Normalizer.Form.NFKD);
        int len = decomposedString.length();
        for (int i = 0; i < len; i += 1) {
            if (!isValidCharacter(decomposedString.charAt(i)))
                return null;
        }
        return decomposedString;
    }

    /**
     * Result of cleaning a string value
     */
    static class CleaningResult {
        /**
         * Cleaned string
         */
        String cleanedString;
        /**
         * Flag indicating that unsupported characters have been replaced
         */
        boolean replacedUnsupportedChars;
    }

    /**
     * Validates if the string is a valid IBAN number
     * <p>
     * The string is checked for valid characters, valid length and for a valid
     * check digit. White space is ignored.
     * </p>
     *
     * @param iban IBAN to validate
     * @return {@code true} if the IBAN is valid, {@code false} otherwise
     */
    public static boolean isValidIBAN(String iban) {

        iban = Strings.whiteSpaceRemoved(iban);

        int len = iban.length();
        if (len < 5)
            return false;

        if (!isAlphaNumeric(iban))
            return false;

        // Check for country code
        if (!Character.isLetter(iban.charAt(0)) || !Character.isLetter(iban.charAt(1)))
            return false;

        // Check for check digits
        if (!Character.isDigit(iban.charAt(2)) || !Character.isDigit(iban.charAt(3)))
            return false;

        String checkDigits = iban.substring(2, 4);
        if ("00".equals(checkDigits) || "01".equals(checkDigits) || "99".equals(checkDigits))
            return false;

        return hasValidMod97CheckDigits(iban);
    }

    /**
     * Indicates if the string is a valid QR-IBAN.
     * <p>
     * QR-IBANs are IBANs with an institution ID in the range 30000 to 31999
     * and a country code for Switzerland or Liechtenstein.
     * Thus, they must have the format "CH..30...", "CH..31...", "LI..30..." or "LI..31...".
     * </p>
     *
     * @param iban account number to check
     * @return {@code true} for valid QR-IBANs, {@code false} otherwise
     */
    public static boolean isQRIBAN(String iban) {
        iban = Strings.whiteSpaceRemoved(iban).toUpperCase(Locale.US);
        return isValidIBAN(iban)
                && (iban.startsWith("CH") || iban.startsWith("LI"))
                && iban.charAt(4) == '3'
                && (iban.charAt(5) == '0' || iban.charAt(5) == '1');
    }

    /**
     * Formats an IBAN or creditor reference by inserting spaces.
     * <p>
     * Spaces are inserted to form groups of 4 letters/digits. If a group of less
     * than 4 letters/digits is needed, it appears at the end.
     * </p>
     *
     * @param iban IBAN or creditor reference without spaces
     * @return formatted IBAN or creditor reference
     */
    public static String formatIBAN(String iban) {
        StringBuilder sb = new StringBuilder(25);
        int len = iban.length();

        for (int pos = 0; pos < len; pos += 4) {
            int endPos = pos + 4;
            if (endPos > len)
                endPos = len;
            sb.append(iban, pos, endPos);
            if (endPos != len)
                sb.append(' ');
        }
        return sb.toString();
    }

    /**
     * Validates if the string is a valid ISO 11649 reference number.
     * <p>
     * The string is checked for valid characters, valid length and a valid check
     * digit. White space is ignored.
     * </p>
     *
     * @param reference ISO 11649 creditor reference to validate
     * @return {@code true} if the creditor reference is valid, {@code false}
     * otherwise
     */
    public static boolean isValidISO11649Reference(String reference) {

        reference = Strings.whiteSpaceRemoved(reference);

        if (reference.length() < 5 || reference.length() > 25)
            return false;

        if (!isAlphaNumeric(reference))
            return false;

        if (reference.charAt(0) != 'R' || reference.charAt(1) != 'F')
            return false;

        if (!Character.isDigit(reference.charAt(2)) || !Character.isDigit(reference.charAt(3)))
            return false;

        return hasValidMod97CheckDigits(reference);
    }

    /**
     * Creates a ISO11649 creditor reference from a raw string by prefixing the
     * string with "RF" and the modulo 97 checksum.
     * <p>
     * Whitespace is removed from the reference
     * </p>
     *
     * @param rawReference The raw string
     * @return ISO11649 creditor reference
     * @throws IllegalArgumentException if {@code rawReference} contains invalid
     *                                  characters
     */
    public static String createISO11649Reference(String rawReference) {
        final String whiteSpaceRemoved = Strings.whiteSpaceRemoved(rawReference);
        final int modulo = Payments.calculateMod97("RF00" + whiteSpaceRemoved);
        return String.format("RF%02d", 98 - modulo) + whiteSpaceRemoved;
    }

    private static boolean hasValidMod97CheckDigits(String number) {
        return calculateMod97(number) == 1;
    }

    /**
     * Calculate the reference's modulo 97 checksum according to ISO11649 and IBAN
     * standard.
     * <p>
     * The string may only contains digits, letters ('A' to 'Z' and 'a' to 'z', no
     * accents). It must not contain white space.
     * </p>
     *
     * @param reference the reference
     * @return the checksum (0 to 96)
     * @throws IllegalArgumentException thrown if the reference contains an invalid
     *                                  character
     */
    private static int calculateMod97(String reference) {
        int len = reference.length();
        if (len < 5)
            throw new IllegalArgumentException("Insufficient characters for checksum calculation");

        String rearranged = reference.substring(4) + reference.substring(0, 4);
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

    private static final int[] MOD_10 = { 0, 9, 4, 6, 8, 2, 7, 1, 3, 5 };

    /**
     * Validates if the string is a valid QR reference.
     * <p>
     * A valid QR reference is a valid ISR reference.
     * </p>
     * <p>
     * The string is checked for valid characters, valid length and a valid check
     * digit. White space is ignored.
     * </p>
     *
     * @param reference QR reference number to validate
     * @return {@code true} if the reference number is valid, {@code false}
     * otherwise
     */
    public static boolean isValidQRReference(String reference) {

        reference = Strings.whiteSpaceRemoved(reference);

        if (!isNumeric(reference))
            return false;

        int len = reference.length();
        if (len != 27)
            return false;

        return calculateMod10(reference) == 0;
    }

    /**
     * Creates a QR reference from a raw string by appending the checksum digit
     * and prepending zeros to make it the correct length.
     * <p>
     * Whitespace is removed from the reference
     * </p>
     *
     * @param rawReference The raw string (digits and whitespace only)
     * @return QR reference
     * @throws IllegalArgumentException if {@code rawReference} contains invalid
     *                                  characters
     */
    public static String createQRReference(String rawReference) {
        final String rawRef = Strings.whiteSpaceRemoved(rawReference);
        if (!isNumeric(rawRef))
            throw new IllegalArgumentException("Invalid character in reference (digits allowed only)");
        if (rawRef.length() > 26)
            throw new IllegalArgumentException("Reference number is too long");
        int mod10 = calculateMod10(rawRef);
        return "00000000000000000000000000".substring(0, 26 - rawRef.length()) + rawRef + (char) ('0' + mod10);

    }

    private static int calculateMod10(String reference) {
        int len = reference.length();
        int carry = 0;
        for (int i = 0; i < len; i++) {
            int digit = reference.charAt(i) - '0';
            carry = MOD_10[(carry + digit) % 10];
        }
        return (10 - carry) % 10;
    }

    /**
     * Formats a QR reference number by inserting spaces.
     * <p>
     * Spaces are inserted to create groups of 5 digits. If a group of less than 5
     * digits is needed, it appears at the start of the formatted reference number.
     * </p>
     *
     * @param refNo reference number without white space
     * @return formatted reference number
     */
    public static String formatQRReferenceNumber(String refNo) {
        int len = refNo.length();
        StringBuilder sb = new StringBuilder();
        int t = 0;
        while (t < len) {
            int n = t + (len - t - 1) % 5 + 1;
            if (t != 0)
                sb.append(" ");
            sb.append(refNo, t, n);
            t = n;
        }

        return sb.toString();
    }

    static boolean isNumeric(String value) {
        int len = value.length();
        for (int i = 0; i < len; i++) {
            char ch = value.charAt(i);
            if (ch < '0' || ch > '9')
                return false;
        }
        return true;
    }

    @SuppressWarnings({"java:S135", "BooleanMethodIsAlwaysInverted"})
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

    @SuppressWarnings("java:S135")
    static boolean isAlpha(String value) {
        int len = value.length();
        for (int i = 0; i < len; i++) {
            char ch = value.charAt(i);
            if (ch >= 'A' && ch <= 'Z')
                continue;
            if (ch >= 'a' && ch <= 'z')
                continue;
            return false;
        }
        return true;
    }
}

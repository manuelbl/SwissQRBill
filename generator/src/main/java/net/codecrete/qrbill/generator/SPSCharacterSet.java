//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import java.util.function.Predicate;

/**
 * Swiss Payment Standard character set.
 *
 * <p>
 * The character set defines the allowed characters in the various payment fields.
 * </p>
 */
public enum SPSCharacterSet {
    /**
     * Restrictive character set from the original Swiss Payment Standard and original QR bill specification.
     * <p>
     * Valid characters consist of a subset of the printable Latin-1 characters in the Unicode blocks <i>Basic Latin</i>
     * and <i>Latin-1 Supplement</i>.
     * </p>
     */
    LATIN_1_SUBSET(SPSCharacterSet::isInLatin1Subset),

    /**
     * Extended Latin character set.
     * <p>
     * Valid characters are all printable characters from the Unicode blocks <i>Basic Latin</i> (Unicode codePoints
     * U+0020 to U+007E), <i>Latin-1 Supplement</i> (Unicode codePoints U+00A0 to U+00FF) and <i>Latin Extended A</i>
     * (Unicode codePoints U+0100 to U+017F) plus a few additional characters (such as the Euro sign).
     * </p>
     * <p>
     * This character set has been introduced with SPS 2022 (November 18, 2022) but may not be used in QR bills
     * until November 21, 2025 when all banks are ready to accept messages with this character set.
     * </p>
     */
    EXTENDED_LATIN(SPSCharacterSet::isInExtendedLatin),

    /**
     * Full Unicode character set.
     * <p>
     * This character set may be used when decoding the QR code text. It is not suitable for generating QR bills
     * or payment messages in general, and it is not covered by the Swiss Payment Standard.
     * </p>
     */
    FULL_UNICODE(SPSCharacterSet::isInUnicode);

    private final Predicate<Integer> containsCharacter;

    SPSCharacterSet(Predicate<Integer> containsCharacter) {
        this.containsCharacter = containsCharacter;
    }

    /**
     * Returns if this characters set contains the specified character.
     * @param ch character
     * @return {@code true} if the character is in this character set, {@code false} otherwise
     */
    public boolean contains(char ch) {
        return containsCharacter.test((int)ch);
    }

    /**
     * Returns if this characters set contains the specified Unicode code point.
     * @param codePoint Unicode code point
     * @return {@code true} if the code point is in this character set, {@code false} otherwise
     */
    public boolean contains(int codePoint) {
        return containsCharacter.test(codePoint);
    }

    @SuppressWarnings("java:S3776")
    private static boolean isInLatin1Subset(int codePoint) {
        if (codePoint < 0x20)
            return false;
        if (codePoint == 0x5e)
            return false;
        if (codePoint <= 0x7e)
            return true;
        if (codePoint == 0xa3 || codePoint == 0xb4)
            return true;
        if (codePoint < 0xc0 || codePoint > 0xfd)
            return false;
        if (codePoint == 0xc3 || codePoint == 0xc5 || codePoint == 0xc6)
            return false;
        if (codePoint == 0xd0 || codePoint == 0xd5 || codePoint == 0xd7 || codePoint == 0xd8)
            return false;
        if (codePoint == 0xdd || codePoint == 0xde)
            return false;
        if (codePoint == 0xe3 || codePoint == 0xe5 || codePoint == 0xe6)
            return false;
        return codePoint != 0xf0 && codePoint != 0xf5 && codePoint != 0xf8;
    }

    @SuppressWarnings({"java:S1126", "RedundantIfStatement"})
    private static boolean isInExtendedLatin(int codePoint) {
        // Basic Latin
        if (codePoint >= 0x0020 && codePoint <= 0x007E)
            return true;

        // Latin-1 Supplement and Latin Extended-A
        if (codePoint >= 0x00A0 && codePoint <= 0x017F)
            return true;

        // Additional characters
        if (codePoint >= 0x0218 && codePoint <= 0x021B)
            return true;

        if (codePoint == 0x20AC)
            return true;

        return false;
    }

    @SuppressWarnings("java:S3400")
    private static boolean isInUnicode(int codePoint) {
        return true;
    }
}

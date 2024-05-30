//
// Swiss QR Bill Generator
// Copyright (c) 2024 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generator;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Character.UnicodeBlock.COMBINING_DIACRITICAL_MARKS;

class StringCleanup {
    private StringCleanup() {
    }

    // sorted by code point (BMP only, no surrogates, resulting in a single character valid for Latin-1 subset)
    private static final char[] QUICK_REPLACEMENTS_FROM =
            "¨¯¸ÃÅÕÝãåõÿĀāĂăĄąĆćĈĉĊċČčĎďĒēĔĕĖėĘęĚěĜĝĞğĠġĢģĤĥĨĩĪīĬĭĮįİĴĵĶķĹĺĻļĽľŃńŅņŇňŌōŎŏŐőŔŕŖŗŘřŚśŜŝŞşŠšŢţŤťŨũŪūŬŭŮůŰűŲųŴŵŶŷŸŹźŻżŽžƠơƯưǍǎǏǐǑǒǓǔǕǖǗǘǙǚǛǜǞǟǠǡǦǧǨǩǪǫǬǭǰǴǵǸǹǺǻȀȁȂȃȄȅȆȇȈȉȊȋȌȍȎȏȐȑȒȓȔȕȖȗȘșȚțȞȟȦȧȨȩȪȫȬȭȮȯȰȱȲȳ˘˙˚˛˜˝ͺ΄΅ḀḁḂḃḄḅḆḇḈḉḊḋḌḍḎḏḐḑḒḓḔḕḖḗḘḙḚḛḜḝḞḟḠḡḢḣḤḥḦḧḨḩḪḫḬḭḮḯḰḱḲḳḴḵḶḷḸḹḺḻḼḽḾḿṀṁṂṃṄṅṆṇṈṉṊṋṌṍṎṏṐṑṒṓṔṕṖṗṘṙṚṛṜṝṞṟṠṡṢṣṤṥṦṧṨṩṪṫṬṭṮṯṰṱṲṳṴṵṶṷṸṹṺṻṼṽṾṿẀẁẂẃẄẅẆẇẈẉẊẋẌẍẎẏẐẑẒẓẔẕẖẗẘẙẛẠạẢảẤấẦầẨẩẪẫẬậẮắẰằẲẳẴẵẶặẸẹẺẻẼẽẾếỀềỂểỄễỆệỈỉỊịỌọỎỏỐốỒồỔổỖỗỘộỚớỜờỞởỠỡỢợỤụỦủỨứỪừỬửỮữỰựỲỳỴỵỶỷỸỹ᾽᾿῀῁῍῎῏῝῞῟῭΅´῾‗‾Å≠≮≯﹉﹊﹋﹌￣"
                    .toCharArray();
    private static final char[] QUICK_REPLACEMENTS_TO =
            "   AAOYaaoyAaAaAaCcCcCcCcDdEeEeEeEeEeGgGgGgGgHhIiIiIiIiIJjKkLlLlLlNnNnNnOoOoOoRrRrRrSsSsSsSsTtTtUuUuUuUuUuUuWwYyYZzZzZzOoUuAaIiOoUuUuUuUuUuAaAaGgKkOoOojGgNnAaAaAaEeEeIiIiOoOoRrRrUuUuSsTtHhAaEeOoOoOoOoYy         AaBbBbBbCcDdDdDdDdDdEeEeEeEeEeFfGgHhHhHhHhHhIiIiKkKkKkLlLlLlLlMmMmMmNnNnNnNnOoOoOoOoPpPpRrRrRrRrSsSsSsSsSsTtTtTtTtUuUuUuUuUuVvVvWwWwWwWwWwXxXxYyZzZzZzhtwysAaAaAaAaAaAaAaAaAaAaAaAaEeEeEeEeEeEeEeEeIiIiOoOoOoOoOoOoOoOoOoOoOoOoUuUuUuUuUuUuUuYyYyYyYy                A=<>     "
                    .toCharArray();

    // additional replacements, not covered by Unicode decomposition
    // (from a single character to possibly multiple characters)
    private static final String[] ADDITIONAL_REPLACEMENT_PAIRS = new String[] {
            "Œ", "OE",
            "œ", "oe",
            "Æ", "AE",
            "æ", "ae",
            "Ǣ", "AE",
            "ǣ", "ae",
            "Ǽ", "AE",
            "ǽ", "ae",
            "Ǿ", "O",
            "ǿ", "o",
            "ȸ", "db",
            "ȹ", "qp",
            "Ø", "O",
            "ø", "o",
            "€", "E",
            "^", ".",
            "¡", "! ",
            "¢", "c",
            "¤", " ",
            "¥", "Y",
            "¦", "/",
            "§", "S",
            "©", "(c)",
            "«", "<<",
            "¬", "-",
            "\u00AD", "", // soft hyphen
            "®", "(r)",
            "°", "o",
            "±", "+-",
            "µ", "u",
            "¶", "P",
            "·", "-",
            "»", ">>",
            "¿", "? ",
            "Ð", "D",
            "×", "x",
            "Þ", "TH",
            "ð", "d",
            "þ", "th",
            "Đ", "D",
            "đ", "d",
            "Ħ", "H",
            "ħ", "h",
            "ı", "i",
            "ĸ", "k",
            "Ŀ", "L",
            "ŀ", "l",
            "Ł", "L",
            "ł", "l",
            "ŉ", "n",
            "Ŋ", "N",
            "ŋ", "n",
            "Ŧ", "T",
            "ŧ", "t",
            "⁄", "/", // fraction slash
    };

    // additional replacements, not covered by Unicode decomposition
    private static final Map<Integer, String> additionalReplacements = new HashMap<>();

    static {
        for (int i = 0; i < ADDITIONAL_REPLACEMENT_PAIRS.length; i += 2) {
            additionalReplacements.put(ADDITIONAL_REPLACEMENT_PAIRS[i].codePointAt(0), ADDITIONAL_REPLACEMENT_PAIRS[i + 1]);
        }
    }

    /**
     * Returns a cleaned text valid according to the specified character set.
     * <p>
     * Unsupported characters are replaced with supported characters, either with the same character without accent
     * (e.g. A instead of Ă), with characters of similar meaning (e.g. TM instead of ™, ij instead of ĳ),
     * with a space (for unsupported whitespace characters) or with a dot.
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
     * @param characterSet character set specifying valid characters
     * @return valid text for Swiss payments
     */
    static String cleanedText(String text, SPSCharacterSet characterSet) {
        CleaningResult result = new CleaningResult();
        cleanText(text, characterSet, false, result);
        return result.cleanedString;
    }

    /**
     * Returns a cleaned and trimmed text valid according to the specified character set.
     * <p>
     * Unsupported characters are replaced with supported characters, either with the same character without accent
     * (e.g. A instead of Ă), with characters of similar meaning (e.g. TM instead of ™, ij instead of ĳ),
     * with a space (for unsupported whitespace characters) or with a dot.
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
     * @param characterSet character set specifying valid characters
     * @return valid text for Swiss payments
     */
    static String cleanedAndTrimmedText(String text, SPSCharacterSet characterSet) {
        CleaningResult result = new CleaningResult();
        cleanText(text, characterSet, true, result);
        return result.cleanedString;
    }

    /**
     * Indicates if the text consists only of characters allowed in the specified character set.
     * <p>
     * This method does not attempt to deal with accents and umlauts built from two code points. It will
     * return {@code false} if the text contains such characters.
     * </p>
     * @param text text to check, possibly {@code null}
     * @param characterSet character set specifying valid characters
     * @return {@code true} if the text is valid, {@code false} otherwise
     */
    static boolean isValidText(String text, SPSCharacterSet characterSet) {
        if (text == null)
            return true;

        int len = text.length();
        for (int i = 0; i < len; i++) {
            if (!characterSet.contains(text.charAt(i)))
                return false;
        }
        return true;
    }

    static void cleanText(String text, SPSCharacterSet characterSet, boolean trimWhitespace, CleaningResult result) {
        result.cleanedString = null;
        result.replacedUnsupportedChars = false;

        if (text == null)
            return;

        // step 1: quick test for valid text
        boolean isValidString = isValidText(text, characterSet);

        if (!isValidString) {
            // step 2: normalize string (to deal with accents built from two code points) and test again
            if (!Normalizer.isNormalized(text, Normalizer.Form.NFC)) {
                text = Normalizer.normalize(text, Normalizer.Form.NFC);
                isValidString = isValidText(text, characterSet);
            }

            // step 3: replace characters
            if (!isValidString) {
                text = replaceCharacters(text, characterSet);
                result.replacedUnsupportedChars = true;
            }
        }

        if (trimWhitespace)
            text = Strings.spacesCleaned(text);
        if (text.isEmpty())
            text = null;

        result.cleanedString = text;
    }

    private static String replaceCharacters(String text, SPSCharacterSet characterSet) {
        StringBuilder sb = new StringBuilder();
        int len = text.length();
        int offset = 0;
        boolean inFallback = false;
        while (offset < len) {
            final int codePoint = text.codePointAt(offset);

            if (characterSet.contains(codePoint)) {
                // valid code point
                sb.appendCodePoint(codePoint);
                inFallback = false;
            } else if (replaceCodePoint(codePoint, characterSet, sb)) {
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

    private static boolean replaceCodePoint(int codePoint, SPSCharacterSet characterSet, StringBuilder sb) {
        // whitespace is replaced with a space
        if (Character.isWhitespace(codePoint)) {
            sb.append(' ');
            return true;
        }

        // check if there is a quick replacement (precomputed case)
        if (codePoint <= 0xFFFF) {
            int pos = Arrays.binarySearch(QUICK_REPLACEMENTS_FROM, (char) codePoint);
            if (pos >= 0) {
                sb.append(QUICK_REPLACEMENTS_TO[pos]);
                return true;
            }
        }

        String codePointString = new String(new int[] { codePoint }, 0, 1);

        // check if canonical decomposition yields a valid string
        String canonical = decomposedString(codePointString, characterSet, Normalizer.Form.NFD);
        if (canonical != null) {
            sb.append(canonical);
            return true;
        }

        // check if compatibility decomposition yields a valid string
        String compatibility = decomposedString(codePointString, characterSet, Normalizer.Form.NFKD);
        if (compatibility != null) {
            sb.append(compatibility);
            return true;
        }

        // check for additional replacements
        String replacement = additionalReplacements.get(codePoint);
        if (replacement != null) {
            sb.append(replacement);
            return true;
        }

        // no good replacement
        return false;
    }

    private static String decomposedString(String codePointString, SPSCharacterSet characterSet, Normalizer.Form form) {
        // decompose string
        String decomposedString = Normalizer.normalize(codePointString, form);

        boolean hasFractionSlash = false;

        // check for valid characters
        int len = decomposedString.length();
        for (int i = 0; i < len; i += 1) {
            if (!characterSet.contains(decomposedString.charAt(i))) {

                // check if decomposition consists one or more valid characters
                // and combining diacritical mark at the end
                if (i == len - 1 && Character.UnicodeBlock.of(decomposedString.charAt(i)) == COMBINING_DIACRITICAL_MARKS) {
                    return decomposedString.substring(0, i);
                }

                // check for fraction slash (U+2044)
                if (decomposedString.charAt(i) == '⁄') {
                    hasFractionSlash = true;
                } else {
                    // return null if no valid decomposition is available
                    return null;
                }
            }
        }

        return hasFractionSlash ? decomposedString.replace('⁄', '/') : decomposedString;
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
}

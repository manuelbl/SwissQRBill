package net.codecrete.qrbill.generator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Character set validation and replacement")
class PaymentsCharacterSetTest {
    private static final String TEXT_WITHOUT_COMBINING_ACCENTS = "àáâäçèéêëìíîïñòóôöùúûüýßÀÁÂÄÇÈÉÊËÌÍÎÏÒÓÔÖÙÚÛÜÑ";
    private static final String TEXT_WITH_COMBINING_ACCENTS = "àáâäçèéêëìíîïñòóôöùúûüýßÀÁÂÄÇÈÉÊËÌÍÎÏÒÓÔÖÙÚÛÜÑ";

    @ParameterizedTest
    @ValueSource(chars = { 'A', 'b', '3', '%', '{', '®', 'Ò', 'æ', 'Ă', 'Ķ', 'Ŕ', 'ț', '€' })
    void validCharacters_returnsTrue(char validChar) {
        assertTrue(Payments.isValidCharacter(validChar));
    }

    @ParameterizedTest
    @ValueSource(chars = { 'A', 'b', '3', '%', '{', '®', 'Ò', 'æ', 'Ă', 'Ķ', 'Ŕ', 'ț', '€' })
    void validCodePoints_returnsTrue(char validChar) {
        assertTrue(Payments.isValidCodePoint(validChar));
    }

    @ParameterizedTest
    @ValueSource(chars = { '\n', '\r', '\u007f', '\u0083', 'Ɖ', 'Ǒ', 'Ȑ', 'Ȟ' })
    void invalidCharacters_returnsFalse(char invalidChar) {
        assertFalse(Payments.isValidCharacter(invalidChar));
    }

    @ParameterizedTest
    @ValueSource(chars = { '\n', '\r', '\u007f', '\u0083', 'Ɖ', 'Ǒ', 'Ȑ', 'Ȟ' })
    void invalidCodePoints_returnsFalse(char invalidChar) {
        assertFalse(Payments.isValidCodePoint(invalidChar));
    }

    @ParameterizedTest
    @ValueSource(strings = { "abc", "ABC", "123", "äöüÄÖÜ", "àáâäçèéìíîïñôöùúûüýÿÀÁÂÄÇÊËÌÍÎÏÓÔÖÛÜÝ", "€", "£", "¥", " ", "" })
    void validText_returnsTrue(String validText) {
        assertTrue(Payments.isValidText(validText));
    }

    @ParameterizedTest
    @ValueSource(strings = { "a\nc", "ABǑC", "12\uD83D\uDE003", "äöü\uD83C\uDDEE\uD83C\uDDF9ÄÖÜ" })
    void invalidText_returnsFalse(String invalidText) {
        assertFalse(Payments.isValidText(invalidText));
    }

    @ParameterizedTest
    @ValueSource(strings = { "abc", " a b c ", "àáâäçè" })
    void cleanText_isNotChanged(String text) {
        assertEquals(text, Payments.cleanedText(text));
    }

    @Test
    void decomposedAccents_areComposed() {
        assertEquals(46, TEXT_WITHOUT_COMBINING_ACCENTS.length());
        assertEquals(59, TEXT_WITH_COMBINING_ACCENTS.length());
        assertEquals(TEXT_WITHOUT_COMBINING_ACCENTS, Payments.cleanedText(TEXT_WITH_COMBINING_ACCENTS));
    }

    @ParameterizedTest
    @ValueSource(strings = { "ab\nc|ab c", "ȆȇȈȉ|EeIi", "Ǒ|O", "ǉǌﬃ|ljnjffi", "Ǽ|Æ", "ʷ|w", "⁵|5", "℁|a/s",
            "Ⅶ|VII", "③|3", "xƉx|x.x", "x\uD83C\uDDE8\uD83C\uDDEDx|x.x" })
    void invalidCharacters_areReplaced(String combinedText) {
        String[] textAndExpected = combinedText.split("\\|");
        assertEquals(textAndExpected[1], Payments.cleanedAndTrimmedText(textAndExpected[0]));
    }

    @Test
    void cleanedNull_returnsNull() {
        assertNull(Payments.cleanedAndTrimmedText(null));
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "   "})
    void blankText_returnsNull(String text) {
        assertNull(Payments.cleanedAndTrimmedText(text));
    }

    @ParameterizedTest
    @ValueSource(strings = { "a   b  c|a b c", " a  b c|a b c"})
    void multipleSpaces_becomeSingleSpace(String combinedText) {
        String[] textAndExpected = combinedText.split("\\|");
        assertEquals(textAndExpected[1], Payments.cleanedAndTrimmedText(textAndExpected[0]));
    }

    @ParameterizedTest
    @ValueSource(strings = { " a ", " a  b", " "})
    void spaces_areNotTrimmed(String text) {
        assertEquals(text, Payments.cleanedText(text));
    }
}

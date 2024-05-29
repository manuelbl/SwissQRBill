package net.codecrete.qrbill.generator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static net.codecrete.qrbill.generator.SPSCharacterSet.EXTENDED_LATIN;
import static net.codecrete.qrbill.generator.SPSCharacterSet.LATIN_1_SUBSET;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Character set validation and replacement")
class PaymentsCharacterSetTest {
    private static final String TEXT_WITHOUT_COMBINING_ACCENTS = "àáâäçèéêëìíîïñòóôöùúûüýßÀÁÂÄÇÈÉÊËÌÍÎÏÒÓÔÖÙÚÛÜÑ";
    private static final String TEXT_WITH_COMBINING_ACCENTS = "àáâäçèéêëìíîïñòóôöùúûüýßÀÁÂÄÇÈÉÊËÌÍÎÏÒÓÔÖÙÚÛÜÑ";

    @ParameterizedTest
    @ValueSource(strings = { "abc", "ABC", "123", "äöüÄÖÜ", "àáâäçèéìíîïñôöùúûüýÿÀÁÂÄÇÊËÌÍÎÏÓÔÖÛÜÝ", "€", "£", "¥", " ", "" })
    void validText_returnsTrue(String validText) {
        assertTrue(Payments.isValidText(validText, EXTENDED_LATIN));
    }

    @ParameterizedTest
    @ValueSource(strings = { "a\nc", "ABǑC", "12\uD83D\uDE003", "äöü\uD83C\uDDEE\uD83C\uDDF9ÄÖÜ" })
    void invalidText_returnsFalse(String invalidText) {
        assertFalse(Payments.isValidText(invalidText, EXTENDED_LATIN));
    }

    @Test
    void nullText_isValid() {
        assertTrue(Payments.isValidText(null, LATIN_1_SUBSET));
        assertTrue(Payments.isValidText(null, EXTENDED_LATIN));
    }

    @ParameterizedTest
    @ValueSource(strings = { "abc", " a b c ", "àáâäçè" })
    void cleanText_isNotChanged(String text) {
        assertEquals(text, Payments.cleanedText(text, EXTENDED_LATIN));
    }

    @Test
    void decomposedAccents_areComposed() {
        assertEquals(46, TEXT_WITHOUT_COMBINING_ACCENTS.length());
        assertEquals(59, TEXT_WITH_COMBINING_ACCENTS.length());
        assertEquals(TEXT_WITHOUT_COMBINING_ACCENTS, Payments.cleanedText(TEXT_WITH_COMBINING_ACCENTS, EXTENDED_LATIN));
    }

    @ParameterizedTest
    @ValueSource(strings = { "ab\nc|ab c", "ȆȇȈȉ|EeIi", "Ǒ|O", "ǉǌﬃ|ljnjffi", "Ǽ|Æ", "ʷ|w", "⁵|5", "℁|a/s",
            "Ⅶ|VII", "③|3", "xƉx|x.x", "x\uD83C\uDDE8\uD83C\uDDEDx|x.x", "Ǆ|DZ" })
    void invalidCharacters_areReplaced(String combinedText) {
        String[] textAndExpected = combinedText.split("\\|");
        assertEquals(textAndExpected[1], Payments.cleanedAndTrimmedText(textAndExpected[0], EXTENDED_LATIN));
    }

    @Test
    void cleanedNull_returnsNull() {
        assertNull(Payments.cleanedAndTrimmedText(null, EXTENDED_LATIN));
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "   "})
    void blankText_returnsNull(String text) {
        assertNull(Payments.cleanedAndTrimmedText(text, EXTENDED_LATIN));
    }

    @ParameterizedTest
    @ValueSource(strings = { "a   b  c|a b c", " a  b c|a b c"})
    @SuppressWarnings("java:S4144")
    void multipleSpaces_becomeSingleSpace(String combinedText) {
        String[] textAndExpected = combinedText.split("\\|");
        assertEquals(textAndExpected[1], Payments.cleanedAndTrimmedText(textAndExpected[0], EXTENDED_LATIN));
    }

    @ParameterizedTest
    @ValueSource(strings = { " a ", " a  b", " "})
    void spaces_areNotTrimmed(String text) {
        assertEquals(text, Payments.cleanedText(text, EXTENDED_LATIN));
    }

    @ParameterizedTest
    @MethodSource("provideExtendedLatinChars")
    void allChars_haveGoodReplacement(char ch, String unicodeName) {
        // unicode code name is displayed if test fails

        String cleaned = Payments.cleanedText(String.valueOf(ch), LATIN_1_SUBSET);
        assertNotEquals(".", cleaned);
        assertTrue(Payments.isValidText(cleaned, LATIN_1_SUBSET));


        cleaned = Payments.cleanedText(String.valueOf(ch), EXTENDED_LATIN);
        assertNotEquals(".", cleaned);
        assertTrue(Payments.isValidText(cleaned, EXTENDED_LATIN));
    }

    private static Stream<Arguments> provideExtendedLatinChars() {
        Stream.Builder<Arguments> builder = Stream.builder();
        for (char ch = 0x0020; ch <= 0x007E; ch++) {
            if (ch == '.' || ch == '^')
                continue;
            builder.add(Arguments.of(ch, String.format("U+%04X", (int) ch)));
        }
        for (char ch = 0x00A0; ch <= 0x017F; ch++) {
            builder.add(Arguments.of(ch, String.format("U+%04X", (int) ch)));
        }
        builder.add(Arguments.of((char) 0x0218, "U+0218"));
        builder.add(Arguments.of((char) 0x0219, "U+0219"));
        builder.add(Arguments.of((char) 0x021A, "U+021A"));
        builder.add(Arguments.of((char) 0x021B, "U+021B"));
        builder.add(Arguments.of((char) 0x20AC, "U+20AC"));
        return builder.build();
    }
}

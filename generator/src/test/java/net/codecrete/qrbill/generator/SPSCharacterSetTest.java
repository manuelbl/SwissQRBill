package net.codecrete.qrbill.generator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for SPS character set
 */
@DisplayName("SPS character set")
class SPSCharacterSetTest {

    @ParameterizedTest
    @ValueSource(chars = { 'A', 'b', '3', '%', '{', '®', 'Ò', 'æ', 'Ă', 'Ķ', 'Ŕ', 'ț', '€' })
    void extendedLatin_containsValidCharacters(char validChar) {
        assertTrue(SPSCharacterSet.EXTENDED_LATIN.contains(validChar));
    }

    @ParameterizedTest
    @ValueSource(chars = { 'A', 'b', '3', '%', '{', '®', 'Ò', 'æ', 'Ă', 'Ķ', 'Ŕ', 'ț', '€' })
    void extendedLatin_containsValidCodePoints(char validChar) {
        assertTrue(SPSCharacterSet.EXTENDED_LATIN.contains((int)validChar));
    }

    @ParameterizedTest
    @ValueSource(chars = { '\n', '\r', '\u007f', '\u0083', 'Ɖ', 'Ǒ', 'Ȑ', 'Ȟ' })
    void extendedLatin_doesNotContainInvalidCharacters(char invalidChar) {
        assertFalse(SPSCharacterSet.EXTENDED_LATIN.contains(invalidChar));
    }

    @ParameterizedTest
    @ValueSource(chars = { '\n', '\r', '\u007f', '\u0083', 'Ɖ', 'Ǒ', 'Ȑ', 'Ȟ' })
    void extendedLatin_doesNotContainInvalidCodePoints(char invalidChar) {
        assertFalse(SPSCharacterSet.EXTENDED_LATIN.contains((int)invalidChar));
    }
}

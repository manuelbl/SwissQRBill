//
// Swiss QR Bill Generator
// Copyright (c) 2024 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

/**
 * Allowed line separators for separating the data fields in the text represented by the QR code
 * (according to the Swiss Implementation Guidelines for the QR-bill, § 4.1.4 Separator element)
 */
public enum QrDataSeparator {
    /**
     * Separate lines with the line feed (␊) character, i.e. unicode U+000A.
     */
    LF,
    /**
     * Separate lines with the carriage return (␍) character and line feed (␊) characters, i.e. unicode U+000D and U+000A.
     */
    CR_LF
}

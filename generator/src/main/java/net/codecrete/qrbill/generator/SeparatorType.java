//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

/**
 * Separator type above and betweeen payment part and receipt
 */
public enum SeparatorType {
    /**
     * No separators are drawn (because paper has perforation)
     */
    NONE,
    /**
     * Solid lines are drawn
     */
    SOLID_LINE,
    /**
     * Solid lines with a scissor symbol are drawn
     */
    SOLID_LINE_WITH_SCISSORS
}

//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

/**
 * QR bill language
 */
public enum Language {
    /**
     * German
     */
    DE,
    /**
     * French
     */
    FR,
    /**
     * Italian
     */
    IT,
    /**
     * Romansh.
     * <p>
     *     Romansh is not covered by the standard. However, processing problems are unlikely.
     *     The language only applies to the human-readable part. The QR code and its data are
     *     language independent.
     * </p>
     */
    RM,
    /**
     * English
     */
    EN
}

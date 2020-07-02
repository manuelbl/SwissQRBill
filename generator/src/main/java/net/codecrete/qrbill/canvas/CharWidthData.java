//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.canvas;


/**
 * Character width data for Helvetica, Arial, Frutiger and Liberation Sans.
 * <p>
 * Character widths are given in 1/1000 pt for font size 1. Only the character
 * range allowed for QR bill text is covered.
 * </p>
 */
class CharWidthData {

    private CharWidthData() {
        // don't instantiate
    }

    /**
     * Default character width for Helvetica Normal
     */
    static final char HELVETICA_NORMAL_DEFAULT_WIDTH = 556;

    /**
     * Character widths for Helvetica Normal (range 0x20 to 0x7f)
     */
    static final char[] HELVETICA_NORMAL_20_7F = {
            278, // 0x20
            278, // 0x21 !
            355, // 0x22 "
            556, // 0x23 #
            556, // 0x24 $
            889, // 0x25 %
            667, // 0x26 &
            191, // 0x27 '
            333, // 0x28 (
            333, // 0x29 )
            389, // 0x2A *
            584, // 0x2B +
            278, // 0x2C ,
            333, // 0x2D -
            278, // 0x2E .
            278, // 0x2F /
            556, // 0x30 0
            556, // 0x31 1
            556, // 0x32 2
            556, // 0x33 3
            556, // 0x34 4
            556, // 0x35 5
            556, // 0x36 6
            556, // 0x37 7
            556, // 0x38 8
            556, // 0x39 9
            278, // 0x3A :
            278, // 0x3B ;
            584, // 0x3C <
            584, // 0x3D =
            584, // 0x3E >
            556, // 0x3F ?
            1015, // 0x40 @
            667, // 0x41 A
            667, // 0x42 B
            722, // 0x43 C
            722, // 0x44 D
            667, // 0x45 E
            611, // 0x46 F
            778, // 0x47 G
            722, // 0x48 H
            278, // 0x49 I
            500, // 0x4A J
            667, // 0x4B K
            556, // 0x4C L
            833, // 0x4D M
            722, // 0x4E N
            778, // 0x4F O
            667, // 0x50 P
            778, // 0x51 Q
            722, // 0x52 R
            667, // 0x53 S
            611, // 0x54 T
            722, // 0x55 U
            667, // 0x56 V
            944, // 0x57 W
            667, // 0x58 X
            667, // 0x59 Y
            611, // 0x5A Z
            278, // 0x5B [
            278, // 0x5C \
            278, // 0x5D ]
            0, // unused
            556, // 0x5F _
            333, // 0x60 `
            556, // 0x61 a
            556, // 0x62 b
            500, // 0x63 c
            556, // 0x64 d
            556, // 0x65 e
            278, // 0x66 f
            556, // 0x67 g
            556, // 0x68 h
            222, // 0x69 i
            222, // 0x6A j
            500, // 0x6B k
            222, // 0x6C l
            833, // 0x6D m
            556, // 0x6E n
            556, // 0x6F o
            556, // 0x70 p
            556, // 0x71 q
            333, // 0x72 r
            500, // 0x73 s
            278, // 0x74 t
            556, // 0x75 u
            500, // 0x76 v
            722, // 0x77 w
            500, // 0x78 x
            500, // 0x79 y
            500, // 0x7A z
            334, // 0x7B {
            0, // unused
            334, // 0x7D }
            584, // 0x7E ~
            0 // unused
    };

    /**
     * Character widths for Helvetica Normal (range 0xa0 to 0xff)
     */
    static final char[] HELVETICA_NORMAL_A0_FF = {
            0, // unused
            0, // unused
            0, // unused
            556, // 0xA3 £
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            333, // 0xB4 ´
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            667, // 0xC0 À
            667, // 0xC1 Á
            667, // 0xC2 Â
            0, // unused
            667, // 0xC4 Ä
            0, // unused
            0, // unused
            722, // 0xC7 Ç
            667, // 0xC8 È
            667, // 0xC9 É
            667, // 0xCA Ê
            667, // 0xCB Ë
            278, // 0xCC Ì
            278, // 0xCD Í
            278, // 0xCE Î
            278, // 0xCF Ï
            0, // unused
            722, // 0xD1 Ñ
            778, // 0xD2 Ò
            778, // 0xD3 Ó
            778, // 0xD4 Ô
            0, // unused
            778, // 0xD6 Ö
            0, // unused
            0, // unused
            722, // 0xD9 Ù
            722, // 0xDA Ú
            722, // 0xDB Û
            722, // 0xDC Ü
            0, // unused
            0, // unused
            611, // 0xDF ß
            556, // 0xE0 à
            556, // 0xE1 á
            556, // 0xE2 â
            0, // unused
            556, // 0xE4 ä
            0, // unused
            0, // unused
            500, // 0xE7 ç
            556, // 0xE8 è
            556, // 0xE9 é
            556, // 0xEA ê
            556, // 0xEB ë
            278, // 0xEC ì
            278, // 0xED í
            278, // 0xEE î
            278, // 0xEF ï
            0, // unused
            556, // 0xF1 ñ
            556, // 0xF2 ò
            556, // 0xF3 ó
            556, // 0xF4 ô
            0, // unused
            556, // 0xF6 ö
            549, // 0xF7 ÷
            0, // unused
            556, // 0xF9 ù
            556, // 0xFA ú
            556, // 0xFB û
            556, // 0xFC ü
            500, // 0xFD ý
            0, // unused
            0 // unused
    };

    /**
     * Default character width for Helvetica Bold
     */
    static final char HELVETICA_BOLD_DEFAULT_WIDTH = 611;

    /**
     * Character widths for Helvetica Bold (range 0x20 to 0x7f)
     */
    static final char[] HELVETICA_BOLD_20_7F = {
            278, // 0x20
            333, // 0x21 !
            474, // 0x22 "
            556, // 0x23 #
            556, // 0x24 $
            889, // 0x25 %
            722, // 0x26 &
            238, // 0x27 '
            333, // 0x28 (
            333, // 0x29 )
            389, // 0x2A *
            584, // 0x2B +
            278, // 0x2C ,
            333, // 0x2D -
            278, // 0x2E .
            278, // 0x2F /
            556, // 0x30 0
            556, // 0x31 1
            556, // 0x32 2
            556, // 0x33 3
            556, // 0x34 4
            556, // 0x35 5
            556, // 0x36 6
            556, // 0x37 7
            556, // 0x38 8
            556, // 0x39 9
            333, // 0x3A :
            333, // 0x3B ;
            584, // 0x3C <
            584, // 0x3D =
            584, // 0x3E >
            611, // 0x3F ?
            975, // 0x40 @
            722, // 0x41 A
            722, // 0x42 B
            722, // 0x43 C
            722, // 0x44 D
            667, // 0x45 E
            611, // 0x46 F
            778, // 0x47 G
            722, // 0x48 H
            278, // 0x49 I
            556, // 0x4A J
            722, // 0x4B K
            611, // 0x4C L
            833, // 0x4D M
            722, // 0x4E N
            778, // 0x4F O
            667, // 0x50 P
            778, // 0x51 Q
            722, // 0x52 R
            667, // 0x53 S
            611, // 0x54 T
            722, // 0x55 U
            667, // 0x56 V
            944, // 0x57 W
            667, // 0x58 X
            667, // 0x59 Y
            611, // 0x5A Z
            333, // 0x5B [
            278, // 0x5C \
            333, // 0x5D ]
            0, // unused
            556, // 0x5F _
            333, // 0x60 `
            556, // 0x61 a
            611, // 0x62 b
            556, // 0x63 c
            611, // 0x64 d
            556, // 0x65 e
            333, // 0x66 f
            611, // 0x67 g
            611, // 0x68 h
            278, // 0x69 i
            278, // 0x6A j
            556, // 0x6B k
            278, // 0x6C l
            889, // 0x6D m
            611, // 0x6E n
            611, // 0x6F o
            611, // 0x70 p
            611, // 0x71 q
            389, // 0x72 r
            556, // 0x73 s
            333, // 0x74 t
            611, // 0x75 u
            556, // 0x76 v
            778, // 0x77 w
            556, // 0x78 x
            556, // 0x79 y
            500, // 0x7A z
            389, // 0x7B {
            0, // unused
            389, // 0x7D }
            584, // 0x7E ~
            0 // unused
    };

    /**
     * Character widths for Helvetica Bold (range 0xa0 to 0xff)
     */
    static final char[] HELVETICA_BOLD_A0_FF = {
            0, // unused
            0, // unused
            0, // unused
            556, // 0xA3 £
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            333, // 0xB4 ´
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            722, // 0xC0 À
            722, // 0xC1 Á
            722, // 0xC2 Â
            0, // unused
            722, // 0xC4 Ä
            0, // unused
            0, // unused
            722, // 0xC7 Ç
            667, // 0xC8 È
            667, // 0xC9 É
            667, // 0xCA Ê
            667, // 0xCB Ë
            278, // 0xCC Ì
            278, // 0xCD Í
            278, // 0xCE Î
            278, // 0xCF Ï
            0, // unused
            722, // 0xD1 Ñ
            778, // 0xD2 Ò
            778, // 0xD3 Ó
            778, // 0xD4 Ô
            0, // unused
            778, // 0xD6 Ö
            0, // unused
            0, // unused
            722, // 0xD9 Ù
            722, // 0xDA Ú
            722, // 0xDB Û
            722, // 0xDC Ü
            0, // unused
            0, // unused
            611, // 0xDF ß
            556, // 0xE0 à
            556, // 0xE1 á
            556, // 0xE2 â
            0, // unused
            556, // 0xE4 ä
            0, // unused
            0, // unused
            556, // 0xE7 ç
            556, // 0xE8 è
            556, // 0xE9 é
            556, // 0xEA ê
            556, // 0xEB ë
            278, // 0xEC ì
            278, // 0xED í
            278, // 0xEE î
            278, // 0xEF ï
            0, // unused
            611, // 0xF1 ñ
            611, // 0xF2 ò
            611, // 0xF3 ó
            611, // 0xF4 ô
            0, // unused
            611, // 0xF6 ö
            549, // 0xF7 ÷
            0, // unused
            611, // 0xF9 ù
            611, // 0xFA ú
            611, // 0xFB û
            611, // 0xFC ü
            556, // 0xFD ý
            0, // unused
            0 // unused
    };

    /**
     * Default character width for Arial Normal
     */
    static final char ARIAL_NORMAL_DEFAULT_WIDTH = 556;

    /**
     * Character widths for Arial Normal (range 0x20 to 0x7f)
     */
    static final char[] ARIAL_NORMAL_20_7F = {
            278, // 0x20
            278, // 0x21 !
            355, // 0x22 "
            556, // 0x23 #
            556, // 0x24 $
            889, // 0x25 %
            667, // 0x26 &
            191, // 0x27 '
            333, // 0x28 (
            333, // 0x29 )
            389, // 0x2A *
            584, // 0x2B +
            278, // 0x2C ,
            333, // 0x2D -
            278, // 0x2E .
            278, // 0x2F /
            556, // 0x30 0
            556, // 0x31 1
            556, // 0x32 2
            556, // 0x33 3
            556, // 0x34 4
            556, // 0x35 5
            556, // 0x36 6
            556, // 0x37 7
            556, // 0x38 8
            556, // 0x39 9
            278, // 0x3A :
            278, // 0x3B ;
            584, // 0x3C <
            584, // 0x3D =
            584, // 0x3E >
            556, // 0x3F ?
            1015, // 0x40 @
            667, // 0x41 A
            667, // 0x42 B
            722, // 0x43 C
            722, // 0x44 D
            667, // 0x45 E
            611, // 0x46 F
            778, // 0x47 G
            722, // 0x48 H
            278, // 0x49 I
            500, // 0x4A J
            667, // 0x4B K
            556, // 0x4C L
            833, // 0x4D M
            722, // 0x4E N
            778, // 0x4F O
            667, // 0x50 P
            778, // 0x51 Q
            722, // 0x52 R
            667, // 0x53 S
            611, // 0x54 T
            722, // 0x55 U
            667, // 0x56 V
            944, // 0x57 W
            667, // 0x58 X
            667, // 0x59 Y
            611, // 0x5A Z
            278, // 0x5B [
            278, // 0x5C \
            278, // 0x5D ]
            0, // unused
            556, // 0x5F _
            333, // 0x60 `
            556, // 0x61 a
            556, // 0x62 b
            500, // 0x63 c
            556, // 0x64 d
            556, // 0x65 e
            278, // 0x66 f
            556, // 0x67 g
            556, // 0x68 h
            222, // 0x69 i
            222, // 0x6A j
            500, // 0x6B k
            222, // 0x6C l
            833, // 0x6D m
            556, // 0x6E n
            556, // 0x6F o
            556, // 0x70 p
            556, // 0x71 q
            333, // 0x72 r
            500, // 0x73 s
            278, // 0x74 t
            556, // 0x75 u
            500, // 0x76 v
            722, // 0x77 w
            500, // 0x78 x
            500, // 0x79 y
            500, // 0x7A z
            334, // 0x7B {
            0, // unused
            334, // 0x7D }
            584, // 0x7E ~
            0 // unused
    };

    /**
     * Character widths for Arial Normal (range 0xa0 to 0xff)
     */
    static final char[] ARIAL_NORMAL_A0_FF = {
            0, // unused
            0, // unused
            0, // unused
            556, // 0xA3 £
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            333, // 0xB4 ´
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            667, // 0xC0 À
            667, // 0xC1 Á
            667, // 0xC2 Â
            0, // unused
            667, // 0xC4 Ä
            0, // unused
            0, // unused
            722, // 0xC7 Ç
            667, // 0xC8 È
            667, // 0xC9 É
            667, // 0xCA Ê
            667, // 0xCB Ë
            278, // 0xCC Ì
            278, // 0xCD Í
            278, // 0xCE Î
            278, // 0xCF Ï
            0, // unused
            722, // 0xD1 Ñ
            778, // 0xD2 Ò
            778, // 0xD3 Ó
            778, // 0xD4 Ô
            0, // unused
            778, // 0xD6 Ö
            0, // unused
            0, // unused
            722, // 0xD9 Ù
            722, // 0xDA Ú
            722, // 0xDB Û
            722, // 0xDC Ü
            0, // unused
            0, // unused
            611, // 0xDF ß
            556, // 0xE0 à
            556, // 0xE1 á
            556, // 0xE2 â
            0, // unused
            556, // 0xE4 ä
            0, // unused
            0, // unused
            500, // 0xE7 ç
            556, // 0xE8 è
            556, // 0xE9 é
            556, // 0xEA ê
            556, // 0xEB ë
            278, // 0xEC ì
            278, // 0xED í
            278, // 0xEE î
            278, // 0xEF ï
            0, // unused
            556, // 0xF1 ñ
            556, // 0xF2 ò
            556, // 0xF3 ó
            556, // 0xF4 ô
            0, // unused
            556, // 0xF6 ö
            549, // 0xF7 ÷
            0, // unused
            556, // 0xF9 ù
            556, // 0xFA ú
            556, // 0xFB û
            556, // 0xFC ü
            500, // 0xFD ý
            0, // unused
            0 // unused
    };

    /**
     * Default character width for Arial Bold
     */
    static final char ARIAL_BOLD_DEFAULT_WIDTH = 611;

    /**
     * Character widths for Arial Bold (range 0x20 to 0x7f)
     */
    static final char[] ARIAL_BOLD_20_7F = {
            278, // 0x20
            333, // 0x21 !
            474, // 0x22 "
            556, // 0x23 #
            556, // 0x24 $
            889, // 0x25 %
            722, // 0x26 &
            238, // 0x27 '
            333, // 0x28 (
            333, // 0x29 )
            389, // 0x2A *
            584, // 0x2B +
            278, // 0x2C ,
            333, // 0x2D -
            278, // 0x2E .
            278, // 0x2F /
            556, // 0x30 0
            556, // 0x31 1
            556, // 0x32 2
            556, // 0x33 3
            556, // 0x34 4
            556, // 0x35 5
            556, // 0x36 6
            556, // 0x37 7
            556, // 0x38 8
            556, // 0x39 9
            333, // 0x3A :
            333, // 0x3B ;
            584, // 0x3C <
            584, // 0x3D =
            584, // 0x3E >
            611, // 0x3F ?
            975, // 0x40 @
            722, // 0x41 A
            722, // 0x42 B
            722, // 0x43 C
            722, // 0x44 D
            667, // 0x45 E
            611, // 0x46 F
            778, // 0x47 G
            722, // 0x48 H
            278, // 0x49 I
            556, // 0x4A J
            722, // 0x4B K
            611, // 0x4C L
            833, // 0x4D M
            722, // 0x4E N
            778, // 0x4F O
            667, // 0x50 P
            778, // 0x51 Q
            722, // 0x52 R
            667, // 0x53 S
            611, // 0x54 T
            722, // 0x55 U
            667, // 0x56 V
            944, // 0x57 W
            667, // 0x58 X
            667, // 0x59 Y
            611, // 0x5A Z
            333, // 0x5B [
            278, // 0x5C \
            333, // 0x5D ]
            0, // unused
            556, // 0x5F _
            333, // 0x60 `
            556, // 0x61 a
            611, // 0x62 b
            556, // 0x63 c
            611, // 0x64 d
            556, // 0x65 e
            333, // 0x66 f
            611, // 0x67 g
            611, // 0x68 h
            278, // 0x69 i
            278, // 0x6A j
            556, // 0x6B k
            278, // 0x6C l
            889, // 0x6D m
            611, // 0x6E n
            611, // 0x6F o
            611, // 0x70 p
            611, // 0x71 q
            389, // 0x72 r
            556, // 0x73 s
            333, // 0x74 t
            611, // 0x75 u
            556, // 0x76 v
            778, // 0x77 w
            556, // 0x78 x
            556, // 0x79 y
            500, // 0x7A z
            389, // 0x7B {
            0, // unused
            389, // 0x7D }
            584, // 0x7E ~
            0 // unused
    };

    /**
     * Character widths for Arial Bold (range 0xa0 to 0xff)
     */
    static final char[] ARIAL_BOLD_A0_FF = {
            0, // unused
            0, // unused
            0, // unused
            556, // 0xA3 £
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            333, // 0xB4 ´
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            722, // 0xC0 À
            722, // 0xC1 Á
            722, // 0xC2 Â
            0, // unused
            722, // 0xC4 Ä
            0, // unused
            0, // unused
            722, // 0xC7 Ç
            667, // 0xC8 È
            667, // 0xC9 É
            667, // 0xCA Ê
            667, // 0xCB Ë
            278, // 0xCC Ì
            278, // 0xCD Í
            278, // 0xCE Î
            278, // 0xCF Ï
            0, // unused
            722, // 0xD1 Ñ
            778, // 0xD2 Ò
            778, // 0xD3 Ó
            778, // 0xD4 Ô
            0, // unused
            778, // 0xD6 Ö
            0, // unused
            0, // unused
            722, // 0xD9 Ù
            722, // 0xDA Ú
            722, // 0xDB Û
            722, // 0xDC Ü
            0, // unused
            0, // unused
            611, // 0xDF ß
            556, // 0xE0 à
            556, // 0xE1 á
            556, // 0xE2 â
            0, // unused
            556, // 0xE4 ä
            0, // unused
            0, // unused
            556, // 0xE7 ç
            556, // 0xE8 è
            556, // 0xE9 é
            556, // 0xEA ê
            556, // 0xEB ë
            278, // 0xEC ì
            278, // 0xED í
            278, // 0xEE î
            278, // 0xEF ï
            0, // unused
            611, // 0xF1 ñ
            611, // 0xF2 ò
            611, // 0xF3 ó
            611, // 0xF4 ô
            0, // unused
            611, // 0xF6 ö
            549, // 0xF7 ÷
            0, // unused
            611, // 0xF9 ù
            611, // 0xFA ú
            611, // 0xFB û
            611, // 0xFC ü
            556, // 0xFD ý
            0, // unused
            0 // unused
    };

    /**
     * Default character width for Liberation Sans Normal
     */
    static final char LIBERATION_SANS_NORMAL_DEFAULT_WIDTH = 556;

    /**
     * Character widths for Liberation Sans Normal (range 0x20 to 0x7f)
     */
    static final char[] LIBERATION_SANS_NORMAL_20_7F = {
            278, // 0x20
            278, // 0x21 !
            355, // 0x22 "
            556, // 0x23 #
            556, // 0x24 $
            889, // 0x25 %
            667, // 0x26 &
            191, // 0x27 '
            333, // 0x28 (
            333, // 0x29 )
            389, // 0x2A *
            584, // 0x2B +
            278, // 0x2C ,
            333, // 0x2D -
            278, // 0x2E .
            278, // 0x2F /
            556, // 0x30 0
            556, // 0x31 1
            556, // 0x32 2
            556, // 0x33 3
            556, // 0x34 4
            556, // 0x35 5
            556, // 0x36 6
            556, // 0x37 7
            556, // 0x38 8
            556, // 0x39 9
            278, // 0x3A :
            278, // 0x3B ;
            584, // 0x3C <
            584, // 0x3D =
            584, // 0x3E >
            556, // 0x3F ?
            1015, // 0x40 @
            667, // 0x41 A
            667, // 0x42 B
            722, // 0x43 C
            722, // 0x44 D
            667, // 0x45 E
            611, // 0x46 F
            778, // 0x47 G
            722, // 0x48 H
            278, // 0x49 I
            500, // 0x4A J
            667, // 0x4B K
            556, // 0x4C L
            833, // 0x4D M
            722, // 0x4E N
            778, // 0x4F O
            667, // 0x50 P
            778, // 0x51 Q
            722, // 0x52 R
            667, // 0x53 S
            611, // 0x54 T
            722, // 0x55 U
            667, // 0x56 V
            944, // 0x57 W
            667, // 0x58 X
            667, // 0x59 Y
            611, // 0x5A Z
            278, // 0x5B [
            278, // 0x5C \
            278, // 0x5D ]
            0, // unused
            556, // 0x5F _
            333, // 0x60 `
            556, // 0x61 a
            556, // 0x62 b
            500, // 0x63 c
            556, // 0x64 d
            556, // 0x65 e
            278, // 0x66 f
            556, // 0x67 g
            556, // 0x68 h
            222, // 0x69 i
            222, // 0x6A j
            500, // 0x6B k
            222, // 0x6C l
            833, // 0x6D m
            556, // 0x6E n
            556, // 0x6F o
            556, // 0x70 p
            556, // 0x71 q
            333, // 0x72 r
            500, // 0x73 s
            278, // 0x74 t
            556, // 0x75 u
            500, // 0x76 v
            722, // 0x77 w
            500, // 0x78 x
            500, // 0x79 y
            500, // 0x7A z
            334, // 0x7B {
            0, // unused
            334, // 0x7D }
            584, // 0x7E ~
            0 // unused
    };

    /**
     * Character widths for Liberation Sans Normal (range 0xa0 to 0xff)
     */
    static final char[] LIBERATION_SANS_NORMAL_A0_FF = {
            0, // unused
            0, // unused
            0, // unused
            556, // 0xA3 £
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            333, // 0xB4 ´
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            667, // 0xC0 À
            667, // 0xC1 Á
            667, // 0xC2 Â
            0, // unused
            667, // 0xC4 Ä
            0, // unused
            0, // unused
            722, // 0xC7 Ç
            667, // 0xC8 È
            667, // 0xC9 É
            667, // 0xCA Ê
            667, // 0xCB Ë
            278, // 0xCC Ì
            278, // 0xCD Í
            278, // 0xCE Î
            278, // 0xCF Ï
            0, // unused
            722, // 0xD1 Ñ
            778, // 0xD2 Ò
            778, // 0xD3 Ó
            778, // 0xD4 Ô
            0, // unused
            778, // 0xD6 Ö
            0, // unused
            0, // unused
            722, // 0xD9 Ù
            722, // 0xDA Ú
            722, // 0xDB Û
            722, // 0xDC Ü
            0, // unused
            0, // unused
            611, // 0xDF ß
            556, // 0xE0 à
            556, // 0xE1 á
            556, // 0xE2 â
            0, // unused
            556, // 0xE4 ä
            0, // unused
            0, // unused
            500, // 0xE7 ç
            556, // 0xE8 è
            556, // 0xE9 é
            556, // 0xEA ê
            556, // 0xEB ë
            278, // 0xEC ì
            278, // 0xED í
            278, // 0xEE î
            278, // 0xEF ï
            0, // unused
            556, // 0xF1 ñ
            556, // 0xF2 ò
            556, // 0xF3 ó
            556, // 0xF4 ô
            0, // unused
            556, // 0xF6 ö
            549, // 0xF7 ÷
            0, // unused
            556, // 0xF9 ù
            556, // 0xFA ú
            556, // 0xFB û
            556, // 0xFC ü
            500, // 0xFD ý
            0, // unused
            0 // unused
    };

    /**
     * Default character width for Liberation Sans Bold
     */
    static final char LIBERATION_SANS_BOLD_DEFAULT_WIDTH = 611;

    /**
     * Character widths for Liberation Sans Bold (range 0x20 to 0x7f)
     */
    static final char[] LIBERATION_SANS_BOLD_20_7F = {
            278, // 0x20
            333, // 0x21 !
            474, // 0x22 "
            556, // 0x23 #
            556, // 0x24 $
            889, // 0x25 %
            722, // 0x26 &
            238, // 0x27 '
            333, // 0x28 (
            333, // 0x29 )
            389, // 0x2A *
            584, // 0x2B +
            278, // 0x2C ,
            333, // 0x2D -
            278, // 0x2E .
            278, // 0x2F /
            556, // 0x30 0
            556, // 0x31 1
            556, // 0x32 2
            556, // 0x33 3
            556, // 0x34 4
            556, // 0x35 5
            556, // 0x36 6
            556, // 0x37 7
            556, // 0x38 8
            556, // 0x39 9
            333, // 0x3A :
            333, // 0x3B ;
            584, // 0x3C <
            584, // 0x3D =
            584, // 0x3E >
            611, // 0x3F ?
            975, // 0x40 @
            722, // 0x41 A
            722, // 0x42 B
            722, // 0x43 C
            722, // 0x44 D
            667, // 0x45 E
            611, // 0x46 F
            778, // 0x47 G
            722, // 0x48 H
            278, // 0x49 I
            556, // 0x4A J
            722, // 0x4B K
            611, // 0x4C L
            833, // 0x4D M
            722, // 0x4E N
            778, // 0x4F O
            667, // 0x50 P
            778, // 0x51 Q
            722, // 0x52 R
            667, // 0x53 S
            611, // 0x54 T
            722, // 0x55 U
            667, // 0x56 V
            944, // 0x57 W
            667, // 0x58 X
            667, // 0x59 Y
            611, // 0x5A Z
            333, // 0x5B [
            278, // 0x5C \
            333, // 0x5D ]
            0, // unused
            556, // 0x5F _
            333, // 0x60 `
            556, // 0x61 a
            611, // 0x62 b
            556, // 0x63 c
            611, // 0x64 d
            556, // 0x65 e
            333, // 0x66 f
            611, // 0x67 g
            611, // 0x68 h
            278, // 0x69 i
            278, // 0x6A j
            556, // 0x6B k
            278, // 0x6C l
            889, // 0x6D m
            611, // 0x6E n
            611, // 0x6F o
            611, // 0x70 p
            611, // 0x71 q
            389, // 0x72 r
            556, // 0x73 s
            333, // 0x74 t
            611, // 0x75 u
            556, // 0x76 v
            778, // 0x77 w
            556, // 0x78 x
            556, // 0x79 y
            500, // 0x7A z
            389, // 0x7B {
            0, // unused
            389, // 0x7D }
            584, // 0x7E ~
            0 // unused
    };

    /**
     * Character widths for Liberation Sans Bold (range 0xa0 to 0xff)
     */
    static final char[] LIBERATION_SANS_BOLD_A0_FF = {
            0, // unused
            0, // unused
            0, // unused
            556, // 0xA3 £
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            333, // 0xB4 ´
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            722, // 0xC0 À
            722, // 0xC1 Á
            722, // 0xC2 Â
            0, // unused
            722, // 0xC4 Ä
            0, // unused
            0, // unused
            722, // 0xC7 Ç
            667, // 0xC8 È
            667, // 0xC9 É
            667, // 0xCA Ê
            667, // 0xCB Ë
            278, // 0xCC Ì
            278, // 0xCD Í
            278, // 0xCE Î
            278, // 0xCF Ï
            0, // unused
            722, // 0xD1 Ñ
            778, // 0xD2 Ò
            778, // 0xD3 Ó
            778, // 0xD4 Ô
            0, // unused
            778, // 0xD6 Ö
            0, // unused
            0, // unused
            722, // 0xD9 Ù
            722, // 0xDA Ú
            722, // 0xDB Û
            722, // 0xDC Ü
            0, // unused
            0, // unused
            611, // 0xDF ß
            556, // 0xE0 à
            556, // 0xE1 á
            556, // 0xE2 â
            0, // unused
            556, // 0xE4 ä
            0, // unused
            0, // unused
            556, // 0xE7 ç
            556, // 0xE8 è
            556, // 0xE9 é
            556, // 0xEA ê
            556, // 0xEB ë
            278, // 0xEC ì
            278, // 0xED í
            278, // 0xEE î
            278, // 0xEF ï
            0, // unused
            611, // 0xF1 ñ
            611, // 0xF2 ò
            611, // 0xF3 ó
            611, // 0xF4 ô
            0, // unused
            611, // 0xF6 ö
            549, // 0xF7 ÷
            0, // unused
            611, // 0xF9 ù
            611, // 0xFA ú
            611, // 0xFB û
            611, // 0xFC ü
            556, // 0xFD ý
            0, // unused
            0 // unused
    };

    /**
     * Default character width for Frutiger Normal.
     */
    static final char FRUTIGER_NORMAL_DEFAULT_WIDTH = 556;

    /**
     * Character widths for Frutiger Normal (range 0x20 to 0x7f)
     */
    static final char[] FRUTIGER_NORMAL_20_7F = {
            278, // 0x20
            389, // 0x21 !
            556, // 0x22 "
            556, // 0x23 #
            556, // 0x24 $
            1000, // 0x25 %
            667, // 0x26 &
            278, // 0x27 '
            278, // 0x28 (
            278, // 0x29 )
            556, // 0x2A *
            600, // 0x2B +
            278, // 0x2C ,
            333, // 0x2D -
            278, // 0x2E .
            278, // 0x2F /
            556, // 0x30 0
            556, // 0x31 1
            556, // 0x32 2
            556, // 0x33 3
            556, // 0x34 4
            556, // 0x35 5
            556, // 0x36 6
            556, // 0x37 7
            556, // 0x38 8
            556, // 0x39 9
            278, // 0x3A :
            278, // 0x3B ;
            600, // 0x3C <
            600, // 0x3D =
            600, // 0x3E >
            500, // 0x3F ?
            800, // 0x40 @
            667, // 0x41 A
            556, // 0x42 B
            667, // 0x43 C
            667, // 0x44 D
            500, // 0x45 E
            444, // 0x46 F
            722, // 0x47 G
            667, // 0x48 H
            222, // 0x49 I
            333, // 0x4A J
            611, // 0x4B K
            444, // 0x4C L
            889, // 0x4D M
            667, // 0x4E N
            722, // 0x4F O
            500, // 0x50 P
            722, // 0x51 Q
            556, // 0x52 R
            500, // 0x53 S
            500, // 0x54 T
            667, // 0x55 U
            611, // 0x56 V
            944, // 0x57 W
            611, // 0x58 X
            611, // 0x59 Y
            500, // 0x5A Z
            278, // 0x5B [
            278, // 0x5C \
            278, // 0x5D ]
            0, // unused
            500, // 0x5F _
            222, // 0x60 `
            500, // 0x61 a
            556, // 0x62 b
            444, // 0x63 c
            556, // 0x64 d
            500, // 0x65 e
            333, // 0x66 f
            556, // 0x67 g
            556, // 0x68 h
            222, // 0x69 i
            222, // 0x6A j
            500, // 0x6B k
            222, // 0x6C l
            833, // 0x6D m
            556, // 0x6E n
            556, // 0x6F o
            556, // 0x70 p
            556, // 0x71 q
            333, // 0x72 r
            389, // 0x73 s
            333, // 0x74 t
            556, // 0x75 u
            444, // 0x76 v
            778, // 0x77 w
            444, // 0x78 x
            444, // 0x79 y
            444, // 0x7A z
            278, // 0x7B {
            0, // unused
            278, // 0x7D }
            600, // 0x7E ~
            0 // unused
    };

    /**
     * Character widths for Frutiger Normal (range 0xa0 to 0xff)
     */
    static final char[] FRUTIGER_NORMAL_A0_FF = {
            0, // unused
            0, // unused
            0, // unused
            556, // 0xA3 £
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            222, // 0xB4 ´
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            667, // 0xC0 À
            667, // 0xC1 Á
            667, // 0xC2 Â
            0, // unused
            667, // 0xC4 Ä
            0, // unused
            0, // unused
            667, // 0xC7 Ç
            500, // 0xC8 È
            500, // 0xC9 É
            500, // 0xCA Ê
            500, // 0xCB Ë
            222, // 0xCC Ì
            222, // 0xCD Í
            222, // 0xCE Î
            222, // 0xCF Ï
            0, // unused
            667, // 0xD1 Ñ
            722, // 0xD2 Ò
            722, // 0xD3 Ó
            722, // 0xD4 Ô
            0, // unused
            722, // 0xD6 Ö
            0, // unused
            0, // unused
            667, // 0xD9 Ù
            667, // 0xDA Ú
            667, // 0xDB Û
            667, // 0xDC Ü
            0, // unused
            0, // unused
            556, // 0xDF ß
            500, // 0xE0 à
            500, // 0xE1 á
            500, // 0xE2 â
            0, // unused
            500, // 0xE4 ä
            0, // unused
            0, // unused
            444, // 0xE7 ç
            500, // 0xE8 è
            500, // 0xE9 é
            500, // 0xEA ê
            500, // 0xEB ë
            222, // 0xEC ì
            222, // 0xED í
            222, // 0xEE î
            222, // 0xEF ï
            0, // unused
            556, // 0xF1 ñ
            556, // 0xF2 ò
            556, // 0xF3 ó
            556, // 0xF4 ô
            0, // unused
            556, // 0xF6 ö
            600, // 0xF7 ÷
            0, // unused
            556, // 0xF9 ù
            556, // 0xFA ú
            556, // 0xFB û
            556, // 0xFC ü
            444, // 0xFD ý
            0, // unused
            0 // unused
    };

    /**
     * Default character width for Frutiger Bold
     */
    static final char FRUTIGER_BOLD_DEFAULT_WIDTH = 611;

    /**
     * Character widths for Frutiger Bold (range 0x20 to 0x7f)
     */
    static final char[] FRUTIGER_BOLD_20_7F = {
            278, // 0x20
            389, // 0x21 !
            481, // 0x22 "
            556, // 0x23 #
            556, // 0x24 $
            1000, // 0x25 %
            722, // 0x26 &
            278, // 0x27 '
            333, // 0x28 (
            333, // 0x29 )
            556, // 0x2A *
            600, // 0x2B +
            278, // 0x2C ,
            333, // 0x2D -
            278, // 0x2E .
            389, // 0x2F /
            556, // 0x30 0
            556, // 0x31 1
            556, // 0x32 2
            556, // 0x33 3
            556, // 0x34 4
            556, // 0x35 5
            556, // 0x36 6
            556, // 0x37 7
            556, // 0x38 8
            556, // 0x39 9
            278, // 0x3A :
            278, // 0x3B ;
            600, // 0x3C <
            600, // 0x3D =
            600, // 0x3E >
            500, // 0x3F ?
            800, // 0x40 @
            722, // 0x41 A
            611, // 0x42 B
            611, // 0x43 C
            722, // 0x44 D
            556, // 0x45 E
            500, // 0x46 F
            722, // 0x47 G
            722, // 0x48 H
            278, // 0x49 I
            389, // 0x4A J
            667, // 0x4B K
            500, // 0x4C L
            944, // 0x4D M
            722, // 0x4E N
            778, // 0x4F O
            556, // 0x50 P
            778, // 0x51 Q
            611, // 0x52 R
            556, // 0x53 S
            556, // 0x54 T
            722, // 0x55 U
            667, // 0x56 V
            1000, // 0x57 W
            667, // 0x58 X
            667, // 0x59 Y
            556, // 0x5A Z
            333, // 0x5B [
            389, // 0x5C \
            333, // 0x5D ]
            0, // unused
            500, // 0x5F _
            278, // 0x60 `
            556, // 0x61 a
            611, // 0x62 b
            444, // 0x63 c
            611, // 0x64 d
            556, // 0x65 e
            389, // 0x66 f
            611, // 0x67 g
            611, // 0x68 h
            278, // 0x69 i
            278, // 0x6A j
            556, // 0x6B k
            278, // 0x6C l
            889, // 0x6D m
            611, // 0x6E n
            611, // 0x6F o
            611, // 0x70 p
            611, // 0x71 q
            389, // 0x72 r
            444, // 0x73 s
            389, // 0x74 t
            611, // 0x75 u
            556, // 0x76 v
            889, // 0x77 w
            556, // 0x78 x
            556, // 0x79 y
            500, // 0x7A z
            333, // 0x7B {
            0, // unused
            333, // 0x7D }
            600, // 0x7E ~
            0 // unused
    };

    /**
     * Character widths for Frutiger Bold (range 0xa0 to 0xff)
     */
    static final char[] FRUTIGER_BOLD_A0_FF = {
            0, // unused
            0, // unused
            0, // unused
            556, // 0xA3 £
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            278, // 0xB4 ´
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            0, // unused
            722, // 0xC0 À
            722, // 0xC1 Á
            722, // 0xC2 Â
            0, // unused
            722, // 0xC4 Ä
            0, // unused
            0, // unused
            611, // 0xC7 Ç
            556, // 0xC8 È
            556, // 0xC9 É
            556, // 0xCA Ê
            556, // 0xCB Ë
            278, // 0xCC Ì
            278, // 0xCD Í
            278, // 0xCE Î
            278, // 0xCF Ï
            0, // unused
            722, // 0xD1 Ñ
            778, // 0xD2 Ò
            778, // 0xD3 Ó
            778, // 0xD4 Ô
            0, // unused
            778, // 0xD6 Ö
            0, // unused
            0, // unused
            722, // 0xD9 Ù
            722, // 0xDA Ú
            722, // 0xDB Û
            722, // 0xDC Ü
            0, // unused
            0, // unused
            611, // 0xDF ß
            556, // 0xE0 à
            556, // 0xE1 á
            556, // 0xE2 â
            0, // unused
            556, // 0xE4 ä
            0, // unused
            0, // unused
            444, // 0xE7 ç
            556, // 0xE8 è
            556, // 0xE9 é
            556, // 0xEA ê
            556, // 0xEB ë
            278, // 0xEC ì
            278, // 0xED í
            278, // 0xEE î
            278, // 0xEF ï
            0, // unused
            611, // 0xF1 ñ
            611, // 0xF2 ò
            611, // 0xF3 ó
            611, // 0xF4 ô
            0, // unused
            611, // 0xF6 ö
            600, // 0xF7 ÷
            0, // unused
            611, // 0xF9 ù
            611, // 0xFA ú
            611, // 0xFB û
            611, // 0xFC ü
            556, // 0xFD ý
            0, // unused
            0 // unused
    };
}

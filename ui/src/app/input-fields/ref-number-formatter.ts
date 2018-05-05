/*
 * Swiss QR Bill Generator
 * Copyright (c) 2018 Manuel Bleichenbacher
 * Licensed under MIT License
 * https://opensource.org/licenses/MIT
 */

import { InputFormatter } from './input-formatter';
import { Injectable } from '@angular/core';

/** Formatter for refrence numbers (ISO11659 creditor reference or QR reference number) */
@Injectable()
export class ReferenceNumberFormatter implements InputFormatter<string> {
    rawValue(formattedValue: string) {
        if (!formattedValue) {
            return null;
        }
        return formattedValue.replace(/\s/g, '');
    }

    formattedValue(rawValue: string): string {
        if (!rawValue) {
            return null;
        }

        rawValue = rawValue.replace(/\s/g, '');
        let formatted = '';

        if (rawValue.startsWith('RF')) {
            // groups of 4 digits, starting on the left hand side
            const len = rawValue.length;
            for (let p = 0; p < len; p += 4) {
                const e = p + 4 <= len ? p + 4 : len;
                formatted += rawValue.substring(p, p + 4);
                if (e < len) {
                    formatted += ' ';
                }
            }
        } else {
            // groups of 5 characters, starting at the end
            const len = rawValue.length;
            let t = 0;
            while (t < len) {
                const n = t + (len - t - 1) % 5 + 1;
                if (t !== 0) {
                    formatted += ' ';
                }
                formatted += rawValue.substring(t, n);
                t = n;
            }
        }

        return formatted;
    }
}

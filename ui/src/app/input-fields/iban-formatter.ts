/*
 * Swiss QR Bill Generator
 * Copyright (c) 2018 Manuel Bleichenbacher
 * Licensed under MIT License
 * https://opensource.org/licenses/MIT
 */

import { InputFormatter } from './input-formatter';
import { Injectable } from '@angular/core';

/** Formatter for IBAN account number */
@Injectable()
export class IBANFormatter implements InputFormatter<string> {
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
    const len = rawValue.length;
    for (let p = 0; p < len; p += 4) {
      const e = p + 4 <= len ? p + 4 : len;
      formatted += rawValue.substring(p, p + 4);
      if (e < len) {
        formatted += ' ';
      }
    }
    return formatted;
  }
}

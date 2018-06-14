/*
 * Swiss QR Bill Generator
 * Copyright (c) 2018 Manuel Bleichenbacher
 * Licensed under MIT License
 * https://opensource.org/licenses/MIT
 */

import { InputFormatter } from './input-formatter';
import { Injectable } from '@angular/core';

/** Formatter for amount (thousand's separator and rounded to two fractional digits) */
@Injectable()
export class AmountFormatter implements InputFormatter<number> {
  private language: string;
  private systemDecimalSeparator: string;
  private userDecimalSeparator: string;
  private cleaner: RegExp;

  constructor() {
    this.setLanguage('de-CH');
  }

  private static rounded(rawValue: number): number {
    // avoid 'toFixed()' for rounded as it is buggy
    return Math.round(rawValue * 100) / 100;
  }

  public setLanguage(language: string) {
    this.language = language;
    this.systemDecimalSeparator = (1.1).toFixed(1).substring(1, 2);
    this.userDecimalSeparator = (1.1)
      .toLocaleString(this.language)
      .substring(1, 2);
    this.cleaner = new RegExp('[^0-9' + this.userDecimalSeparator + ']', 'g');
  }

  rawValue(formattedValue: string): number {
    if (!formattedValue) {
      return null;
    }
    let cleanedValue = formattedValue.replace(this.cleaner, '');
    if (this.userDecimalSeparator !== this.systemDecimalSeparator) {
      cleanedValue = cleanedValue.replace(
        this.userDecimalSeparator,
        this.systemDecimalSeparator
      );
    }
    const num = Number(cleanedValue);
    return AmountFormatter.rounded(num);
  }

  formattedValue(rawValue: number): string {
    if (!rawValue) {
      return '';
    }
    const n = AmountFormatter.rounded(rawValue);
    return n.toLocaleString(this.language, { minimumFractionDigits: 2 });
  }

  editValue(rawValue: number): string {
    if (!rawValue) {
      return '';
    }
    const n = AmountFormatter.rounded(rawValue);
    return n
      .toLocaleString(this.language, { minimumFractionDigits: 2 })
      .replace(this.cleaner, '');
  }
}

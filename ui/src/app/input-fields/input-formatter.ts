/*
 * Swiss QR Bill Generator
 * Copyright (c) 2018 Manuel Bleichenbacher
 * Licensed under MIT License
 * https://opensource.org/licenses/MIT
 */

 /**
  * Formats the value displayed in a `InputWithFormatDirective` input field.
  */
export interface InputFormatter<T> {
    /** Remove the formatting and return the raw value */
    rawValue(formattedValue: string): T;

    /** Apply the formatting */
    formattedValue(rawValue: T): string;

    /** Provide the value while input field is in focus (if different than formatted value) */
    editValue?(rawValue: T): string;
}

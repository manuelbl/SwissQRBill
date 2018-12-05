/*
 * Swiss QR Bill Generator
 * Copyright (c) 2018 Manuel Bleichenbacher
 * Licensed under MIT License
 * https://opensource.org/licenses/MIT
 */

import { PaymentValidationError } from './payment-validation-error';

export class Payments {
  private static MOD_10 = [0, 9, 4, 6, 8, 2, 7, 1, 3, 5];

  public static createISO11649(rawReference: string): string {
    rawReference = Payments.whiteSpaceRemoved(rawReference);
    const modulo = Payments.calculateMod97('RF00' + rawReference);
    return 'RF' + ('00' + (98 - modulo)).slice(-2) + rawReference;
  }

  private static whiteSpaceRemoved(str: string): string {
    return str.replace(/\s/g, '');
  }

  private static calculateMod97(reference: string): number {
    const rearranged = reference.substring(4) + reference.substring(0, 4);
    const len = rearranged.length;
    let sum = 0;
    for (let i = 0; i < len; i++) {
      const cc = rearranged.charCodeAt(i);
      if (cc >= 48 && cc <= 57) {
        sum = sum * 10 + (cc - 48);
      } else if (cc >= 65 && cc <= 90) {
        sum = sum * 100 + (cc - 65 + 10);
      } else if (cc >= 97 && cc <= 122) {
        sum = sum * 100 + (cc - 97 + 10);
      } else {
        throw new PaymentValidationError(
          'Invalid character in reference: ' + reference
        );
      }
      if (sum > 9999999) {
        sum = sum % 97;
      }
    }

    sum = sum % 97;
    return sum;
  }

  public static createQRReference(reference: string): string {
    reference = Payments.whiteSpaceRemoved(reference);
    if (reference.length > 26) {
      throw new PaymentValidationError(
        'Reference number too long: ' + reference
      );
    }
    const mod10 = Payments.calcMod10(reference);
    return reference + String.fromCharCode(48 + mod10);
  }

  private static calcMod10(reference: string): number {
    let carry = 0;
    const len = reference.length;
    if (reference.replace(/\D/g, '') !== reference) {
      throw new PaymentValidationError(
        'Invalid character in reference number: ' + reference
      );
    }

    for (let i = 0; i < len; i++) {
      const digit = reference.charCodeAt(i) - 48;
      carry = Payments.MOD_10[(carry + digit) % 10];
    }
    return (10 - carry) % 10;
  }

  public static isQRIBAN(account: string): boolean {
    account = Payments.whiteSpaceRemoved(account);
    return account.length > 6
      && (account.substring(4, 6) === '30' || account.substring(4, 6) === '31');
  }
}

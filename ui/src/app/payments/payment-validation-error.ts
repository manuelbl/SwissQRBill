/*
 * Swiss QR Bill Generator
 * Copyright (c) 2018 Manuel Bleichenbacher
 * Licensed under MIT License
 * https://opensource.org/licenses/MIT
 */

export class PaymentValidationError extends Error {
  constructor(message?: string) {
    super(message);
  }
}

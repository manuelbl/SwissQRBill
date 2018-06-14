//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { QrBill } from './qrbill';
import { ValidationMessage } from './validation-message';

export class ValidationResponse {
  valid: boolean;
  validationMessages: ValidationMessage[];
  validatedBill: QrBill;
  billID?: string;
  qrCodeText?: string;
}

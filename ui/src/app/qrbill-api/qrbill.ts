//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { Address } from './address';
import { BillFormat } from './bill-format';

export class QrBill {
  version = 'V2_0';
  amount?: number;
  currency?: string;
  account?: string;
  creditor: Address;
  reference?: string;
  unstructuredMessage?: string;
  billInformation?: string;
  debtor?: Address;
  format?: BillFormat;

  static clone(bill: QrBill): QrBill {
    return {
      version: bill.version,
      amount: bill.amount,
      currency: bill.currency,
      account: bill.account,
      creditor: Address.clone(bill.creditor),
      reference: bill.reference,
      unstructuredMessage: bill.unstructuredMessage,
      billInformation: bill.billInformation,
      debtor: Address.clone(bill.debtor),
      format: BillFormat.clone(bill.format)
    };
  }
}

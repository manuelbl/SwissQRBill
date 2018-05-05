//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { Address } from './address';

export class QrBill {
    language = 'en';
    version = 'V1_0';
    amount?: number;
    currency?: string;
    account?: string;
    creditor: Address;
    finalCreditor?: Address;
    referenceNo?: string;
    additionalInfo?: string;
    debtor?: Address;
    dueDate?: string;

    static clone(bill: QrBill): QrBill {
        return {
            language: bill.language,
            version: bill.version,
            amount: bill.amount,
            currency: bill.currency,
            account: bill.account,
            creditor: Address.clone(bill.creditor),
            finalCreditor: Address.clone(bill.finalCreditor),
            referenceNo: bill.referenceNo,
            additionalInfo: bill.additionalInfo,
            debtor: Address.clone(bill.debtor),
            dueDate: bill.dueDate
        };
    }
}

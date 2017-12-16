import { Address } from './address';

export class QrBill {
    language: string = "en";
    version: string = "V1_0";
    amount?: number;
    currency?: string;
    account?: string;
    creditor: Address;
    finalCreditor?: Address;
    referenceNo?: string;
    additionalInfo?: string;
    debtor?: Address
    dueDate?: string;
}

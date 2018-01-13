//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { QrBill } from "../qrbill-api/qrbill";

export class Example {
    bill: QrBill;
    billID: string;

    constructor(bill: QrBill, billID: string) {
        this.bill = bill;
        this.billID = billID;
    }

    get title(): string {
        if (this.bill.creditor && this.bill.creditor.name) {
            return this.bill.creditor.name;
        }
        return "No creditor yet";
    }
}

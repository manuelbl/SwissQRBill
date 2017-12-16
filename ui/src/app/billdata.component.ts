import { Component, OnInit, Pipe } from '@angular/core';
import { FormControl, FormGroup, FormBuilder, FormGroupDirective, NgForm, Validators } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { Address } from './qrbill-api/address';
import { QrBill } from './qrbill-api/qrbill';

@Component({
  selector: 'bill-data',
  templateUrl: './billdata.component.html',
  styleUrls: ['./billdata.component.css']
})
export class BillData implements OnInit {
  public bill: QrBill;
  public billForm: FormGroup;

  constructor(private formBuilder: FormBuilder) {
    this.bill = {
      language: "en",
      version: "V1_0",
      currency: "CHF",
      amount: 0,
      account: "CH93 0076 2011 6238 5295 7",
      creditor: {
        name: "Lea Simmen",
        street: "Weinbergstrasse",
        houseNo: "31",
        postalCode: "5502",
        town: "Hunzenschwil",
        countryCode: "CH"
      }
    };
  }

  ngOnInit() {
    this.billForm = this.formBuilder.group({
      account: [this.bill.account, [Validators.required, Validators.pattern('[A-Z0-9 ]{5,26}')]],
      creditor: this.formBuilder.group({
        name: [this.bill.creditor.name, [Validators.required, Validators.maxLength(70)]],
        street: [this.bill.creditor.street, [Validators.maxLength(70)]],
        houseNo: [this.bill.creditor.houseNo, [Validators.maxLength(16)]],
        countryCode: [this.bill.creditor.countryCode, [Validators.required, Validators.pattern('[A-Z]{2}')]],
        postalCode: [this.bill.creditor.postalCode, [Validators.required, Validators.maxLength(16)]],
        town: [this.bill.creditor.town, [Validators.required, Validators.maxLength(35)]]
      })
    });
  }

  download(model: FormGroup) {
    let bill = model.value as QrBill;
    console.log(bill);
  }
}


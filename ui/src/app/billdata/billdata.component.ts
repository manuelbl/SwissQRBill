//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { Component, OnInit, Pipe } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, FormBuilder, FormGroupDirective, NgForm, Validators } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialog } from '@angular/material';
import { ValidationErrors } from '@angular/forms/src/directives/validators';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/first';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/debounceTime';
import { QrBillService } from '../qrbill.service';
import { Address } from '../qrbill-api/address';
import { QrBill } from '../qrbill-api/qrbill';
import { ValidationMessage } from '../qrbill-api/validation-message';
import { ValidationResponse } from '../qrbill-api/validation-response';
import { PreviewComponent } from '../preview/preview.component';

@Component({
  selector: 'bill-data',
  templateUrl: './billdata.component.html',
  styleUrls: ['./billdata.component.css']
})
export class BillData implements OnInit {
  public bill: QrBill;
  public billForm: FormGroup;
  private validatedBill: QrBill;
  private billID: string;
  private validationInProgress: number = 0;
  private previewPressed: boolean = false;
  

  constructor(private formBuilder: FormBuilder, private qrBillService: QrBillService,
    private dialog: MatDialog) {
    this.bill = {
      language: "en",
      version: "V1_0",
      currency: "CHF",
      amount: 100,
      account: "CH93 0076 2011 6238 5295 7",
      creditor: {
        name: "Lea Simmen",
        street: "Weinbergstrasse",
        houseNo: "31",
        postalCode: "5502",
        town: "Hunzenschwil",
        countryCode: "CH"
      },
      finalCreditor: {
      },
      additionalInfo: "",
      referenceNo: "",
      debtor: {
      },
      dueDate: "2018-03-31"
    };
  }

  ngOnInit() {
    // The below validations are only for a quick feedback. The real validation is performed server-side and
    // only server-side messages are displayed.
    this.billForm = new FormGroup({
      account: new FormControl(this.bill.account, { validators: [Validators.required, Validators.pattern('[A-Z0-9 ]{5,26}')]}),
      creditor: new FormGroup({
        name: new FormControl(this.bill.creditor.name, { validators: Validators.required}),
        street: new FormControl(this.bill.creditor.street),
        houseNo: new FormControl(this.bill.creditor.houseNo),
        countryCode: new FormControl(this.bill.creditor.countryCode, { validators: [Validators.required, Validators.pattern('[A-Z]{2}')]}),
        postalCode: new FormControl(this.bill.creditor.postalCode, { validators: Validators.required}),
        town: new FormControl(this.bill.creditor.town, { validators: Validators.required})
      }),
      finalCreditor: this.formBuilder.group({
        name: new FormControl(this.bill.finalCreditor.name),
        street: new FormControl(this.bill.finalCreditor.street),
        houseNo: new FormControl(this.bill.finalCreditor.houseNo),
        countryCode: new FormControl(this.bill.finalCreditor.countryCode, { validators: Validators.pattern('[A-Z]{2}')}),
        postalCode: new FormControl(this.bill.finalCreditor.postalCode),
        town: new FormControl(this.bill.finalCreditor.town)
      }),
      currency: new FormControl(this.bill.currency, { validators: [Validators.required, Validators.pattern('[A-Z]{3}')]}),
      amount: new FormControl(this.bill.amount, { validators: [Validators.required, Validators.min(0.01), Validators.max(999999999.99)]}),
      referenceNo: new FormControl(this.bill.referenceNo),
      additionalInfo: new FormControl(this.bill.additionalInfo),
      debtor: this.formBuilder.group({
        name: new FormControl(this.bill.debtor.name),
        street: new FormControl(this.bill.debtor.street),
        houseNo: new FormControl(this.bill.debtor.houseNo),
        countryCode: new FormControl(this.bill.debtor.countryCode, { validators: Validators.pattern('[A-Z]{2}')}),
        postalCode: new FormControl(this.bill.debtor.postalCode),
        town: new FormControl(this.bill.debtor.town)
      }),
      dueDate: new FormControl(this.bill.dueDate)
    }, {
      updateOn: 'blur'
    });

    this.billForm.valueChanges
      .subscribe(val => this.validateServerSide(val));
  }

  validateServerSide(value: any) {
    this.validationInProgress++;
    let bill = this.getBill(value);
    return this.qrBillService.validate(bill)
      .subscribe(response => this.updateServerSideErrors(response));
  }

  updateServerSideErrors(response: ValidationResponse) {
    this.validatedBill = response.validatedBill;
    this.billID = response.billID;
    this.validationInProgress--;

    this.clearServerSideErrors(this.billForm);
    if (response.validationMessages) {
      let messages = response.validationMessages;
      for (let msg of messages) {
        let control = this.billForm.get(msg.field);
        let errors = control.errors;
        if (!errors)
          errors = { };
        errors["serverSide"] = msg.message;
        control.setErrors(errors);
      }
    }

    if (this.previewPressed)
      this.openPreview();
  }

  clearServerSideErrors(group: FormGroup) {
    for (let controlName in group.controls) {
      let control: AbstractControl = group.get(controlName);
      if (control instanceof FormGroup) {
        this.clearServerSideErrors(control as FormGroup);
      } else {
        if (control.hasError("serverSide")) {
          let errors = control.errors;
          delete errors.serverSide;
          control.setErrors(errors);
        }
      }
    }
  }

  preview(model: FormGroup) {
    if (!this.billID)
      this.validateServerSide(this.billForm.value);
    if (this.validationInProgress > 0) {
      this.previewPressed = true;
      return;
    }

    this.openPreview();
  }

  private openPreview() {
    if (!this.billForm.valid)
      return;

    this.previewPressed = false;
    this.dialog.open(PreviewComponent, {
      width: '100vw',
      height: '100vh',
      data: {
        validatedBill: this.validatedBill,
        billID: this.billID
      }
    });
  }

  getBill(value: any): QrBill {
    if (value.dueDate instanceof Date) {
      let dueDate = value.dueDate as Date;
      value.dueDate = dueDate.toISOString().substring(0, 10);
    }
    return value as QrBill;
  }
}


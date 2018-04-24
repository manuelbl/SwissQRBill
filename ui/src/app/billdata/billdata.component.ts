//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material';
import { TranslateService, LangChangeEvent } from '@ngx-translate/core';
import { QrBillService } from '../qrbill-api/qrbill.service';
import { QrBill } from '../qrbill-api/qrbill';
import { ValidationResponse } from '../qrbill-api/validation-response';
import { PreviewComponent } from '../preview/preview.component';
import { BillSingletonService } from '../bill-singleton-service/bill-singleton.service';
import { IBANFormatter } from '../input-fields/iban-formatter';

@Component({
  selector: 'bill-data',
  templateUrl: './billdata.component.html',
  styleUrls: ['./billdata.component.css'],
  providers: [ IBANFormatter ]
})
export class BillData implements OnInit {
  public bill: QrBill;
  public outputSize: string;
  public billForm: FormGroup;
  private validatedBill: QrBill;
  private billID: string;
  private validationInProgress: number = 0;
  private previewPressed: boolean = false;


  constructor(private formBuilder: FormBuilder, private qrBillService: QrBillService,
      private dialog: MatDialog, private translate: TranslateService,
      private billSingleton: BillSingletonService, protected ibanFormatter: IBANFormatter) {
    this.bill = billSingleton.getBill();
    this.outputSize = "a6-landscape";
  }

  ngOnInit() {
    // The below validations are only for a quick feedback. The real validation is performed server-side and
    // only server-side messages are displayed.
    this.billForm = new FormGroup({
      account: new FormControl(this.bill.account, { validators: [Validators.required, Validators.pattern('[A-Za-z0-9 ]{5,30}')]}),
      creditor: new FormGroup({
        name: new FormControl(this.bill.creditor.name, { validators: Validators.required}),
        street: new FormControl(this.bill.creditor.street),
        houseNo: new FormControl(this.bill.creditor.houseNo),
        countryCode: new FormControl(this.bill.creditor.countryCode, { validators: [Validators.required, Validators.pattern('[A-Za-z]{2}')]}),
        postalCode: new FormControl(this.bill.creditor.postalCode, { validators: Validators.required}),
        town: new FormControl(this.bill.creditor.town, { validators: Validators.required})
      }),
      finalCreditor: this.formBuilder.group({
        name: new FormControl(this.bill.finalCreditor.name),
        street: new FormControl(this.bill.finalCreditor.street),
        houseNo: new FormControl(this.bill.finalCreditor.houseNo),
        countryCode: new FormControl(this.bill.finalCreditor.countryCode, { validators: Validators.pattern('[A-Za-z]{2}')}),
        postalCode: new FormControl(this.bill.finalCreditor.postalCode),
        town: new FormControl(this.bill.finalCreditor.town)
      }),
      currency: new FormControl(this.bill.currency, { validators: [Validators.required, Validators.pattern('[A-Za-z]{3}')]}),
      amount: new FormControl(this.bill.amount, { validators: [Validators.min(0.01), Validators.max(999999999.99)]}),
      referenceNo: new FormControl(this.bill.referenceNo, { validators: [Validators.pattern('[A-Za-z0-9 ]{5,40}')]}),
      additionalInfo: new FormControl(this.bill.additionalInfo),
      language: new FormControl(this.bill.language),
      outputSize: new FormControl(this.outputSize),
      debtor: this.formBuilder.group({
        name: new FormControl(this.bill.debtor.name),
        street: new FormControl(this.bill.debtor.street),
        houseNo: new FormControl(this.bill.debtor.houseNo),
        countryCode: new FormControl(this.bill.debtor.countryCode, { validators: Validators.pattern('[A-Za-z]{2}')}),
        postalCode: new FormControl(this.bill.debtor.postalCode),
        town: new FormControl(this.bill.debtor.town)
      }),
      dueDate: new FormControl(this.bill.dueDate)
    }, {
      updateOn: 'blur'
    });

    this.billForm.valueChanges
      .subscribe(val => this.validateServerSide(val));

    this.translate.onLangChange.subscribe((params: LangChangeEvent) => {
      this.validateServerSide(this.billForm.value);
    });
  }

  // Send data to server for validation
  validateServerSide(value: any) {
    this.validationInProgress++;
    let bill = this.getBill(value);
    return this.qrBillService.validate(bill, this.translate.currentLang)
      .subscribe(response => this.updateServerSideErrors(response));
  }

  // Use the validation response to update the error messages in the UI
  updateServerSideErrors(response: ValidationResponse) {
    this.validatedBill = response.validatedBill;
    this.billID = response.billID;
    this.validationInProgress--;

    this.clearServerSideErrors(this.billForm);

    let messages = response.validationMessages;
    if (messages) {
      for (let msg of messages) {
        if (msg.type === "Error") {
          let control = this.billForm.get(msg.field);
          let errors = control.errors;
          if (!errors)
            errors = { };
          errors["serverSide"] = msg.message;
          control.setErrors(errors);
        }
      }
    }

    if (messages) {
      // TODO: set focus to first field with error
      if (this.previewPressed && this.validationInProgress == 0)
      this.previewPressed = false;
    } else {
      // user clicked on "Preview" and is waiting for validation
      if (this.previewPressed && this.validationInProgress == 0)
        this.openPreview();
    }
  }

  // Remove the errors of type "serverSide"
  clearServerSideErrors(group: FormGroup) {
    for (let controlName in group.controls) {
      let control = group.get(controlName);
      if (control instanceof FormGroup) {
        this.clearServerSideErrors(control as FormGroup);
      } else {
        if (control.hasError("serverSide")) {
          let errors = control.errors;
          delete errors.serverSide;
          if (Object.keys(errors).length === 0)
            errors = null;
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
      maxWidth: '100vw',
      maxHeight: '100vh',
      data: {
        validatedBill: this.validatedBill,
        billID: this.billID,
        outputSize: this.billForm.value.outputSize
      }
    });
  }

  getBill(value: any): QrBill {
    if (value.dueDate instanceof Date) {
      let dueDate = value.dueDate as Date;
      value.dueDate = dueDate.getFullYear() + '-' + this._2digit(dueDate.getMonth() + 1)
        + '-' + this._2digit(dueDate.getDate());
    }
    return value as QrBill;
  }

  private _2digit(n: number) {
    return ('00' + n).slice(-2);
  }
}


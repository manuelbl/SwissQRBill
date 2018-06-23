//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { Component, OnInit, NgZone } from '@angular/core';
import {
  FormControl,
  FormGroup,
  FormBuilder,
  Validators
} from '@angular/forms';
import { MatDialog } from '@angular/material';
import { TranslateService, LangChangeEvent } from '@ngx-translate/core';
import { QrBillService } from '../qrbill-api/qrbill.service';
import { QrBill } from '../qrbill-api/qrbill';
import { ValidationResponse } from '../qrbill-api/validation-response';
import { PreviewComponent } from '../preview/preview.component';
import { BillSingletonService } from '../bill-singleton-service/bill-singleton.service';
import { IBANFormatter } from '../input-fields/iban-formatter';
import { ReferenceNumberFormatter } from '../input-fields/ref-number-formatter';
import { AmountFormatter } from '../input-fields/amount-formatter';
import { Moment, isMoment } from 'moment';
import { Observable, Subscription, Subject } from 'rxjs';
import { Payments } from '../payments/payments';
import { startWith, map } from 'rxjs/operators';

@Component({
  selector: 'qrbill-data',
  templateUrl: './billdata.component.html',
  styleUrls: ['./billdata.component.css'],
  providers: [IBANFormatter, ReferenceNumberFormatter]
})
export class BillDataComponent implements OnInit {
  public bill: QrBill;
  public outputSize: string;
  public billForm: FormGroup;
  public readonly refNoChanges: Subject<string> = new Subject<string>();
  public refNoSuggestions: Observable<string[]>;
  private validatedBill: QrBill;
  private billID: string;
  private validationInProgress = 0;
  private previewPressed = false;

  private static _2digit(n: number) {
    return ('00' + n).slice(-2);
  }

  constructor(
    private formBuilder: FormBuilder,
    private qrBillService: QrBillService,
    private dialog: MatDialog,
    private translate: TranslateService,
    private billSingleton: BillSingletonService,
    public amountFormatter: AmountFormatter,
    public ibanFormatter: IBANFormatter,
    public refNumberFormatter: ReferenceNumberFormatter,
    private ngZone: NgZone
  ) {
    this.bill = billSingleton.getBill();
    this.outputSize = 'a6-landscape';
  }

  ngOnInit() {
    // The below validations are only for a quick feedback. The real validation is performed server-side and
    // only server-side messages are displayed.
    this.billForm = new FormGroup(
      {
        account: new FormControl(this.bill.account, {
          validators: [
            Validators.required,
            Validators.pattern('[A-Za-z0-9 ]{5,30}')
          ]
        }),
        creditor: new FormGroup({
          name: new FormControl(this.bill.creditor.name, {
            validators: Validators.required
          }),
          street: new FormControl(this.bill.creditor.street),
          houseNo: new FormControl(this.bill.creditor.houseNo),
          countryCode: new FormControl(this.bill.creditor.countryCode, {
            validators: [Validators.required, Validators.pattern('[A-Za-z]{2}')]
          }),
          postalCode: new FormControl(this.bill.creditor.postalCode, {
            validators: Validators.required
          }),
          town: new FormControl(this.bill.creditor.town, {
            validators: Validators.required
          })
        }),
        finalCreditor: this.formBuilder.group({
          name: new FormControl(this.bill.finalCreditor.name),
          street: new FormControl(this.bill.finalCreditor.street),
          houseNo: new FormControl(this.bill.finalCreditor.houseNo),
          countryCode: new FormControl(this.bill.finalCreditor.countryCode, {
            validators: Validators.pattern('[A-Za-z]{2}')
          }),
          postalCode: new FormControl(this.bill.finalCreditor.postalCode),
          town: new FormControl(this.bill.finalCreditor.town)
        }),
        currency: new FormControl(this.bill.currency, {
          validators: [Validators.required, Validators.pattern('[A-Za-z]{3}')]
        }),
        amount: new FormControl(this.bill.amount, {
          validators: [Validators.min(0.01), Validators.max(999999999.99)]
        }),
        referenceNo: new FormControl(this.bill.referenceNo, {
          validators: [Validators.pattern('[A-Za-z0-9 ]{5,40}')]
        }),
        additionalInfo: new FormControl(this.bill.additionalInfo),
        language: new FormControl(this.bill.language),
        outputSize: new FormControl(this.outputSize),
        debtor: this.formBuilder.group({
          name: new FormControl(this.bill.debtor.name),
          street: new FormControl(this.bill.debtor.street),
          houseNo: new FormControl(this.bill.debtor.houseNo),
          countryCode: new FormControl(this.bill.debtor.countryCode, {
            validators: Validators.pattern('[A-Za-z]{2}')
          }),
          postalCode: new FormControl(this.bill.debtor.postalCode),
          town: new FormControl(this.bill.debtor.town)
        }),
        dueDate: new FormControl(this.bill.dueDate)
      },
      {
        updateOn: 'blur'
      }
    );

    // Server-side validation on each change
    this.billForm.valueChanges.subscribe(val => this.validateServerSide(val));

    this.translate.onLangChange.subscribe((params: LangChangeEvent) => {
      this.validateServerSide(this.billForm.value);
    });

    this.refNoSuggestions = this.refNoChanges.pipe(
      startWith(''),
      map(val => this.generateRefNos(val))
    );
  }

  // Send data to server for validation
  private validateServerSide(value: any): Subscription {
    this.validationInProgress++;
    this.previewPressed = false;
    const bill = this.getBill(value);
    return this.qrBillService
      .validate(bill, this.translate.currentLang)
      .subscribe(response => this.updateServerSideErrors(response));
  }

  // Use the validation response to update the error messages in the UI
  updateServerSideErrors(response: ValidationResponse) {
    this.validatedBill = response.validatedBill;
    this.billID = response.billID;
    this.validationInProgress--;

    this.clearServerSideErrors(this.billForm);

    const messages = response.validationMessages;
    let controlPath: string;
    if (messages) {
      for (const msg of messages) {
        if (msg.type === 'Error') {
          if (!controlPath) {
            controlPath = msg.field;
          }
          const control = this.billForm.get(msg.field);
          let errors = control.errors;
          if (!errors) {
            errors = {};
          }
          errors['serverSide'] = msg.message;
          control.setErrors(errors);
        }
      }
    }

    // user clicked on "Preview" and is waiting for validation
    if (this.previewPressed && this.validationInProgress === 0) {
      this.previewPressed = false;
      if (messages) {
        this.focusControl(controlPath);
      } else {
        this.openPreview();
      }
    }
  }

  // Remove the errors of type "serverSide"
  private clearServerSideErrors(group: FormGroup) {
    const controls = group.controls;
    for (const controlName of Object.keys(controls)) {
      const control = controls[controlName];
      if (control instanceof FormGroup) {
        this.clearServerSideErrors(control);
      } else if (control.hasError('serverSide')) {
        let errors = control.errors;
        delete errors.serverSide;
        if (Object.keys(errors).length === 0) {
          errors = null;
        }
        control.setErrors(errors);
      }
    }
  }

  // User has clicked "Preview" button
  preview() {
    if (!this.billForm.valid) {
      // focus invalid field
      this.focusControl(this.findFirstInvalidControl());
      return;
    }
    if (!this.billID) {
      // validate data if needed
      this.validateServerSide(this.billForm.value);
    }
    if (this.validationInProgress > 0) {
      // pending validation - postpone opening the preview
      this.previewPressed = true;
      return;
    }

    this.openPreview();
  }

  private openPreview() {
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

  private getBill(value: any): QrBill {
    if (isMoment(value.dueDate)) {
      const dueDate = value.dueDate as Moment;
      value.dueDate = dueDate.toISOString(true).substring(0, 10);
    }
    return value as QrBill;
  }

  private focusControl(controlPath: string) {
    const pathElements = controlPath.split('.');
    const selector =
      'qrbill-data *[ng-reflect-name=' +
      pathElements.join('] *[ng-reflect-name=') +
      ']';
    this.ngZone.runOutsideAngular(() => {
      setTimeout(() => {
        const e = document.querySelector(selector);
        if (e instanceof HTMLElement) {
          e.focus();
        }
      }, 0);
    });
  }

  private generateRefNos(rawReference: string): string[] {
    const suggestions: string[] = [];
    let str = rawReference.toUpperCase();
    str = str.replace(/\W/g, '');
    if (str.startsWith('RF') && str.length > 4) {
      suggestions.push(Payments.createISO11649(str.substring(4)));
    }
    if (str.length > 0 && str.length <= 21) {
      suggestions.push(Payments.createISO11649(str));
    }
    if (str.length > 0 && str.length <= 26 && str.replace(/\D/g, '') === str) {
      suggestions.push(Payments.createQRReference(str));
    }
    return suggestions;
  }

  private findFirstInvalidControl(
    root: FormGroup = this.billForm,
    rootPath: string = ''
  ): string {
    const controls = root.controls;
    for (const field of Object.keys(controls)) {
      const ctrl = controls[field];
      if (ctrl.invalid) {
        if (ctrl instanceof FormGroup) {
          return this.findFirstInvalidControl(ctrl, rootPath + field + '.');
        } else if (ctrl instanceof FormControl) {
          return rootPath + field;
        }
      }
    }
    return null;
  }
}

//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { Injectable } from '@angular/core';
import { QrBill } from '../qrbill-api/qrbill';
import { ExampleService } from '../example-service/example.service';

@Injectable({
  providedIn: 'root'
})
export class BillSingletonService {
  private bill: QrBill;

  constructor(private exampleService: ExampleService) {
    this.setBill(exampleService.getExamples()[0].bill);
  }

  public getBill(): QrBill {
    return this.bill;
  }

  public setBill(bill: QrBill) {
    this.bill = QrBill.clone(bill);
  }
}

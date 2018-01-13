import { Injectable } from '@angular/core';
import { QrBill } from '../qrbill-api/qrbill';
import { ExampleService } from '../example-service/example.service';

@Injectable()
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

import { TestBed, inject } from '@angular/core/testing';

import { BillSingletonService } from './bill-singleton.service';
import { TranslateModule, TranslateLoader, TranslateService } from '@ngx-translate/core';
import { TranslateMockLoader } from '../mock/translate-mock.loader';
import { ExampleService } from '../example-service/example.service';
import { QrBill } from '../qrbill-api/qrbill';

describe('BillSingletonService', () => {
  let singletonService: BillSingletonService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        BillSingletonService,
        ExampleService
      ],
      imports: [
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateMockLoader }
        })
      ]
    });

    singletonService = TestBed.get(BillSingletonService);
  });

  it('should be created', () => {
    expect(singletonService).toBeTruthy();
  });

  it('Service injected via inject(...) and TestBed.get(...) should be the same instance',
    inject([BillSingletonService], (injectService: BillSingletonService) => {
      expect(injectService).toBe(singletonService);
  }));

  it('should retain singleton data', () => {
    let bill: QrBill = {
      creditor: {
        name: "Name-1",
        countryCode: "CH",
        postalCode: "1234",
        town: "Nana"
      }
    };
    singletonService.setBill(bill);

    expect(singletonService.getBill() === bill).toBeFalsy(); // must be copy, not same instance
    expect(singletonService.getBill().creditor.name).toEqual(bill.creditor.name);
    expect(singletonService.getBill().creditor.postalCode).toEqual(bill.creditor.postalCode);
  });
});

//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { TestBed, inject } from '@angular/core/testing';

import { BillSingletonService } from './bill-singleton.service';
import { ExampleService } from '../example-service/example.service';
import { QrBill } from '../qrbill-api/qrbill';

describe('BillSingletonService', () => {
  let singletonService: BillSingletonService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [BillSingletonService, ExampleService]
    });

    singletonService = TestBed.get(BillSingletonService);
  });

  it('should be created', () => {
    expect(singletonService).toBeTruthy();
  });

  it('Service injected via inject(...) and TestBed.get(...) should be the same instance', inject(
    [BillSingletonService],
    (injectService: BillSingletonService) => {
      expect(injectService).toBe(singletonService);
    }
  ));

  it('should retain singleton data', () => {
    const bill: QrBill = {
      version: 'V2_0',
      creditor: {
        name: 'Name-1',
        countryCode: 'CH',
        postalCode: '1234',
        town: 'Nana'
      },
      format: {
        language: 'de'
      }
    };
    singletonService.setBill(bill);

    expect(singletonService.getBill() === bill).toBeFalsy(); // must be copy, not same instance
    expect(singletonService.getBill().creditor.name).toEqual(
      bill.creditor.name
    );
    expect(singletonService.getBill().creditor.postalCode).toEqual(
      bill.creditor.postalCode
    );
  });
});

//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { TestBed, inject } from '@angular/core/testing';
import { HttpClient } from '@angular/common/http';

import { QrBillService } from './qrbill.service';

class HttpClientMock {}

describe('QrBillService', () => {
  let qrBillService: QrBillService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        QrBillService,
        {
          provide: HttpClient,
          useClass: HttpClientMock
        }
      ]
    });

    qrBillService = TestBed.get(QrBillService);
  });

  it('should be created', inject([QrBillService], (service: QrBillService) => {
    expect(service).toBeTruthy();
  }));

  it('Service injected via inject(...) and TestBed.get(...) should be the same instance', inject(
    [QrBillService],
    (injectService: QrBillService) => {
      expect(injectService).toBe(qrBillService);
    }
  ));
});

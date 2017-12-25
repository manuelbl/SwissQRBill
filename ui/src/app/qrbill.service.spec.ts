//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { TestBed, inject } from '@angular/core/testing';

import { QrBillService } from './qrbill.service';

describe('QrbillService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [QrBillService]
    });
  });

  it('should be created', inject([QrBillService], (service: QrBillService) => {
    expect(service).toBeTruthy();
  }));
});

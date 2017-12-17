//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { TestBed, inject } from '@angular/core/testing';

import { QrbillService } from './qrbill.service';

describe('QrbillService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [QrbillService]
    });
  });

  it('should be created', inject([QrbillService], (service: QrbillService) => {
    expect(service).toBeTruthy();
  }));
});

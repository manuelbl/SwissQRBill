//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { TestBed, inject } from '@angular/core/testing';

import { ExampleService } from './example.service';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateMockLoader } from '../mock/translate-mock.loader';

describe('ExampleService', () => {
  let exampleService: ExampleService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateMockLoader }
        })
      ],
      providers: [ExampleService]
    });

    exampleService = TestBed.get(ExampleService);
  });

  it('should be created', () => {
    expect(exampleService).toBeTruthy();
  });

  it('Service injected via inject(...) and TestBed.get(...) should be the same instance',
    inject([ExampleService], (injectService: ExampleService) => {
      expect(injectService).toBe(exampleService);
  }));

  it('should return examples', () => {
    let examples = exampleService.getExamples();
    expect(examples).toBeTruthy();
    expect(examples.length).toBeGreaterThanOrEqual(3);
  });

  it('should return examples with data', () => {
    let examples = exampleService.getExamples();
    let example = examples[2];
    expect(example.bill.creditor).toBeTruthy();
    expect(example.bill.creditor.name).toBeTruthy();
    expect(example.billID.length).toBeGreaterThanOrEqual(100);
  });
});

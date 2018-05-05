//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { TestBed, inject } from '@angular/core/testing';

import { ExampleService } from './example.service';

describe('ExampleService', () => {
  let exampleService: ExampleService;

  beforeEach(() => {
    TestBed.configureTestingModule({
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
    const examples = exampleService.getExamples();
    expect(examples).toBeTruthy();
    expect(examples.length).toBeGreaterThanOrEqual(3);
  });

  it('should return examples with data', () => {
    const examples = exampleService.getExamples();
    const example = examples[2];
    expect(example.bill.creditor).toBeTruthy();
    expect(example.bill.creditor.name).toBeTruthy();
    expect(example.billID.length).toBeGreaterThanOrEqual(100);
  });
});

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
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateMockLoader }
        })
      ],
      providers: [ExampleService]
    });
  });

  it('should be created', inject([ExampleService], (service: ExampleService) => {
    expect(service).toBeTruthy();
  }));
});

import { TestBed, inject } from '@angular/core/testing';

import { BillSingletonService } from './bill-singleton.service';
import { ExampleService } from '../example-service/example.service';
import { TranslateModule, TranslateLoader, TranslateService } from '@ngx-translate/core';
import { TranslateMockLoader } from '../mock/translate-mock.loader';

describe('BillSingletonService', () => {
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
  });

  it('should be created', inject([BillSingletonService], (service: BillSingletonService) => {
    expect(service).toBeTruthy();
  }));
});

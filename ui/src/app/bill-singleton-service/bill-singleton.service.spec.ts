import { TestBed, inject } from '@angular/core/testing';

import { BillSingletonService } from './bill-singleton.service';

describe('BillSingletonService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [BillSingletonService]
    });
  });

  it('should be created', inject([BillSingletonService], (service: BillSingletonService) => {
    expect(service).toBeTruthy();
  }));
});

//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { Inject, Injectable, LOCALE_ID } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { QrBill } from './qrbill';
import { ValidationResponse } from './validation-response';

@Injectable()
export class QrBillService {

  constructor(private http: HttpClient, @Inject(LOCALE_ID) private localeId: string) { }

  validate(bill: QrBill): Observable<ValidationResponse> {
    return this.http.post<ValidationResponse>("api/bill/validate", bill, { headers: { 'Accept-Language': this.localeId } });
  }

}

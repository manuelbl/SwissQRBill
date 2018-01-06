//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { QrBill } from './qrbill';
import { ValidationResponse } from './validation-response';

@Injectable()
export class QrBillService {

  constructor(private http: HttpClient) { }

  validate(bill: QrBill, language: string): Observable<ValidationResponse> {
    return this.http.post<ValidationResponse>("../qrbill-api/bill/validate", bill, { headers: { 'Accept-Language': language } });
  }
}

//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { QrBill } from './qrbill-api/qrbill';
import { ValidationResponse } from './qrbill-api/validation-response';

@Injectable()
export class QrBillService {

  constructor(private http: HttpClient) { }

  validate(bill: QrBill): Observable<ValidationResponse> {
    return this.http.post<ValidationResponse>("/api/validate", bill);
  }

}

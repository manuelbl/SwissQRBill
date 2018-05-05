//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { QrBill } from './qrbill';
import { ValidationResponse } from './validation-response';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class QrBillService {

  constructor(private http: HttpClient) { }

  validate(bill: QrBill, language: string): Observable<ValidationResponse> {
    return this.http.post<ValidationResponse>('../qrbill-api/bill/validate', bill, { headers: { 'Accept-Language': language } });
  }
}

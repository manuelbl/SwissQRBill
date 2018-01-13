//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { Injectable } from '@angular/core';
import { QrBill } from '../qrbill-api/qrbill';
import { TranslateService } from '@ngx-translate/core';
import { Example } from './example';

@Injectable()
export class ExampleService {

  private examples: Example[];

  constructor(private translate: TranslateService) {

    let e1 = new Example(
      {
        language: "de",
        version: "V1_0",
        currency: "CHF",
        amount: 100,
        account: "CH93 0076 2011 6238 5295 7",
        creditor: {
          name: "Lea Simmen",
          street: "Weinbergstrasse",
          houseNo: "31",
          postalCode: "5502",
          town: "Hunzenschwil",
          countryCode: "CH"
        },
        finalCreditor: {
        },
        additionalInfo: "",
        referenceNo: "",
        debtor: {
        },
        dueDate: "2018-03-31"
      },
      "eJx1TssKwkAM_BXJuZXsLtuH14L0IFWo4KWXqqEutCvutiqK_25j8djDzGQSkswb7uS8uVpYiQDa2jZD3RCs4EwQwM3t6dmPrtxllassCkRWwZTlqUKMI4lCRFIlWqY65sGG6kVpuo4suwMZeyTX-N7V3hO31G9fa5Ss-WBfZP3p8jDtdJd5DmOC5RQiy9cs4_skRBVOR-dQbIt_CZ8vBN1TvQ~~"
    );

    let e2 = new Example(
      {
        "account": "CH95 0020 6206 1057 2901 J",
        "creditor": {
          "name": "Gromion SA",
          "street": "Avenue Fornachon",
          "houseNo": "34",
          "countryCode": "CH",
          "postalCode": "2034",
          "town": "Peseux"
        },
        "finalCreditor": {
          "name": null,
          "street": null,
          "houseNo": null,
          "countryCode": null,
          "postalCode": null,
          "town": null
        },
        "currency": "CHF",
        "amount": 371.2,
        "referenceNo": "0 00038 38000 01776",
        "additionalInfo": "",
        "language": "fr",
        "debtor": {
          "name": "Céline Pythoud",
          "street": "Chemin du Grand Record",
          "houseNo": "42",
          "countryCode": "CH",
          "postalCode": "1040",
          "town": "Échallens"
        },
        "dueDate": "2018-04-17"
      },
      "eJx1T0FqwzAQ_IrQOSm7shM5uYVAUnpynR5zEfY2NjgrKkchpfQBfUrfkY_VKx9KD120M-zMMmg_9JXC0HnWa5zp3vEpuhPptX4NeqbfwgvdLuN0KLfHcGRAAGEU2D6uFgAGluNDWFizAnwSYx_8eUxUh41MmytxJLXzgV3dehYtywUNTFzSQPE2RQr-15nFBwPT3m4KwGIO-RxtEu_ffcekyvdL62OTpJbOHasmqn1w3KiKah-Sk5t0B-Qp7_5Vt67viYffXzxXVTr5b2VFVgijtUux9ecPsLtrBQ~~"
    );

    let e3 = new Example(
      {
        language: translate.currentLang,
        version: "V1_0",
        currency: "CHF",
        amount: 100,
        account: "CH93 0076 2011 6238 5295 7",
        creditor: {
          name: "Lea Simmen",
          street: "Weinbergstrasse",
          houseNo: "31",
          postalCode: "5502",
          town: "Hunzenschwil",
          countryCode: "CH"
        },
        finalCreditor: {
        },
        additionalInfo: "",
        referenceNo: "",
        debtor: {
        },
        dueDate: "2018-03-31"
      },
      "eJx1TssKwkAM_BXJuZXsLtuH14L0IFWo4KWXqqEutCvutiqK_25j8djDzGQSkswb7uS8uVpYiQDa2jZD3RCs4EwQwM3t6dmPrtxllassCkRWwZTlqUKMI4lCRFIlWqY65sGG6kVpuo4suwMZeyTX-N7V3hO31G9fa5Ss-WBfZP3p8jDtdJd5DmOC5RQiy9cs4_skRBVOR-dQbIt_CZ8vBN1TvQ~~"
    );

    this.examples = [e1, e2, e3];
  }

  public getExamples(): Example[] {
    return this.examples;
  }

}

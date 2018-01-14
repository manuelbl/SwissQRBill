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
        account: "CH95 0020 6206 1057 2901 J",
        creditor: {
          name: "Gromion SA",
          street: "Avenue Fornachon",
          houseNo: "34",
          countryCode: "CH",
        postalCode: "2034",
          town: "Peseux"
        },
        finalCreditor: {
          name: null,
          street: null,
          houseNo: null,
          countryCode: null,
          postalCode: null,
          town: null
        },
        currency: "CHF",
        amount: 371.2,
        referenceNo: "0 00038 38000 01776",
        additionalInfo: "",
        language: "fr",
        debtor: {
          name: "Céline Pythoud",
          street: "Chemin du Grand Record",
          houseNo: "42",
          countryCode: "CH",
          postalCode: "1040",
          town: "Échallens"
        },
        dueDate: "2018-04-17"
      },
      "eJx1T0FqwzAQ_IrQOSm7shM5uYVAUnpynR5zEfY2NjgrKkchpfQBfUrfkY_VKx9KD120M-zMMmg_9JXC0HnWa5zp3vEpuhPptX4NeqbfwgvdLuN0KLfHcGRAAGEU2D6uFgAGluNDWFizAnwSYx_8eUxUh41MmytxJLXzgV3dehYtywUNTFzSQPE2RQr-15nFBwPT3m4KwGIO-RxtEu_ffcekyvdL62OTpJbOHasmqn1w3KiKah-Sk5t0B-Qp7_5Vt67viYffXzxXVTr5b2VFVgijtUux9ecPsLtrBQ~~"
    );

    let e3 = new Example(
      {
        account: "CH45 0023 0230 9999 9999 A",
        creditor: {
          name: "UBS Switzerland AG",
          street: "Postfach",
          houseNo: "",
          countryCode: "CH",
          postalCode: "8098",
          town: "Zürich"
        },
        finalCreditor: {
          name: "Schreinerei Habegger & Söhne",
          street: "Uetlibergstrasse",
          houseNo: "138",
          countryCode: "CH",
          postalCode: "8045",
          town: "Zürich"
        },
        currency: "EUR",
        amount: 287.3,
        referenceNo: "87 00129 38238 13990 00012 38028",
        additionalInfo: "Rechnungsnr. 10978 / Auftragsrnr. 3987",
        language: "de",
        debtor: {
          name: "Simon Glarner",
          street: "Bächliwis",
          houseNo: "55",
          countryCode: "CH",
          postalCode: "8184",
          town: "Bachenbülach"
        },
        dueDate: "2018-07-18"
      },
      "eJxtT7FuwjAQ_RXLQyeg5xiUMxugCkZKylKxOOFwLKVGtUOpWvVv-g2d2PJjjQ1Dh1r2O7938nu-T_5GPtij41Mx4I125qQN8SnfEx_wV_9E723PivVi53cOBECsIsJiNZ4AZDJudVuz2NjOC1acbftBvjfcs9kyqutjaA-6quM9HgSFsT53F2-v8mIVsahqT9ZRD2ylSzKGPLtjRfdTO0r-1Da2JG9C63UISRMSr6bjyb-mGeYjmf7-sN0kAQQOIR-K9K6wL0fHlo32fW4U5t13VTf2bENkk2SKAsep109Bruwujf4b8bhJxpgDiExJzCQKqRRA5BIhS0kbqmp3ciY4P2ICVI7sns1Oh34UE3wUpcKcf_0CpgqLsQ~~"
    );

    let e4 = new Example(
      {
        account:"CH19 0900 0000 6900 2821 9",
        creditor: {
          name:"Croce Rossa Svizzera - Sezione del Sottoceneri",
          street:"Via alla Campagna",
          houseNo:"9",
          countryCode:"CH",
          postalCode:"6900",
          town:"Lugano"
        },
        finalCreditor: {
        },
        additionalInfo: "",
        referenceNo: "",
        debtor: {
        },
        currency:"CHF",
        language:"it",
      },
      "eJyFTU0LwjAM_SshZ4V2B3G7FmQHmWLF0y5BQynMVrtuyMT_bjvxqoHk5ePlvSeOHHrrHVZygR05M5BhrNBGXOA9HPkR06T3qg2tE1KIjDIXVctSlGKOVcJiXchyPgR_Zjj4vifQo50mDgRL0DwlH4YLd6B9jInkONj8cbIE1HUEiq43Mo7yctbKwhm3gyHnP7a5_kpVb_5Rml3zbfH1BiqXWw0~"
    );

    this.examples = [e1, e2, e3, e4];
  }

  public getExamples(): Example[] {
    return this.examples;
  }

}

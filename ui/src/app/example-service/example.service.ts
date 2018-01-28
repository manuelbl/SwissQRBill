//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { Injectable } from '@angular/core';
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

    let e5 = new Example(
      {
        "language":"en",
        "version":"V1_0",
        "amount":123949.75,
        "currency":"CHF",
        "account":"CH4431999123000889012",
        "creditor":{
          "name":"Robert Schneider AG",
          "street":"Rue du Lac",
          "houseNo":"1268/2/22",
          "postalCode":"2501",
          "town":"Biel",
          "countryCode":"CH"
        },
        "finalCreditor":{
          "name":"Robert Schneider Services Switzerland AG",
          "street":"Rue du Lac",
          "houseNo":"1268/3/1",
          "postalCode":"2501",
          "town":"Biel",
          "countryCode":"CH"
        },
        "referenceNo":"210000000003139471430009017",
        "additionalInfo":"Instruction of 15.09.2019##S1/01/20170309/11/10201409/20/14000000/22/36958/30/CH106017086/40/1020/41/3010",
        "debtor":{
          "name":"Pia-Maria Rutschmann-Schnyder",
          "street":"Grosse Marktgasse",
          "houseNo":"28",
          "postalCode":"9400",
          "town":"Rorschach",
          "countryCode":"CH"
        },
        "dueDate":"2019-10-30"
      },
      "eJx1UctOwzAQ_BXLvTb1buI83BtUokUCqSQ99mJS01oURzhJeYl_Z-0icaE-rHe9s-Px-IufjO9t5_gcp_yo3X7Ue8Pn3Dg-5a9-Y94Hqpr1Yuu3DhAg7BjCYiVlhkopTDMAqCoFmIZG3T0aP7CmPThjd8azq2U8Hg3bjexOt5EiLSqRijROpDlEymtrjmfqf3ka40-2NT1r3uzwaTzJ3V0mzwRe5ibNSqpZmZ-PbiISUCUISRbfuLY6udfealaPQ98eXrRzSdDyQVICYOm7vjeMMM_DXlMaOaoQlTz7VHeeJnV7-Lv4oa4jjpz8XRmSlBJlMJEsLEP71vWDH9uBPoZ1TwzzGahZ0DeZNCgABeUlZKAEokCgSlKegqA9LjJWZIXKyQYQixVCEQaqQkiIeCGROgj8-wfJDZzJ"
    );

    let e6 = new Example(
      {
        "language":"fr",
        "version":"V1_0",
        "amount":null,
        "currency":"CHF",
        "account":"CH37 0900 0000 3044 4222 5",
        "creditor":{
          "name":"Salvation Army Foundation Switzerland",
          "postalCode":"3000",
          "town":"Bern",
          "countryCode":"CH"
        },
        "finalCreditor": { },
        "referenceNo":null,
        "additionalInfo":"Donation to the Winterfest campaign",
        "debtor": { },
        "dueDate":null
      },
      "eJyFTcsKwjAQ_JWQcw_pQ8TetFJ6qkIFL70Eu9ZAu9FtWl_47zaGggfBhdndYYaZJx-AOqWRx77HG4l1L2vgMT8S9_iFdnAzIyu2SUklCl8Ie327kiyci4X4TCiiKAqCYGaFQjaDNGMmW1J7Z6nusXK8uCrzABprKmucEAoXuwJCl_yt_kKSpf8s-Saf3rVG1280Mydge4UG6AidYQfZnqWqkb_eEPdeig~~"
    );

    this.examples = [e3, e2, e1, e4, e5, e6];
  }

  public getExamples(): Example[] {
    return this.examples;
  }

}

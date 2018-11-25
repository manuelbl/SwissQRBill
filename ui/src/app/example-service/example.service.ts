//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { Injectable } from '@angular/core';
import { Example } from './example';

@Injectable({
  providedIn: 'root'
})
export class ExampleService {
  private readonly examples: Example[];

  constructor() {
    const e1 = new Example(
      {
        version: 'V1_0',
        currency: 'CHF',
        amount: 100,
        account: 'CH9300762011623852957',
        creditor: {
          name: 'Lea Simmen',
          street: 'Weinbergstrasse',
          houseNo: '31',
          postalCode: '5502',
          town: 'Hunzenschwil',
          countryCode: 'CH'
        },
        unstructuredMessage: '',
        reference: '',
        debtor: {},
        format: {
          language: 'de',
          outputSize: 'qr-bill-only',
          separatorType: 'solid-line-with-scissors'
        }
      },
      // tslint:disable-next-line:max-line-length
      'eJxdjUFPg0AQhf-KmTOYBUJruaKEg6lNaOJlL1s6wiTLLO4s1rbpfxfUi2Yuk7z3vu8KH-iFHEORRPDm_GACFFewhrvJdAgFHBEi6LwZe2ql-m3wZG0EbgrjFBq6LL13Hx_I2tixPc8LwdF4E5zfn8clFmfpGFtijE8U-lhaEnFeYNFyqMxA8-4bfItm2B4_Zw80u1KzSpXSnGgu602m1HqVqiRZpdlDnm7yteZG8zOau4aGAVnzKxIf0HcSvBFBzdk8zXOVaq4nviBL25_ILjjNfy9R6n5xlXX1P9q-bH-ep92jZrh9AQXZbMQ~'
    );

    const e2 = new Example(
      {
        version: 'V1_0',
        account: 'CH950020620610572901J',
        creditor: {
          name: 'Gromion SA',
          street: 'Avenue Fornachon',
          houseNo: '34',
          countryCode: 'CH',
          postalCode: '2034',
          town: 'Peseux'
        },
        currency: 'CHF',
        amount: 371.2,
        reference: 'RF23T2083QFT2800291F',
        unstructuredMessage: '',
        debtor: {
          name: 'Céline Pythoud',
          street: 'Chemin du Grand Record',
          houseNo: '42',
          countryCode: 'CH',
          postalCode: '1040',
          town: 'Échallens'
        },
        format: {
          language: 'fr',
          outputSize: 'qr-bill-only',
          separatorType: 'solid-line-with-scissors'
        }
      },
      // tslint:disable-next-line:max-line-length
      'eJxVT0tOwzAQvUrkdYIcJ6WfXRVIERtCk6U3JnETS844-FMaqh6Ao3COXgy7sEEzGmk-7zNndOTaCAVok8booPTILNqckWTQO9ZztEEHjWLUazYNojXl3wU4KWOknJ2crcVnuHvXyZuQMlEgZ48wfGKaWaWbeQpro6ToEimAJx_CDolphTFKGxRkwZZsFB53I77EnqzhJ6-D6qqggAnGFFIKxdN6gX137zPFiyVZ4_SZQk1hp9Xo34jqLYXtkYPjkfcKrB0UUMhyCgSHWnHD3SkwUfgf2TK9Izhsyhtjcf0ObqNqtoNynR8MfBQQdS7aaQZdtOet0n6eE-8N5x56_WoHJiUH8ytQFy97CvuSZA3Bq-y1bMjK21-nZRB8rB4ooMsP9iOEFg~~'
    );


    // 87 00129 38238 13990 00012 38028
    const e3 = new Example(
      {
        version: 'V1_0',
        account: 'CH450023023099999999A',
        creditor: {
          name: 'Schreinerei Habegger & Söhne',
          street: 'Uetlibergstrasse',
          houseNo: '138',
          countryCode: 'CH',
          postalCode: '8045',
          town: 'Zürich'
        },
        currency: 'EUR',
        amount: 287.3,
        reference: 'RF192320QF02T3234UI234',
        unstructuredMessage: 'Rechnungsnr. 10978 / Auftragsrnr. 3987',
        debtor: {
          name: 'Simon Glarner',
          street: 'Bächliwis',
          houseNo: '55',
          countryCode: 'CH',
          postalCode: '8184',
          town: 'Bachenbülach'
        },
        format: {
          language: 'de',
          outputSize: 'qr-bill-only',
          separatorType: 'solid-line-with-scissors'
        }
      },
      // tslint:disable-next-line:max-line-length
      'eJxVjr9SwzAMxl8l54EpKc6fXpNupRDKRCHtwnlxUjf2nSsHO6GUXt-GZ2DqlhdDgTIg6TR8kn6fjuRNWKcMkGnok62xO96S6ZFoDnXHa0GmZCOIT2rLG6kql182oNPaJ6Zrm64t1Mew92qDUmkdGNAHvHCi4Za3xq4OzTB2RqtNoBWIYK9aGbhKOWesI4MttDnfKbz7AZ98hK3EO_qQYjlnQCNKGYQM5otkTGkUD5VdYsagwKqkFQjH5i14KepaWO_KK_ovCYLBWrRalcLWrrXcOVTCOGWQ0mTM4KU_W1XJAc_gf0bpZBSj9936-ddG7Qx495pbtGJw039WUqu9cgzGSErDNEGVV1JA2Z81_6MW80cEPOdhFsURfcpptIqjOFk_YENdVBI6qB3YkRfSbJJ6196s2-KvtbODGGfpBL9Y3jIgp28Ia5aI'
    );

    const e4 = new Example(
      {
        version: 'V1_0',
        account: 'CH1909000000690028219',
        creditor: {
          name: 'Croce Rossa Svizzera - Sezione del Sottoceneri',
          street: 'Via alla Campagna',
          houseNo: '9',
          countryCode: 'CH',
          postalCode: '6900',
          town: 'Lugano'
        },
        unstructuredMessage: '',
        reference: '',
        debtor: {},
        currency: 'CHF',
        format: {
          language: 'it',
          outputSize: 'qr-bill-only',
          separatorType: 'solid-line-with-scissors'
        }
      },
      // tslint:disable-next-line:max-line-length
      'eJxdT81OwzAMfpXI50Zqe0C010K1AxoTmTjlYrqQWUqdkqSDddq7kw5Osw-2_P3JFziZEMkztFUBnz6MmKC9gEO2M1oDLVCCAmzA6UhD7P8ZPDtXgJ_TNCdFy8r7CvKDnJOe3TkropkwYPJhf55WOHpHB-mIjfymdJRxoBh9iLDGcupxpKy7GV-LbLY3PzkH1K7TXNZlqbnS3G2qpmzKWz3kWT_WVaNZZST4wYg3HyMKdaJlMQGFFMos-TkjDsYJ5VPKJDaBNL8TCnQORYfjhJZRczZaPTW_zBbZr2ma77vb9Pen7ev2b3nePWmG6y8FaXYA'
    );

    const e5 = new Example(
      {
        version: 'V1_0',
        amount: 123949.75,
        currency: 'CHF',
        account: 'CH4431999123000889012',
        creditor: {
          name: 'Robert Schneider AG',
          street: 'Rue du Lac',
          houseNo: '1268/2/22',
          postalCode: '2501',
          town: 'Biel',
          countryCode: 'CH'
        },
        reference: '210000000003139471430009017',
        unstructuredMessage:
          'Instruction of 15.09.2019##S1/01/20170309/11/10201409/20/14000000/22/36958/30/CH106017086/40/1020/41/3010',
        debtor: {
          name: 'Pia-Maria Rutschmann-Schnyder',
          street: 'Grosse Marktgasse',
          houseNo: '28',
          postalCode: '9400',
          town: 'Rorschach',
          countryCode: 'CH'
        },
        format: {
          language: 'en',
          outputSize: 'qr-bill-only',
          separatorType: 'solid-line-with-scissors'
        }
      },
      // tslint:disable-next-line:max-line-length
      'eJxVUc1y2yAQfhUNuVqGRbItfGvdOulMO-NKOXIhMrGY4sUF1NbN5N27OO2hcGCH728XXtgPG5MLyLawYM8hnk1m2xfmDZ5mc7JsyyyyBTtFc5ncmPZ_GTh7v2Bhzpc5D-534X2P9ZPzvg7or6RI9mKiySE-Xi8FTsG7Y-0d2vqny1OdRpdSiImVWMx7c3akuxm_Lsjs0f6iHDYcdhqFFEIjaNw9tG0DSimQjRCi65QAqXHQ2IcnG3M1jBNad7SxendPl7OtjnP12YykluuOSy6JLleCvN4764ujxv83WatWLTerAu5v5gdn6i8mOlP1c07jdDaIdYm6UpLG-xhSshUxvuWToZIiOo2qLV33NOM4mXF6y_ra94SC-LcaoLQNtGUcGmaj8ROmHOcx06dU4bmC1VKopRSg7u4G4AI41RvRCMUBONDTQEu1FJzO26IZebNWq443gu8eQKyLoFvzVtz4vAVCgHr7ePigkb3-ATcQn4g~'
    );

    const e6 = new Example(
      {
        version: 'V1_0',
        amount: null,
        currency: 'CHF',
        account: 'CH3709000000304442225',
        creditor: {
          name: 'Salvation Army Foundation Switzerland',
          postalCode: '3000',
          town: 'Bern',
          countryCode: 'CH'
        },
        reference: null,
        unstructuredMessage: 'Donation to the Winterfest campaign',
        debtor: {},
        format: {
          language: 'fr',
          outputSize: 'qr-bill-only',
          separatorType: 'solid-line-with-scissors'
        }
      },
      // tslint:disable-next-line:max-line-length
      'eJxdj8FugzAMhl8F-QxSClTTuG10qKe2EpV2ySWDAJGCw5zQjVZ99yVlh2lxJMv27--Xb3CRZJVBKDYxdIZG4aC4gRbYz6KXUEBHEENPYhpUY6tfBc5ax2BmN82uVteg-6TkQ2mdGNSL37ByEiScofMyhbE1WrWJViiTL-WGxDbKWkMWgi26SozK7z3A99jDzvLb-0B9KjmylDGOG47lPntiz-zxMpbneZqmW461_0JfhPOHRC80LlFlZmzXuvZ2V0n-opZjiIwF2qskDMC19zfKffW_dTgeQtoZXJnORG6Q0btCJ6mT1kWNGCehei96O-04wv0HRDh5fQ~~'
    );

    this.examples = [e3, e2, e1, e4, e5, e6];
  }

  public getExamples(): Example[] {
    return this.examples;
  }
}

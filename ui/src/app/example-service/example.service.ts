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
          separatorType: 'dashed-line-with-scissors'
        }
      },
      // tslint:disable-next-line:max-line-length
      'eJxdjUFPg0AQhf-KmTOYBUJre0UJB1Ob0MTLXrZ0CpMss7izWNum_12oXjRzecm873tX-EQv5BjWSQRH53sTYH0Fa7gdTYuwhgNCBK03Q0eNlL8NHq2NwI1hGENNl7n34eM9WRs7tueJEByMN8H53Xm4a4x0eIgtMcYnCl0sDYk4LzDvcihNTxN4N9-iybbDr2kI6m2hWaVKaU40F9UqU2q5SFWSLNLsKU9X-VJzrfkVzUNNfY-s-R2J9-hbCd6IoOZsQvNcpZqrkS_I0nQnsrNO899LlHqct4qq_P_avG1-wsv2WTPcvgFq420S'
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
          separatorType: 'dashed-line-with-scissors'
        }
      },
      // tslint:disable-next-line:max-line-length
      'eJxVT0tOwzAQvUrkdYIcp6WfXRVIERtCk6U3JnFjS844-FNaqh6Ao3COXgy7sEEzGmk-7zNndODGSg1onador83IHFqfkWIweDZwtEZ7g1I0GDYJ2dnq7wK8UinS3k3eNfIz3r2b7E0qlWlQp4CwfGKGOW3a0xTXPbOC95mSwLMP6URmO2mtNhZFXXAVG2UA3pgvaWBr-TEIoaYuKWCCMYWcQvm0muPQ3YfM8XxBVjh_ptBQ2Bo9hj-SZkNhc-DgeRLMAuuEBgrFjALBsdbccn-MTBT-R7HI7wiOm-rGWF6_o9ukPjmhfR8Ggo8Skt4nW8OgT3a80ybMZyR4w7MAvX51ginFwf4KNOXLjsKuIkVL8LJ4rVqyDPZXeRUFH-sHCujyA3E7hGQ~'
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
          separatorType: 'dashed-line-with-scissors'
        }
      },
      // tslint:disable-next-line:max-line-length
      'eJxVjs9SwjAQh1-lk4MniukfhpYbohVPIoWLk0taQpOZsK1JKiLD2_gMnrj1xdwiHkx29vCb3e_bI3kXxqoayCQYkG1tdtyRyZFoDlXLK0EmZCPIgFSGN1KVNrtOQKv1gNSta1qXq89-7s34hdLar0EfcMOKhhvuarM6NBcMt1JsfK1A-HvlpG9LZW1tLOm94DK-U7h4IZ8GSFuJDxSRfDFjQENKGQQMZvN4RGkY9ZVe35RBjlVKIxCOzZvzQlSVMN6Nl3ffEgSDtXBaFcJU1hluLSZBlDBIaDxi8NqdjSplj2fw_4fJeBih-2G9_NWoXQ3eo-YGVQzuuq9SarVXlsEISUmQxJjyUgoourPmf9R89oyAZRakYRTSl4yGqyiM4vUTNsxFKaGFyoIZegFNx4l3603bLd5aWdOHUZqM8YrFPQNy-gGSoZbW'
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
          separatorType: 'dashed-line-with-scissors'
        }
      },
      // tslint:disable-next-line:max-line-length
      'eJxdT81ugzAMfpXIZyIBh2lwZUM9TF21VDvl4oEHkYLDktCtVH33hW6n2gdb_v7kC5zIB-MY6iKDT-cnjFBfwCIPCw4ENZgIGQwe59F0of1n8GJtBm6J8xKVWTfel5cfxlrp2J6TItCMHqPzx_O8wT2GkXppDZP8NnGUoTMhOB9gy-XY4mSS8OZ8zZLbkX5SEKhDozkv81xzobnZFVVe5bd6SLN8LItKs0qIdx2JNxcCCnUy60oehRSK1vQdiZ6sUC7GRGLyRvO7QYHWomhwmnFg1JyMNk_NL8uA7LY0zffd7Nr70_51_7c8H540w_UXciV2Tg~~'
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
          'Instruction of 15.09.2019',
        billInformation: '//S1/01/20170309/11/10201409/20/14000000/22/36958/30/CH106017086/40/1020/41/3010',
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
          separatorType: 'dashed-line-with-scissors'
        }
      },
      // tslint:disable-next-line:max-line-length
      'eJxVUc1y2yAQfhUNZ8vsItkWvjVOnXSmnXGlHLkQmVhM5ZULqK2bybt3cdpD4cDOfn8svIofLkQ_kdjiQrxM4WyT2L6K0dJpticntsKRWIhTsJfB93H_l0HzOC7ENKfLnDr_O_O-h_LZj2M50XhlRXQXG2yawtP1kuGjjYM7lqMnV_70aShj72OcQhQ5l9Lenj0Lb85vC3Z7cr84SHSHnSFQAIbQ0O6xrivUWqOqAKBpNKAy1Blqp2cXUtH1Azl_dKH48MDN2RXHufhse1ardSOVVExXK2CvO-_G7Gjo_83WutbLzSqD-5v5wdvyiw3eFu2cYj-cLVGZo66cZOghTDG6ghnf0slyyRGNIV3nW7c8Yz_YfnjP-tq2jCL8WxVy2gbrPA4PszH0iWIKc5_4V4rppcDVEvRSAWpDHw_3hqTsUAJKbm2gAi0RJfILYc21AsnnbfGoslrrVSMrkLtHhHUWNGtZw40va2QEQbz9ATXGn-4~'
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
          separatorType: 'dashed-line-with-scissors'
        }
      },
      // tslint:disable-next-line:max-line-length
      'eJxdj8FugzAMhl8F-QxSCkzTuG10qKeuEpV24eKVAJGCw5zQjVZ99yVlh2lxJMv27--Xr3CWbJUhKDYxdIZHdFBcQSP1M_YSCugYYugZp0GdbPWroFnrGMzsptnV6hJ0n5x8KK0TQ3rxG1ZOyOgMH5cpjFu0g2wTrUgmX8oNiT0paw1bCL7kKhyVX7yTb7GnHeW3N4L6UDYkUiEa2jRU7rJH8STuLxN5nqdp-tBQ7T_qMzp_SfTM4xJVZqZ2rWtvd5HsT2obCpGJQHuRTAG49v5Guav-t_Zv-5C2hlamM5EbZPSuyEnupHXRCccJVe9Fr4dtQ3D7AbLDecs~'
    );

    this.examples = [e3, e2, e1, e4, e5, e6];
  }

  public getExamples(): Example[] {
    return this.examples;
  }
}

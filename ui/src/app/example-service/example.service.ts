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
      'eJxdjctuwjAQRX-lmnWoHEeBkm3aKAsESEHqZjaGDsGSM079aAuIf6_Tx6LVbEZzz7lzhTdyXluGKs_gaN2gAlRXMIr7qHqCCl4IMuidGk_64JsfgqMxGdgYxhg6fZm4Vzfba2Nmls05GZ5G5VSwbnce6Vc4Wg6NGnQivi63LGk7-kiN0G1rZCGFQM6R63ZZCLGYS5Hnc1k8lHJZLpA75BWpu04PAzHyM2nek-t9cMp7Qi6SWpZCIreRL8T-cHrXZqpD_ju5EPfTr7pt_kfrzfp7edo-IsPtE5kcZLw~'
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
        reference: '0000383800001776',
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
      'eJxVTstuwjAQ_JXI51A5DwjkhtKGqqeUcPTFDUtiyVmnToxIER_QT-l38GPdUHrAa9na2ZnZObMj2F4ZZGngs4OxrRxYemZaYu1kDSxlB8t8VlvZNarq8zsDndY-M27o3FCqr4n3aWcfSuuZQT2SoodOWjkYuxs7-BccDA65bBUxbsjFJ9kOTuTIyiITyEPOBQYCs9fVnFO3oBvweRKuePAmsBS4saalwF65Frg-AjrwKBXKqjEoMIoFhnx6C-jBnSYngY8VJcFTyKdJfnPMrj9aIXjFODTG7QlooFXo7Z23sRL33hYqYwmPQ8rGY5Jev6tGag3Y_y14324p_OOJltFy-oMkWUxrX4pngezyCwxZfFI~'
    );

    const e3 = new Example(
      {
        version: 'V1_0',
        account: 'CH450023023099999999A',
        creditor: {
          name: 'UBS Switzerland AG',
          street: 'Postfach',
          houseNo: '',
          countryCode: 'CH',
          postalCode: '8098',
          town: 'Zürich'
        },
        currency: 'EUR',
        amount: 287.3,
        reference: '870012938238139900001238028',
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
      'eJxNjkFuwjAQRa8SeR2oE4NiswNaYEkJbCpvTHASS2ac2k4pIG7TY7DjYnUQSIxn46__3_wz-pHWKQNolMSoNHYvPBqdkRZQtaKSaIR2EsWosqKpVeFmDwe0WsfItL5pfa5One_b9rZK654BfQwJJxthhTd2fWzkM1Aa8DOxV8FxVy5xiK3lbyCifDnlgFOMOSQcpovBEOOUdMseM-aQc9hM8ig_KH-SNrTcReM5h6VxvhRFzYEDxYxy-LpdreqE6aITX19Ksz4JVz42qzswV3sD0VwLC9JymNz-ilqrg3IchsPAS-ggqIEuYXu7avGkfq5CnmYYJykjNCU0IYxh3P0JxWkosZJFDS1UDmw_SjDLaPQWjdvSW1E524mE0Sw0Wb5zQJd_vTOFyA~~'
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
      'eJxdj01vwjAMhv9K5HMrtT1MtNduFYcJ0IJ2ysVjJkRKnS4faBTx35ey7YJ9sGU_72v5CmfywTiGri7g6PyIEborWGSdUBN0YCIUoD1OJ3MIwx_BydoCXIpTitLMC_flyw9jbenYXrIi0IQeo_P7y0T_gqPjOOBoMnGf3Ios29N3dgS56xVXTVUprhX367qt2uoeT7k2q6ZuFcu88e5A4s2FgEKezTyTR1EKSXN-g8QnWSFdjBli8kbxu0GB1qLocZxQMyrORoun4tekkd1yTfFj9uvhcbTZbn6bl92zYrj9AM4dbfg~'
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
      'eJxVUM1OwzAMfpUqXLfFTrut2Q0GAySQRssxl1CyLaJzRpoiBuLdcQcccA6x7O_H9qd4c7HzgcQCR2IT4t4msfgUraVtb7dOLIQjMRLbaA8733SrXwT1bTsSoU-HPtX-Y8C9xvGTb9txoPbIjM4dbLQpxMfjwf0RNoHSyu49I06VrxHTHt07K4p6vTQECsAQGlreFEWOWmtUOQCUpQZUhmpDVXhyMWV1syPnn13Mzq-52Lvsuc_ubMNsNSulkorhagqsdeFdOyga-v9YWhd6Mp8OzdVJfO3t-N5Gb7OqT12z21ui8WB1ZCdD1zF0ncsY8ZK2llO2KA3pYpi6CpEZttn9eD1UFXcR_iJHdptjMazDy8wN3VKXYt8kPn8WNhlOJ6AnClCfndUoASXnc8hBS0SJfBosOFcg-T8F7yjzmZ6WMge5vEGYDYRyJgs44WWB3EGe7Wp9aUh8fQNyzZeA'
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
      'eJxdTtFqg0AQ_BXZZwMXtZT61ppIntKAgb7cyzY5zcG5Z9c1rQn5956xhdKdhWWHmWGucDbcW0-QL2OoPbcokF_BITUDNgZyqBliaBi7kz305Y-CBudi8IN0g1T2Muk-ePFunVt4cmNw9KZDRvG8Hzvza6g9SYmtDYo7c4uDbW--QiJUu0KTSpTStNRUbNJH9aTuk6osy5IkedBUhUV3RgmVo2dux6j0Ax3nv_q0cjEcuh81TUjVlPZimKbAmfuLYlP-p7av2-msPM2Z4iM5mejNkhiuTS_RAdsObRNE691KE9y-Ad0qcXU~'
    );

    this.examples = [e3, e2, e1, e4, e5, e6];
  }

  public getExamples(): Example[] {
    return this.examples;
  }
}

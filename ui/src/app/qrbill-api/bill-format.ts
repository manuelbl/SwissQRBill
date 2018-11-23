//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
export class BillFormat {
  language = 'en';
  graphicsFormat?: string;
  outputSize?: string;
  separatorType?: string;
  fontFamily?: string;

  static clone(format: BillFormat): BillFormat {
    return {
        language: format.language,
        graphicsFormat: format.graphicsFormat,
        outputSize: format.outputSize,
        separatorType: format.separatorType,
        fontFamily: format.fontFamily
    };
  }
}

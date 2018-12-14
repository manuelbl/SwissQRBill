//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { QrBill } from '../qrbill-api/qrbill';

@Component({
  selector: 'qrbill-preview',
  templateUrl: './preview.component.html',
  styleUrls: ['./preview.component.css']
})
export class PreviewComponent implements OnInit {
  public billID: string;
  public imageWidth: string;
  public imageHeight: string;

  constructor(@Inject(MAT_DIALOG_DATA) private data: any) {
    this.billID = data.billID;

    const bill: QrBill = data.validatedBill;
    const outputSize = bill.format.outputSize;

    // Set the image size to prevent the dialog from resizing
    // in the middle of the opening animation.
    // The values are calculated as: dim_in_mm / 25.4 mm/in * 96 px/in
    if (outputSize === 'qr-bill-only') {
      this.imageWidth = '793.700';
      this.imageHeight = '396.850';
    } else if (outputSize === 'a4-portrait-sheet') {
      this.imageWidth = '793.700';
      this.imageHeight = '1122.519';
    } else if (outputSize === 'qr-code-only') {
      this.imageWidth = '173.858';
      this.imageHeight = '173.858';
    }
  }

  ngOnInit() {}
}

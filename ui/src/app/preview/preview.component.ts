//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'qrbill-preview',
  templateUrl: './preview.component.html',
  styleUrls: ['./preview.component.css']
})
export class PreviewComponent implements OnInit {
  public billID: string;
  public outputSize = 'a6-landscape';

  constructor(@Inject(MAT_DIALOG_DATA) private data: any) {
    this.billID = data.billID;
    this.outputSize = data.outputSize;
  }

  ngOnInit() {}
}

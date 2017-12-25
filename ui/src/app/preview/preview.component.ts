//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

@Component({
  selector: 'app-preview',
  templateUrl: './preview.component.html',
  styleUrls: ['./preview.component.css']
})
export class PreviewComponent implements OnInit {

  public billID: string;
  public outputSize: string = "a6Landscape";

  constructor(public dialogRef: MatDialogRef<PreviewComponent>,
      @Inject(MAT_DIALOG_DATA) private data: any) {
        this.billID = data.billID;
      }

  ngOnInit() {
  }

  closeDialog() {
    this.dialogRef.close();
  }
}

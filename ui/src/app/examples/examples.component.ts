//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { Component, OnInit } from '@angular/core';
import { ExampleService } from '../example-service/example.service';
import { Example } from '../example-service/example';
import { BillSingletonService } from '../bill-singleton-service/bill-singleton.service';
import { Router } from '@angular/router';

@Component({
  selector: 'qrbill-examples',
  templateUrl: './examples.component.html',
  styleUrls: ['./examples.component.css']
})
export class ExamplesComponent implements OnInit {

  examples: Example[];

  constructor(private exampleService: ExampleService, private billSingleton: BillSingletonService,
    private router: Router) { }

  ngOnInit() {
    this.examples = this.exampleService.getExamples();
  }

  useExample(example: Example) {
    this.billSingleton.setBill(example.bill);
    this.router.navigate(['/bill']);
  }
}

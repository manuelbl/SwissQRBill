//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ExamplesComponent } from './examples.component';
import { MatCardModule } from '@angular/material';
import { ExampleService } from '../example-service/example.service';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateMockLoader } from '../mock/translate-mock.loader';
import { BillSingletonService } from '../bill-singleton-service/bill-singleton.service';
import { RouterTestingModule } from '@angular/router/testing';

describe('ExamplesComponent', () => {
  let component: ExamplesComponent;
  let fixture: ComponentFixture<ExamplesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        ExamplesComponent
      ],
      imports: [
        MatCardModule,
        RouterTestingModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateMockLoader }
        })
      ],
      providers: [
        ExampleService,
        BillSingletonService
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ExamplesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

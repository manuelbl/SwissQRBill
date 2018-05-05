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
import { RouterTestingModule } from '@angular/router/testing';

describe('ExamplesComponent', () => {
  let component: ExamplesComponent;
  let fixture: ComponentFixture<ExamplesComponent>;
  let exampleService: ExampleService;

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
      ]
    })
    .compileComponents();

    exampleService = TestBed.get(ExampleService);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ExamplesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render cards', async(() => {
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('button').textContent).toContain('use');
    expect(compiled.querySelector('img').src.endsWith('/' + exampleService.getExamples()[0].billID)).toBeTruthy();
    expect(compiled.querySelectorAll('mat-card').length).toEqual(exampleService.getExamples().length);
  }));

});

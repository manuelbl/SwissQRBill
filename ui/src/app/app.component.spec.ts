//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { TestBed, async } from '@angular/core/testing';
import {
  MatButtonModule,
  MatButtonToggleModule,
  MatCardModule,
  MatDatepickerModule,
  MatDialogModule,
  MatIconModule,
  MatInputModule,
  MatSelectModule,
  DateAdapter,
} from '@angular/material';
import { RouterModule } from '@angular/router';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { APP_BASE_HREF } from '@angular/common';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';

import { TranslateMockLoader } from './mock/translate-mock.loader';
import { AboutComponent } from './about/about.component';
import { AppComponent } from './app.component';
import { BillData } from './billdata/billdata.component';
import { NavbarComponent } from './navbar/navbar.component';
import { AppRoutingModule } from './app-routing/app-routing.module';
import { SettingsComponent } from './settings/settings.component';
import { ExamplesComponent } from './examples/examples.component';
import { IsoDateAdapter } from './date-adapter/iso-date-adapter';

describe('AppComponent', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        AppComponent,
        BillData,
        NavbarComponent,
        SettingsComponent,
        AboutComponent,
        ExamplesComponent
      ],
      imports: [
        FormsModule,
        ReactiveFormsModule,
        MatButtonModule,
        MatButtonToggleModule,
        MatCardModule,
        MatDatepickerModule,
        MatDialogModule,
        MatIconModule,
        MatInputModule,
        MatSelectModule,
        BrowserAnimationsModule,
        RouterModule,
        AppRoutingModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateMockLoader }
        })
      ],
      providers: [
        { provide: APP_BASE_HREF, useValue: '/qrbill' },
        { provide: DateAdapter, useClass: IsoDateAdapter }
      ]
    }).compileComponents();
  }));
  it('should create the app', async(() => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.debugElement.componentInstance;
    expect(app).toBeTruthy();
  }));
  it(`should have as title 'Swiss QR Bill'`, async(() => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.debugElement.componentInstance;
    expect(app.title).toEqual('Swiss QR Bill');
  }));
  it('should render navigation bar', async(() => {
    const fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('.navbar-button').textContent).toContain('Swiss QR Bill');
  }));
});

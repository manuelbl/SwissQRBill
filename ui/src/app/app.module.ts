//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import '../polyfills';
import { NgModule, Input } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {
  MatButtonModule,
  MatButtonToggleModule,
  MatCardModule,
  MatDatepickerModule,
  MatDialogModule,
  MatIconModule,
  MatInputModule,
  MatNativeDateModule,
  MatSelectModule,
  DateAdapter
} from '@angular/material';
import 'hammerjs';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';

import { HttpClientModule, HttpClient } from '@angular/common/http';
import { AppComponent } from './app.component';
import { BillData } from './billdata/billdata.component';
import { QrBillService } from './qrbill-api/qrbill.service';
import { PreviewComponent } from './preview/preview.component';
import { NavbarComponent } from './navbar/navbar.component';
import { AppRoutingModule } from './app-routing/app-routing.module';
import { AboutComponent } from './about/about.component';
import { SettingsComponent } from './settings/settings.component';
import { ExamplesComponent } from './examples/examples.component';
import { ExampleService } from './example-service/example.service';
import { BillSingletonService } from './bill-singleton-service/bill-singleton.service';
import { IsoDateAdapter } from './date-adapter/iso-date-adapter';
import { InputWithFormatDirective } from './input-fields/input-with-format';
import { AmountFormatter } from './input-fields/amount-formatter';

export function createTranslateLoader(http: HttpClient) {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}

@NgModule({
  declarations: [
    AppComponent,
    BillData,
    PreviewComponent,
    NavbarComponent,
    AboutComponent,
    SettingsComponent,
    ExamplesComponent,
    InputWithFormatDirective
  ],
  entryComponents: [
    PreviewComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatCardModule,
    MatDatepickerModule,
    MatDialogModule,
    MatIconModule,
    MatInputModule,
    MatNativeDateModule,
    MatSelectModule,
    HttpClientModule,
    AppRoutingModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: (createTranslateLoader),
        deps: [ HttpClient ]
      }
    })
  ],
  providers: [
    QrBillService,
    ExampleService,
    BillSingletonService,
    { provide: DateAdapter, useClass: IsoDateAdapter },
    AmountFormatter
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}

//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { InputWithFormatDirective } from './input-fields/input-with-format';
import { AppRoutingModule } from './app-routing/app-routing.module';
import { AboutComponent } from './about/about.component';
import {
  MatButtonModule,
  MatButtonToggleModule,
  MatCardModule,
  MatDatepickerModule,
  MatDialogModule,
  MatIconModule,
  MatInputModule,
  MatSelectModule,
  MatDialogConfig
} from '@angular/material';
import {
  MatMomentDateModule,
  MomentDateAdapter
} from '@angular/material-moment-adapter';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { NavbarComponent } from './navbar/navbar.component';
import { DateAdapter } from '@angular/material';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AmountFormatter } from './input-fields/amount-formatter';
import { SettingsComponent } from './settings/settings.component';
import { ExamplesComponent } from './examples/examples.component';
import { PreviewComponent } from './preview/preview.component';
import { BillDataComponent } from './billdata/billdata.component';

export function createTranslateLoader(http: HttpClient) {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}

@NgModule({
  declarations: [
    AppComponent,
    BillDataComponent,
    PreviewComponent,
    NavbarComponent,
    AboutComponent,
    SettingsComponent,
    ExamplesComponent,
    InputWithFormatDirective
  ],
  entryComponents: [PreviewComponent],
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
    MatMomentDateModule,
    MatSelectModule,
    HttpClientModule,
    AppRoutingModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: createTranslateLoader,
        deps: [HttpClient]
      }
    })
  ],
  providers: [
    { provide: DateAdapter, useClass: MomentDateAdapter },
    AmountFormatter,
    MatDialogConfig
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}

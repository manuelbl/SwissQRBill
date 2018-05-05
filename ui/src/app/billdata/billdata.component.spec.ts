import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BillDataComponent } from './billdata.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
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
  MatNativeDateModule
} from '@angular/material';
import { RouterModule } from '@angular/router';
import { AppRoutingModule } from '../app-routing/app-routing.module';
import { InputWithFormatDirective } from '../input-fields/input-with-format';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateMockLoader } from '../mock/translate-mock.loader';
import { HttpClientModule } from '@angular/common/http';
import { AmountFormatter } from '../input-fields/amount-formatter';
import { IsoDateAdapter } from '../date-adapter/iso-date-adapter';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('BillDataComponent', () => {
  let component: BillDataComponent;
  let fixture: ComponentFixture<BillDataComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        BillDataComponent,
        InputWithFormatDirective
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
        MatNativeDateModule,
        BrowserAnimationsModule,
        RouterModule,
        HttpClientModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateMockLoader }
        })
      ],
      providers: [
        { provide: DateAdapter, useClass: IsoDateAdapter },
        AmountFormatter
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BillDataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

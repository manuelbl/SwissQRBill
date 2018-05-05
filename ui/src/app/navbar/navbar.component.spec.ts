//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { MatMenuModule, MatIconModule } from '@angular/material';

import { NavbarComponent } from './navbar.component';
import { TranslateModule, TranslateLoader, TranslateService } from '@ngx-translate/core';
import { TranslateMockLoader } from '../mock/translate-mock.loader';

describe('NavbarComponent', () => {
  let component: NavbarComponent;
  let fixture: ComponentFixture<NavbarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NavbarComponent ],
      imports: [
        MatMenuModule,
        MatIconModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateMockLoader }
        })
      ]
    })
    .compileComponents();
  }));

  beforeEach(inject([TranslateService], (service) => {
    service.use('en');
    fixture = TestBed.createComponent(NavbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render bar', async(() => {
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('img').src.endsWith('/assets/swiss-qr-bill.svg')).toBeTruthy();
    expect(compiled.querySelector('.navbar-link').textContent).toEqual('Examples');
  }));

});

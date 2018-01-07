import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { MatMenuModule, MatIconModule } from '@angular/material';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';

import { NavbarComponent } from './navbar.component';
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

  beforeEach(() => {
    fixture = TestBed.createComponent(NavbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

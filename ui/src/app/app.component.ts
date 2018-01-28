//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { Component } from '@angular/core';
import { TranslateService, LangChangeEvent } from '@ngx-translate/core';
import { Title } from '@angular/platform-browser';
import { DateAdapter } from '@angular/material';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'Swiss QR Bill';

  constructor(private translate: TranslateService, private titleService: Title, private dateAdapter: DateAdapter<any>) {
    translate.addLangs(["en", "de"]);
    translate.setDefaultLang('en');

    let browserLang = translate.getBrowserLang();
    if (browserLang.match(/de/)) {
      browserLang = "de";
    } else {
      browserLang = "en";
    }
    translate.use(browserLang);
    this.dateAdapter.setLocale(browserLang + "-CH");

    this.setTitle();
    this.translate.onLangChange.subscribe((params: LangChangeEvent) => {
      this.setTitle();
      this.dateAdapter.setLocale(params.lang + "-CH");
    });
  }

  private setTitle() {
    this.translate.get('app_name').subscribe((res: string) => {
      this.titleService.setTitle(res);
    });
  }
}

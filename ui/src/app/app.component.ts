//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { Component } from '@angular/core';
import { TranslateService, LangChangeEvent } from '@ngx-translate/core';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'app';

  constructor(private translate: TranslateService, private titleService: Title) {
    translate.addLangs(["en", "de"]);
    translate.setDefaultLang('en');

    let browserLang = translate.getBrowserLang();
    translate.use(browserLang.match(/en|de/) ? browserLang : 'en');

    this.setTitle();
    this.translate.onLangChange.subscribe((params: LangChangeEvent) => {
      this.setTitle();
    });
  }

  private setTitle() {
    this.translate.get('app_name').subscribe((res: string) => {
      this.titleService.setTitle(res);
    });
  }
}

//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'qrbill-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent implements OnInit {
  language: string;

  constructor(private translate: TranslateService) {}

  ngOnInit() {
    this.language = this.translate.currentLang;
  }

  switchLanguage(lang: string) {
    this.translate.use(lang);
  }
}

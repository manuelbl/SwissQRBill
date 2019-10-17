//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { TranslateLoader } from '@ngx-translate/core';
import { Observable, of } from 'rxjs';

const translations: any = {
  app_name: 'Swiss QR Bill',
  copyright: 'Copyright © 2019 Manuel Bleichenbacher',
  examples: 'Examples'
};

export class TranslateMockLoader extends TranslateLoader {
  getTranslation(lang: string): Observable<any> {
    return of(translations);
  }
}

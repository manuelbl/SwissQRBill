//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
import { TranslateLoader } from "@ngx-translate/core";
import { Observable } from "rxjs/Observable";
import { of } from 'rxjs/observable/of';

let translations: any = {
    app_name: "Swiss QR Bill",
    copyright: "Copyright © 2018 Manuel Bleichenbacher"
};

export class TranslateMockLoader extends TranslateLoader {
    getTranslation(lang: string): Observable<any> {
        return of(translations);
    }
}

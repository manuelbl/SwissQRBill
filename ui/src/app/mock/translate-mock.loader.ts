import { TranslateLoader } from "@ngx-translate/core";
import { Observable } from "rxjs/Observable";
import { of } from 'rxjs/observable/of';

let translations: any = {
    app_name: "Swiss QR Bill"
};

export class TranslateMockLoader extends TranslateLoader {
    getTranslation(lang: string): Observable<any> {
        return of(translations);
    }
}

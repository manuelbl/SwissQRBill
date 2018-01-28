//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

import { Injectable, Optional, Inject } from "@angular/core";
import { NativeDateAdapter, MAT_DATE_LOCALE } from "@angular/material";

@Injectable()
export class IsoDateAdapter extends NativeDateAdapter {

    constructor(@Optional() @Inject(MAT_DATE_LOCALE) matDateLocale: string) {
        super(matDateLocale);
    }

    getFirstDayOfWeek(): number {
        return 1;
    }

}

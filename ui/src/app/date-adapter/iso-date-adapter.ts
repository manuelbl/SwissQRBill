//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

import { Injectable, Optional, Inject } from "@angular/core";
import { NativeDateAdapter, MAT_DATE_LOCALE } from "@angular/material";
import { Platform } from "@angular/cdk/platform";

@Injectable()
export class IsoDateAdapter extends NativeDateAdapter {

    constructor(@Optional() @Inject(MAT_DATE_LOCALE) matDateLocale: string, platform: Platform) {
        super(matDateLocale, platform);
    }

    getFirstDayOfWeek(): number {
        return 1;
    }

}

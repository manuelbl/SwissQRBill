//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
export class ValidationMessage {
    type: string;
    field: string;
    messageKey: string;
    message?: string;
    messageParameters?: string[];

}

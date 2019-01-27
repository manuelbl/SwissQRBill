//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web.controller;

class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = 4931808843934640625L;

    BadRequestException(String message) {
        super(message);
    }
}

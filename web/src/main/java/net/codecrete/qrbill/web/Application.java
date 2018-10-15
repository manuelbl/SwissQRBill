//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "net.codecrete.qrbill.web", "net.codecrete.qrbill.web.config", "net.codecrete.qrbill.web.controller" })
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}


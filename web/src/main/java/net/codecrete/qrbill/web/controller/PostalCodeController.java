//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web.controller;

import net.codecrete.qrbill.web.api.PostalCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PostalCodeController {

    @Autowired
    private PostalCodeData postalCodeData;


    @RequestMapping(value = "/postal-codes/suggest")
    @ResponseBody
    public PostalCode[] suggestPostalCodes(@RequestParam(name = "country", defaultValue = "") String country, @RequestParam("substring") String substring) {

        // get postal code
        List<PostalCodeData.PostalCode> postalCodeList = postalCodeData.suggestPostalCodes(country, substring);

        // convert result into API data structure
        int len = postalCodeList.size();
        PostalCode[] postalCodes = new PostalCode[len];
        for (int i = 0; i < len; i++) {
            PostalCode pc = new PostalCode();
            pc.setPostalCode(postalCodeList.get(i).postalCode);
            pc.setTown(postalCodeList.get(i).town);
            postalCodes[i] = pc;
        }
        return postalCodes;
    }
}



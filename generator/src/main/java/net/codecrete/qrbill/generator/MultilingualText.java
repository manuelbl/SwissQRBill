//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import java.util.Locale;
import java.util.ResourceBundle;

public class MultilingualText {

    public static final String KEY_PAYMENT_PART = "payment_part";
    public static final String KEY_ACCOUNT_PAYABLE_TO = "account_payable_to";
    public static final String KEY_REFERENCE = "reference";
    public static final String KEY_ADDITIONAL_INFORMATION = "additional_info";
    public static final String KEY_CURRENCY = "currency";
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_RECEIPT = "receipt";
    public static final String KEY_ACCEPTANCE_POINT = "acceptance_point";
    public static final String KEY_PAYABLE_BY = "payable_by";
    public static final String KEY_PAYABLE_BY_NAME_ADDRESS = "payable_by_name_addr";

    private static final String[] languageCodes = { "de", "fr", "it", "en" };

    private static final ResourceBundle[] messageBundles = new ResourceBundle[4];

    public static String getText(String key, Language language) {

        int index = getLanguageIndex(language);
        ResourceBundle bundle = messageBundles[index];

        if (bundle == null) {
            Locale locale = new Locale(languageCodes[index], "CH");
            bundle = ResourceBundle.getBundle("qrbill", locale, MultilingualText.class.getClassLoader());
            messageBundles[index] = bundle;
        }

        return bundle.getString(key);
    }

    private static int getLanguageIndex(Language language) {
        int index;
        switch (language) {
        case DE:
            index = 0;
            break;
        case FR:
            index = 1;
            break;
        case IT:
            index = 2;
            break;
        default:
            index = 3;
        }

        return index;
    }

    private MultilingualText() {
        // Do not create instances
    }
}

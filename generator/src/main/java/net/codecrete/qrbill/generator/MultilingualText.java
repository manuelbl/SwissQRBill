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

    public static final String KEY_QR_BILL_PAYMENT_PART = "qrbill_payment_part";
    public static final String KEY_SUPPORTS = "supports";
    public static final String KEY_CREDIT_TRANSFER = "credit_transfer";
    public static final String KEY_ACCOUNT = "account";
    public static final String KEY_CREDITOR = "creditor";
    public static final String KEY_FINAL_CREDITOR = "ultimate_creditor";
    public static final String KEY_REFERENCE_NUMBER = "reference_no";
    public static final String KEY_ADDITIONAL_INFORMATION = "additional_info";
    public static final String KEY_DEBTOR = "debtor";
    public static final String KEY_DUE_DATE = "due_date";
    public static final String KEY_CURRENCY = "currency";
    public static final String KEY_AMOUNT = "amount";

    private static String languageCodes[] = { "de", "fr", "it", "en" };

    private static ResourceBundle messageBundles[] = new ResourceBundle[4];


    public static String getText(String key, Bill.Language language) {

        int index = getLanguageIndex(language);
        ResourceBundle bundle = messageBundles[index];

        if (bundle == null) {
            Locale locale = new Locale(languageCodes[index], "CH");
            bundle = ResourceBundle.getBundle("qrbill", locale, MultilingualText.class.getClassLoader());
            messageBundles[index] = bundle;
        }

        return bundle.getString(key);
    }

    private static int getLanguageIndex(Bill.Language language) {
        int index;
        switch (language) {
            case de:
                index = 0;
                break;
            case fr:
                index = 1;
                break;
            case it:
                index = 2;
                break;
            default:
                index = 3;
        }

        return index;
    }
}

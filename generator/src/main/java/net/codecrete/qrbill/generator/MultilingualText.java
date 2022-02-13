//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Helper class providing multilingual texts printed on QR bills.
 */
public class MultilingualText {

    /**
     * Text key for "Payment part"
     */
    public static final String KEY_PAYMENT_PART = "payment_part";
    /**
     * Text key for "Account / payable to"
     */
    public static final String KEY_ACCOUNT_PAYABLE_TO = "account_payable_to";
    /**
     * Text key for "Reference"
     */
    public static final String KEY_REFERENCE = "reference";
    /**
     * Text key for "Additional information"
     */
    public static final String KEY_ADDITIONAL_INFORMATION = "additional_info";
    /**
     * Text key for "Currency"
     */
    public static final String KEY_CURRENCY = "currency";
    /**
     * Text key for "Amount"
     */
    public static final String KEY_AMOUNT = "amount";
    /**
     * Text key for "Receipt"
     */
    public static final String KEY_RECEIPT = "receipt";
    /**
     * Text key for "Acceptance point"
     */
    public static final String KEY_ACCEPTANCE_POINT = "acceptance_point";
    /**
     * Text key for "Payable by"
     */
    public static final String KEY_PAYABLE_BY = "payable_by";
    /**
     * Text key for "Payable by (name / address)"
     */
    public static final String KEY_PAYABLE_BY_NAME_ADDRESS = "payable_by_name_addr";
    /**
     * Text key for "DO NOT USE FOR PAYMENT"
     */
    public static final String KEY_DO_NOT_USE_FOR_PAYMENT = "do_not_use_for_payment";


    private static final String[] languageCodes = { "de", "fr", "it", "rm", "en" };

    private static final ResourceBundle[] messageBundles = new ResourceBundle[5];


    private MultilingualText() {
        // Do not create instances
    }

    /**
     * Gets the text for the specified text key in the specified language
     *
     * @param key      text key
     * @param language language
     * @return text
     */
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
            case RM:
                index = 3;
                break;
            default:
                index = 4;
        }

        return index;
    }
}

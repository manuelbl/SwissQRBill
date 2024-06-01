//
// Swiss QR Bill Generator
// Copyright (c) 2023 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.examples;

import net.codecrete.qrbill.generator.*;
import net.sf.jasperreports.engine.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Data source adding a column with the QR bill image.
 * <p>
 * This data source wraps another data source and adds the additional field
 * <i>QrBillImage</i> implementing {@link net.sf.jasperreports.renderers.Renderable}
 * and {@link net.sf.jasperreports.renderers.Graphics2DRenderable}.
 * </p>
 * <p>
 *     The wrapped/inner data source should provide the following fields:
 * </p>
 * <ul>
 *     <li>CreditorName</li>
 *     <li>CreditorStreet – optional</li>
 *     <li>CreditorHouseNo – optional</li>
 *     <li>CreditorPostalCode – optional</li>
 *     <li>CreditorTown – optional</li>
 *     <li>CreditorCountryCode</li>
 *     <li>Account</li>
 *     <li>DebtorName – optional</li>
 *     <li>DebtorStreet – optional</li>
 *     <li>DebtorHouseNo – optional</li>
 *     <li>DebtorPostalCode – optional</li>
 *     <li>DebtorTown – optional</li>
 *     <li>DebtorCountryCode – optional</li>
 *     <li>Reference – optional</li>
 *     <li>UnstructuredMessage – optional</li>
 *     <li>BillInformation – optional</li>
 *     <li>Currency</li>
 *     <li>Amount – optional</li>
 *     <li>Language</li>
 * </ul>
 * <p>
 *     If these fields have different names in the inner data source, the different name
 *     can be specified using {@link #setFieldName(String, String)}. If an optional field
 *     is not used, it can be disabled using {@link #setFieldNotUsed(String)}.
 * </p>
 */
public class QrBillImageDataSource implements JRDataSource {

    public static final String FIELD_QR_BILL_IMAGE = "QrBillImage";

    public static final String INPUT_FIELD_CREDITOR_NAME = "CreditorName";
    public static final String INPUT_FIELD_CREDITOR_STREET = "CreditorStreet";
    public static final String INPUT_FIELD_CREDITOR_HOUSE_NO = "CreditorHouseNo";
    public static final String INPUT_FIELD_CREDITOR_POSTAL_CODE = "CreditorPostalCode";
    public static final String INPUT_FIELD_CREDITOR_TOWN = "CreditorTown";
    public static final String INPUT_FIELD_CREDITOR_COUNTRY_CODE = "CreditorCountryCode";
    public static final String INPUT_FIELD_ACCOUNT = "Account";
    public static final String INPUT_FIELD_DEBTOR_NAME = "DebtorName";
    public static final String INPUT_FIELD_DEBTOR_STREET = "DebtorStreet";
    public static final String INPUT_FIELD_DEBTOR_HOUSE_NO = "DebtorHouseNo";
    public static final String INPUT_FIELD_DEBTOR_POSTAL_CODE = "DebtorPostalCode";
    public static final String INPUT_FIELD_DEBTOR_TOWN = "DebtorTown";
    public static final String INPUT_FIELD_DEBTOR_COUNTRY_CODE = "DebtorCountryCode";
    public static final String INPUT_FIELD_REFERENCE = "Reference";
    public static final String INPUT_FIELD_UNSTRUCTURED_MESSAGE = "UnstructuredMessage";
    public static final String INPUT_FIELD_BILL_INFORMATION = "BillInformation";
    public static final String INPUT_FIELD_CURRENCY = "Currency";
    public static final String INPUT_FIELD_AMOUNT = "Amount";
    public static final String INPUT_FIELD_LANGUAGE = "Language";

    private final JRDataSource innerSource;
    private final Map<String, JRField> fieldNameMap = new HashMap<>();

    private QrBillRenderer qrBillImage;

    private void initFieldNameMap() {
        addFieldNameMapping(INPUT_FIELD_CREDITOR_NAME);
        addFieldNameMapping(INPUT_FIELD_CREDITOR_STREET);
        addFieldNameMapping(INPUT_FIELD_CREDITOR_HOUSE_NO);
        addFieldNameMapping(INPUT_FIELD_CREDITOR_POSTAL_CODE);
        addFieldNameMapping(INPUT_FIELD_CREDITOR_TOWN);
        addFieldNameMapping(INPUT_FIELD_CREDITOR_COUNTRY_CODE);
        addFieldNameMapping(INPUT_FIELD_ACCOUNT);
        addFieldNameMapping(INPUT_FIELD_DEBTOR_NAME);
        addFieldNameMapping(INPUT_FIELD_DEBTOR_STREET);
        addFieldNameMapping(INPUT_FIELD_DEBTOR_HOUSE_NO);
        addFieldNameMapping(INPUT_FIELD_DEBTOR_POSTAL_CODE);
        addFieldNameMapping(INPUT_FIELD_DEBTOR_TOWN);
        addFieldNameMapping(INPUT_FIELD_DEBTOR_COUNTRY_CODE);
        addFieldNameMapping(INPUT_FIELD_REFERENCE);
        addFieldNameMapping(INPUT_FIELD_UNSTRUCTURED_MESSAGE);
        addFieldNameMapping(INPUT_FIELD_BILL_INFORMATION);
        addFieldNameMapping(INPUT_FIELD_CURRENCY);
        addFieldNameMapping(INPUT_FIELD_LANGUAGE);

        putFieldNameMapping(INPUT_FIELD_AMOUNT, INPUT_FIELD_AMOUNT, BigDecimal.class);
    }

    /**
     * Creates a new instance wrapping the given data source.
     *
     * @param innerSource data source
     */
    public QrBillImageDataSource(JRDataSource innerSource) {
        this.innerSource = innerSource;
        initFieldNameMap();
    }

    @Override
    public boolean next() throws JRException {
        var hasNext = innerSource.next();
        if (!hasNext) {
            qrBillImage = null;
            return false;
        }

        createBill();
        return true;
    }

    @Override
    public Object getFieldValue(JRField jrField) throws JRException {
        if (!jrField.getName().equals(FIELD_QR_BILL_IMAGE))
            return innerSource.getFieldValue(jrField);

        return qrBillImage;
    }

    /**
     * Sets the field name in the inner data source.
     * @param expectedName QR bill field name (one of {@code INPUT_FIELD_*} constants)
     * @param effectiveName field name in the inner data source
     */
    public void setFieldName(String expectedName, String effectiveName) {
        if (!fieldNameMap.containsKey(expectedName))
            throw new IllegalArgumentException("Invalid QR bill field name: " + expectedName);

        putFieldNameMapping(expectedName, effectiveName, String.class);
    }

    /**
     * Disables the given field.
     * @param expectedName QR bill field name (one of {@code INPUT_FIELD_*} constants)
     */
    public void setFieldNotUsed(String expectedName) {
        if (!fieldNameMap.containsKey(expectedName))
            throw new IllegalArgumentException("Invalid QR bill field name: " + expectedName);

        fieldNameMap.put(expectedName, null);
    }

    private void putFieldNameMapping(String expectedName, String effectiveName, Class<?> type) {
        fieldNameMap.put(expectedName, makeJRField(effectiveName, type));
    }

    private void addFieldNameMapping(String expectedName) {
        fieldNameMap.put(expectedName, makeJRField(expectedName, String.class));
    }

    private static JRField makeJRField(String name, Class<?> type) {
        return new SimpleField(name, type);
    }

    protected String getStringValue(String fieldName) throws JRException {
        var field = fieldNameMap.get(fieldName);
        if (field == null)
            return null;
        return innerSource.getFieldValue(field).toString();
    }

    protected BigDecimal getAmount() throws JRException {
        var field = fieldNameMap.get(INPUT_FIELD_AMOUNT);
        if (field == null)
            return null;
        return (BigDecimal) innerSource.getFieldValue(field);
    }

    protected void createBill() throws JRException {
        var bill = new Bill();

        var creditor = new Address();
        creditor.setName(getStringValue(INPUT_FIELD_CREDITOR_NAME));
        creditor.setStreet(getStringValue(INPUT_FIELD_CREDITOR_STREET));
        creditor.setHouseNo(getStringValue(INPUT_FIELD_CREDITOR_HOUSE_NO));
        creditor.setPostalCode(getStringValue(INPUT_FIELD_CREDITOR_POSTAL_CODE));
        creditor.setTown(getStringValue(INPUT_FIELD_CREDITOR_TOWN));
        creditor.setCountryCode(getStringValue(INPUT_FIELD_CREDITOR_COUNTRY_CODE));
        bill.setCreditor(creditor);
        bill.setAccount(getStringValue(INPUT_FIELD_ACCOUNT));

        var debtor = new Address();
        debtor.setName(getStringValue(INPUT_FIELD_DEBTOR_NAME));
        debtor.setStreet(getStringValue(INPUT_FIELD_DEBTOR_STREET));
        debtor.setHouseNo(getStringValue(INPUT_FIELD_DEBTOR_HOUSE_NO));
        debtor.setPostalCode(getStringValue(INPUT_FIELD_DEBTOR_POSTAL_CODE));
        debtor.setTown(getStringValue(INPUT_FIELD_DEBTOR_TOWN));
        debtor.setCountryCode(getStringValue(INPUT_FIELD_DEBTOR_COUNTRY_CODE));
        bill.setDebtor(debtor);

        bill.setReference(getStringValue(INPUT_FIELD_REFERENCE));
        bill.setUnstructuredMessage(getStringValue(INPUT_FIELD_UNSTRUCTURED_MESSAGE));
        bill.setBillInformation(getStringValue(INPUT_FIELD_BILL_INFORMATION));

        bill.setCurrency(getStringValue(INPUT_FIELD_CURRENCY));
        bill.setAmount(getAmount());

        bill.getFormat().setLanguage(Language.valueOf(getStringValue(INPUT_FIELD_LANGUAGE)));
        bill.getFormat().setGraphicsFormat(GraphicsFormat.SVG);
        bill.getFormat().setOutputSize(OutputSize.QR_BILL_EXTRA_SPACE);

        qrBillImage = new QrBillRenderer(bill);
    }

    static class SimpleField implements JRField {
        private final String name;
        private final Class<?> type;

        public SimpleField(String name, Class<?> type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Class<?> getValueClass() {
            return type;
        }

        @Override
        public String getDescription() {
            return "Field " + name;
        }

        @Override
        public void setDescription(String description) {

        }

        @Override
        public String getValueClassName() {
            return type.getName();
        }

        @Override
        public JRPropertyExpression[] getPropertyExpressions() {
            return new JRPropertyExpression[0];
        }

        @Override
        public boolean hasProperties() {
            return false;
        }

        @Override
        public JRPropertiesMap getPropertiesMap() {
            return null;
        }

        @Override
        public JRPropertiesHolder getParentProperties() {
            return null;
        }

        @Override
        public Object clone() {
            try {
                return super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

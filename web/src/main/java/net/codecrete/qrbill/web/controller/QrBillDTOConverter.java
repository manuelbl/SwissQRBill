//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web.controller;

import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.GraphicsFormat;
import net.codecrete.qrbill.generator.Language;
import net.codecrete.qrbill.generator.OutputSize;
import net.codecrete.qrbill.generator.SeparatorType;
import net.codecrete.qrbill.web.model.Address;
import net.codecrete.qrbill.web.model.AlternativeScheme;
import net.codecrete.qrbill.web.model.BillFormat;
import net.codecrete.qrbill.web.model.QrBill;
import net.codecrete.qrbill.web.model.ValidationMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class QrBillDTOConverter {

    private QrBillDTOConverter() {
        // do not instantiate
    }

    static QrBill toDTOQrBill(Bill bill) {
        if (bill == null)
            return null;

        QrBill dto = new QrBill();
        dto.setVersion(bill.getVersion().name());
        dto.setAmount(bill.getAmount());
        dto.setCurrency(bill.getCurrency());
        dto.setAccount(bill.getAccount());
        dto.setCreditor(toDtoAddress(bill.getCreditor()));
        dto.setReference(bill.getReference());
        dto.setUnstructuredMessage(bill.getUnstructuredMessage());
        dto.setBillInformation(bill.getBillInformation());
        dto.setAlternativeSchemes(toDtoSchemes(bill.getAlternativeSchemes()));
        dto.setDebtor(toDtoAddress(bill.getDebtor()));
        dto.setFormat(toDtoBillFormat(bill.getFormat()));
        return dto;
    }

    static Bill fromDtoQrBill(QrBill dto) {
        if (dto == null)
            return null;

        Bill bill = new Bill();
        bill.setVersion(net.codecrete.qrbill.generator.Bill.Version.valueOf(dto.getVersion()));
        bill.setAmount(dto.getAmount());
        bill.setCurrency(dto.getCurrency());
        bill.setAccount(dto.getAccount());
        bill.setCreditor(fromDtoAddress(dto.getCreditor()));
        bill.setReference(dto.getReference());
        bill.setUnstructuredMessage(dto.getUnstructuredMessage());
        bill.setBillInformation(dto.getBillInformation());
        bill.setAlternativeSchemes(fromDtoSchemes(dto.getAlternativeSchemes()));
        bill.setDebtor(fromDtoAddress(dto.getDebtor()));
        bill.setFormat(fromDtoBillFormat(dto.getFormat()));
        return bill;
    }

    private static Address toDtoAddress(net.codecrete.qrbill.generator.Address address) {
        if (address == null)
            return null;

        Address dto = new Address();
        dto.setAddressType(Address.AddressTypeEnum.valueOf(address.getType().name()));
        dto.setName(address.getName());
        dto.setAddressLine1(address.getAddressLine1());
        dto.setAddressLine2(address.getAddressLine2());
        dto.setStreet(address.getStreet());
        dto.setHouseNo(address.getHouseNo());
        dto.setPostalCode(address.getPostalCode());
        dto.setTown(address.getTown());
        dto.setCountryCode(address.getCountryCode());
        return dto;
    }

    private static net.codecrete.qrbill.generator.Address fromDtoAddress(Address dto) {
        if (dto == null)
            return null;

        net.codecrete.qrbill.generator.Address address = new net.codecrete.qrbill.generator.Address();
        address.setName(dto.getName());
        if (dto.getAddressLine1() != null)
            address.setAddressLine1(dto.getAddressLine1());
        if (dto.getAddressLine2() != null)
            address.setAddressLine2(dto.getAddressLine2());
        if (dto.getStreet() != null)
            address.setStreet(dto.getStreet());
        if (dto.getHouseNo() != null)
            address.setHouseNo(dto.getHouseNo());
        if (dto.getPostalCode() != null)
            address.setPostalCode(dto.getPostalCode());
        if (dto.getTown() != null)
            address.setTown(dto.getTown());
        address.setCountryCode(dto.getCountryCode());
        return address;
    }

    static BillFormat toDtoBillFormat(net.codecrete.qrbill.generator.BillFormat format) {
        if (format == null)
            return null;

        BillFormat dto = new BillFormat();
        dto.setOutputSize(toDtoOutputSize(format.getOutputSize()));
        dto.setLanguage(toDtoLanguage(format.getLanguage()));
        dto.setSeparatorType(toDtoSeparatorType(format.getSeparatorType()));
        dto.setFontFamily(format.getFontFamily());
        dto.setGraphicsFormat(toDtoGraphicsFormat(format.getGraphicsFormat()));
        return dto;
    }

    static net.codecrete.qrbill.generator.BillFormat fromDtoBillFormat(BillFormat dto) {
        if (dto == null)
            return null;

        net.codecrete.qrbill.generator.BillFormat format = new net.codecrete.qrbill.generator.BillFormat();
        format.setOutputSize(fromDtoOutputSize(dto.getOutputSize()));
        format.setLanguage(fromDtoLanguage(dto.getLanguage()));
        format.setSeparatorType(fromDtoSeparatorType(dto.getSeparatorType()));
        format.setFontFamily(dto.getFontFamily());
        format.setGraphicsFormat(fromDtoGraphicsFormat(dto.getGraphicsFormat()));
        return format;
    }

    private static List<AlternativeScheme> toDtoSchemes(net.codecrete.qrbill.generator.AlternativeScheme[] alternativeSchemes) {
        if (alternativeSchemes == null)
            return null; // NOSONAR

        List<AlternativeScheme> dto = new ArrayList<>();
        for (net.codecrete.qrbill.generator.AlternativeScheme scheme : alternativeSchemes) {
            AlternativeScheme dtoScheme = null;
            if (scheme != null) {
                dtoScheme = new AlternativeScheme();
                dtoScheme.setName(scheme.getName());
                dtoScheme.setInstruction(scheme.getInstruction());
            }
            dto.add(dtoScheme);
        }
        return dto;
    }

    private static net.codecrete.qrbill.generator.AlternativeScheme[] fromDtoSchemes(List<AlternativeScheme> dto) {
        if (dto == null)
            return null; // NOSONAR

        net.codecrete.qrbill.generator.AlternativeScheme[] schemes
                = new net.codecrete.qrbill.generator.AlternativeScheme[dto.size()];
        for (int i = 0; i < dto.size(); i++) {
            AlternativeScheme dtoScheme = dto.get(i);
            if (dtoScheme != null) {
                net.codecrete.qrbill.generator.AlternativeScheme scheme
                        = new net.codecrete.qrbill.generator.AlternativeScheme();
                scheme.setName(dtoScheme.getName());
                scheme.setInstruction(dtoScheme.getInstruction());
                schemes[i] = scheme;
            }
        }

        return schemes;
    }

    private static ValidationMessage toDtoValidationMessage(net.codecrete.qrbill.generator.ValidationMessage msg) {
        if (msg == null)
            return null;

        ValidationMessage dto = new ValidationMessage();
        dto.setType(ValidationMessage.TypeEnum.valueOf(msg.getType().name()));
        dto.setField(msg.getField());
        dto.setMessageKey(msg.getMessageKey());
        if (msg.getMessageParameters() != null)
            dto.setMessageParameters(Arrays.asList(msg.getMessageParameters()));
        return dto;
    }

    static List<ValidationMessage> toDtoValidationMessageList(List<net.codecrete.qrbill.generator.ValidationMessage> list) {
        List<ValidationMessage> dtoList = new ArrayList<>(list.size());
        for (net.codecrete.qrbill.generator.ValidationMessage msg : list) {
            dtoList.add(toDtoValidationMessage(msg));
        }

        return dtoList;
    }

    private static BillFormat.OutputSizeEnum toDtoOutputSize(OutputSize outputSize) {
        if (outputSize == null)
            return null;
        return BillFormat.OutputSizeEnum.valueOf(outputSize.name());
    }

    private static OutputSize fromDtoOutputSize(BillFormat.OutputSizeEnum outputSize) {
        if (outputSize == null)
            return null;
        return OutputSize.valueOf(outputSize.name());
    }

    private static BillFormat.LanguageEnum toDtoLanguage(Language language) {
        if (language == null)
            return null;
        return BillFormat.LanguageEnum.valueOf(language.name());
    }

    private static Language fromDtoLanguage(BillFormat.LanguageEnum language) {
        if (language == null)
            return null;
        return Language.valueOf(language.name());
    }

    private static BillFormat.GraphicsFormatEnum toDtoGraphicsFormat(GraphicsFormat graphicsFormat) {
        if (graphicsFormat == null)
            return null;
        return BillFormat.GraphicsFormatEnum.valueOf(graphicsFormat.name());
    }

    private static GraphicsFormat fromDtoGraphicsFormat(BillFormat.GraphicsFormatEnum graphicsFormat) {
        if (graphicsFormat == null)
            return null;
        return GraphicsFormat.valueOf(graphicsFormat.name());
    }

    private static BillFormat.SeparatorTypeEnum toDtoSeparatorType(SeparatorType separatorType) {
        if (separatorType == null)
            return null;
        return BillFormat.SeparatorTypeEnum.valueOf(separatorType.name());
    }

    private static SeparatorType fromDtoSeparatorType(BillFormat.SeparatorTypeEnum separatorType) {
        if (separatorType == null)
            return null;
        return SeparatorType.valueOf(separatorType.name());
    }
}

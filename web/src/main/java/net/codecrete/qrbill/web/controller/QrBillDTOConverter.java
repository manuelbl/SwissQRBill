//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web.controller;

import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.web.model.Address;
import net.codecrete.qrbill.web.model.AlternativeScheme;
import net.codecrete.qrbill.web.model.QrBill;
import net.codecrete.qrbill.web.model.ValidationMessage;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class QrBillDTOConverter {

    static QrBill toDTOQrBill(Bill bill) {
        if (bill == null)
            return null;

        QrBill dto = new QrBill();
        dto.setLanguage(QrBill.LanguageEnum.valueOf(bill.getLanguage().name()));
        dto.setVersion(bill.getVersion().name());
        dto.setAmount(bill.getAmount() != null ? new BigDecimal(bill.getAmount(), MathContext.DECIMAL32) : null);
        dto.setCurrency(bill.getCurrency());
        dto.setAccount(bill.getAccount());
        dto.setCreditor(toDtoAddress(bill.getCreditor()));
        dto.setReferenceNo(bill.getReferenceNo());
        dto.setUnstructuredMessage(bill.getUnstructuredMessage());
        dto.setBillInformation(bill.getBillInformation());
        dto.setAlternativeSchemes(toDtoSchemes(bill.getAlternativeSchemes()));
        dto.setDebtor(toDtoAddress(bill.getDebtor()));
        return dto;
    }

    static Bill fromDtoQrBill(QrBill dto) {
        if (dto == null)
            return null;

        Bill bill = new Bill();
        bill.setLanguage(Bill.Language.valueOf(dto.getLanguage().name()));
        bill.setVersion(net.codecrete.qrbill.generator.Bill.Version.valueOf(dto.getVersion()));
        bill.setAmount(dto.getAmount() != null ? dto.getAmount().doubleValue() : null);
        bill.setCurrency(dto.getCurrency());
        bill.setAccount(dto.getAccount());
        bill.setCreditor(fromDtoAddress(dto.getCreditor()));
        bill.setReferenceNo(dto.getReferenceNo());
        bill.setUnstructuredMessage(dto.getUnstructuredMessage());
        bill.setBillInformation(dto.getBillInformation());
        bill.setAlternativeSchemes(fromDtoSchemes(dto.getAlternativeSchemes()));
        bill.setDebtor(fromDtoAddress(dto.getDebtor()));
        return bill;
    }

    private static Address toDtoAddress(net.codecrete.qrbill.generator.Address address) {
        if (address == null)
            return null;

        Address dto = new Address();
        dto.setAddressType(toDtoAddressType(address.getType()));
        dto.setName(address.getName());
        dto.setAddressType(Address.AddressTypeEnum.STRUCTURED);
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

    private static List<AlternativeScheme> toDtoSchemes(net.codecrete.qrbill.generator.AlternativeScheme[] alternativeSchemes) {
        if (alternativeSchemes == null)
            return null;

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
            return null;

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
        dto.setType(toDtoMessageType(msg.getType()));
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

    private static ValidationMessage.TypeEnum toDtoMessageType(net.codecrete.qrbill.generator.ValidationMessage.Type type) {
        if (type == net.codecrete.qrbill.generator.ValidationMessage.Type.ERROR)
            return ValidationMessage.TypeEnum.ERROR;
        if (type == net.codecrete.qrbill.generator.ValidationMessage.Type.WARNING)
            return ValidationMessage.TypeEnum.WARNING;
        return null;
    }

    private static Address.AddressTypeEnum toDtoAddressType(net.codecrete.qrbill.generator.Address.Type type) {
        if (type == net.codecrete.qrbill.generator.Address.Type.STRUCTURED) {
            return Address.AddressTypeEnum.STRUCTURED;
        } else if (type == net.codecrete.qrbill.generator.Address.Type.COMBINED_ELEMENTS) {
            return Address.AddressTypeEnum.COMBINED_ELEMENTS;
        } else if (type == net.codecrete.qrbill.generator.Address.Type.CONFLICTING) {
            return Address.AddressTypeEnum.CONFLICTING;
        }
        return Address.AddressTypeEnum.UNDETERMINED;
    }
}

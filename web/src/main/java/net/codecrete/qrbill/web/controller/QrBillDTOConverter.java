//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web.controller;

import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.web.model.Address;
import net.codecrete.qrbill.web.model.QrBill;
import net.codecrete.qrbill.web.model.ValidationMessage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class QrBillDTOConverter {

    static QrBill toDTOQrBill(Bill bill) {
        QrBill dto = new QrBill();
        dto.setLanguage(QrBill.LanguageEnum.valueOf(bill.getLanguage().name()));
        dto.setVersion(bill.getVersion().name());
        dto.setAmount(new BigDecimal(bill.getAmount()));
        dto.setCurrency(bill.getCurrency());
        dto.setAccount(bill.getAccount());
        dto.setCreditor(toDtoAddress(bill.getCreditor()));
        dto.setFinalCreditor(toDtoAddress(bill.getFinalCreditor()));
        dto.setReferenceNo(bill.getReferenceNo());
        dto.setAdditionalInfo(bill.getAdditionalInfo());
        dto.setDebtor(toDtoAddress(bill.getDebtor()));
        dto.setDueDate(bill.getDueDate());
        return dto;
    }

    static Bill fromDtoQrBill(QrBill dto) {
        if (dto == null)
            return null;

        Bill bill = new Bill();
        bill.setLanguage(Bill.Language.valueOf(dto.getLanguage().name()));
        bill.setVersion(net.codecrete.qrbill.generator.Bill.Version.valueOf(dto.getVersion()));
        bill.setAmount(dto.getAmount().doubleValue());
        bill.setCurrency(dto.getCurrency());
        bill.setAccount(dto.getAccount());
        bill.setCreditor(fromDtoAddress(dto.getCreditor()));
        bill.setFinalCreditor(fromDtoAddress(dto.getFinalCreditor()));
        bill.setReferenceNo(dto.getReferenceNo());
        bill.setAdditionalInfo(dto.getAdditionalInfo());
        bill.setDebtor(fromDtoAddress(dto.getDebtor()));
        bill.setDueDate(dto.getDueDate());
        return bill;
    }

    private static Address toDtoAddress(net.codecrete.qrbill.generator.Address address) {
        if (address == null)
            return null;

        Address dto = new Address();
        dto.setName(address.getName());
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
        address.setStreet(dto.getStreet());
        address.setHouseNo(dto.getHouseNo());
        address.setPostalCode(dto.getPostalCode());
        address.setTown(dto.getTown());
        address.setCountryCode(dto.getCountryCode());
        return address;
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
}

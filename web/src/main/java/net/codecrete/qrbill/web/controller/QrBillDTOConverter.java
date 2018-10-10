//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web.controller;

import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.web.api.Address;
import net.codecrete.qrbill.web.api.QrBill;
import net.codecrete.qrbill.web.api.ValidationMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class QrBillDTOConverter {

    static QrBill toDTOQrBill(Bill bill) {
        QrBill dto = new QrBill();
        dto.setLanguage(toDtoLanguage(bill.getLanguage()));
        dto.setVersion(QrBill.Version.valueOf(bill.getVersion().name()));
        dto.setAmount(bill.getAmount());
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
        bill.setLanguage(fromDtoLanguage(dto.getLanguage()));
        bill.setVersion(net.codecrete.qrbill.generator.Bill.Version.valueOf(dto.getVersion().name()));
        bill.setAmount(dto.getAmount());
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

    private static QrBill.Language toDtoLanguage(net.codecrete.qrbill.generator.Bill.Language language) {
        String name = language.name();
        name = name.toLowerCase(Locale.US);
        return QrBill.Language.valueOf(name);
    }

    private static net.codecrete.qrbill.generator.Bill.Language fromDtoLanguage(QrBill.Language language) {
        String name = language.name();
        name = name.toUpperCase(Locale.US);
        return net.codecrete.qrbill.generator.Bill.Language.valueOf(name);
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
        dto.setType(typeFromUppercase(msg.getType().name()));
        dto.setField(msg.getField());
        dto.setMessageKey(msg.getMessageKey());
        dto.setMessageParameters(msg.getMessageParameters());
        return dto;
    }

    static List<ValidationMessage> toDtoValidationMessageList(List<net.codecrete.qrbill.generator.ValidationMessage> list) {
        List<ValidationMessage> dtoList = new ArrayList<>(list.size());
        for (net.codecrete.qrbill.generator.ValidationMessage msg : list) {
            dtoList.add(toDtoValidationMessage(msg));
        }

        return dtoList;
    }

    private static ValidationMessage.Type typeFromUppercase(String name) {
        if ("ERROR".equals(name))
            return ValidationMessage.Type.Error;
        if ("WARNING".equals(name))
            return ValidationMessage.Type.Warning;
        return null;
    }
}

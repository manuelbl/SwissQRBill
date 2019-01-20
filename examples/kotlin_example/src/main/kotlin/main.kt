//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

@file:JvmName("Example")

package net.codecrete.qrbill.examples.kotlin

import net.codecrete.qrbill.generator.Address
import net.codecrete.qrbill.generator.Bill
import net.codecrete.qrbill.generator.QRBill
import java.nio.file.Files
import java.nio.file.Paths

fun main(args : Array<String>) {

    // Setup bill
    val bill = Bill()
    bill.account = "CH4431999123000889012"
    bill.setAmountFromDouble(199.95)
    bill.currency = "CHF"

    // Set creditor
    val creditor = Address()
    creditor.name = "Robert Schneider AG"
    creditor.addressLine1 = "Rue du Lac 1268/2/22"
    creditor.addressLine2 = "2501 Biel"
    creditor.countryCode = "CH"
    bill.creditor = creditor

    // more bill data
    bill.reference = "210000000003139471430009017"
    bill.unstructuredMessage = "Abonnment für 2020"

    // Set debtor
    val debtor = Address()
    debtor.name = "Pia-Maria Rutschmann-Schnyder"
    debtor.addressLine1 = "Grosse Marktgasse 28"
    debtor.addressLine2 = "9400 Rorschach"
    debtor.countryCode = "CH"
    bill.debtor = debtor

    // Generate QR bill
    val svg = QRBill.generate(bill)

    val path = Paths.get("qrbill.svg")
    Files.write(path, svg)

    println("QR bill saved at ${path.toAbsolutePath()} ")
}
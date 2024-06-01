//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

@file:JvmName("Example")

package net.codecrete.qrbill.examples.kotlin

import net.codecrete.qrbill.generator.*
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Console application for generating a QR bill.
 *
 * The QR bill is saved as an SVG file in the working directory.
 * The path of the working directory is printed to <c>stdout</c>.
 */
fun main() {

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
    bill.unstructuredMessage = "Abonnement für 2020"

    // Set debtor
    val debtor = Address()
    debtor.name = "Pia-Maria Rutschmann-Schnyder"
    debtor.addressLine1 = "Grosse Marktgasse 28"
    debtor.addressLine2 = "9400 Rorschach"
    debtor.countryCode = "CH"
    bill.debtor = debtor

    // Set output format
    val format = BillFormat()
    format.graphicsFormat = GraphicsFormat.SVG
    format.outputSize = OutputSize.QR_BILL_ONLY
    format.language = Language.DE
    bill.format = format

    // Generate QR bill
    val svg = QRBill.generate(bill)

    val path = Paths.get("qrbill.svg")
    Files.write(path, svg)

    println("QR bill saved at ${path.toAbsolutePath()}")
}

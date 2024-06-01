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
    val bill = Bill().apply {
        account = "CH4431999123000889012"
        setAmountFromDouble(199.95)
        currency = "CHF"

        // creditor
        creditor = Address().apply {
            name = "Robert Schneider AG"
            street = "Rue du Lac"
            houseNo = "1268/2/22"
            postalCode = "2501"
            town = "Biel"
            countryCode = "CH"
        }

        // more bill data
        reference = "210000000003139471430009017"
        unstructuredMessage = "Abonnement für 2020"

        // debtor
        debtor = Address().apply {
            name = "Pia-Maria Rutschmann-Schnyder"
            street = "Grosse Marktgasse"
            houseNo = "28"
            postalCode = "9400"
            town = "Rorschach"
            countryCode = "CH"
        }

        // output format
        format = BillFormat().apply {
            graphicsFormat = GraphicsFormat.SVG
            outputSize = OutputSize.QR_BILL_ONLY
            language = Language.DE
        }
    }

    // Generate QR bill
    val svg = QRBill.generate(bill)

    val path = Paths.get("qrbill.svg")
    Files.write(path, svg)

    println("QR bill saved at ${path.toAbsolutePath()}")
}

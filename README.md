# Swiss QR Bill for Java

[![javadoc](https://javadoc.io/badge2/net.codecrete.qrbill/qrbill-generator/javadoc.svg)](https://javadoc.io/doc/net.codecrete.qrbill/qrbill-generator)

Open-source Java library to generate and decode Swiss QR bills (jointly developed with the [.NET version](https://github.com/manuelbl/SwissQRBill.NET)).

Try it yourself and [create a QR bill](https://www.codecrete.net/qrbill). The code for this demonstration (Angular UI and RESTful service) can be found on [GitHub](https://github.com/manuelbl/SwissQRBillDemo) as well. 

This library implements version 2.1 of the *Swiss Implementation Guidelines QR-bill* valid since September 30, 2019, the *Style guide* released in January 2019 and *Swico Syntax Definition (S1)* from November 11, 2018.

## Introduction

The Swiss QR bill is the new QR code based payment format that started on 30 June, 2020. The old payment slip will no longer be accepted after 30 September 2022.

The new payment slip will be sent electronically in most cases. But it can still be printed at the bottom of an invoice or added to the invoice on a separate sheet. The payer scans the QR code with his/her mobile banking app to initiate the payment. The payment just needs to be confirmed.

 If the invoicing party adds structured bill information (VAT rates, payment conditions etc.) to the QR bill, the payer can automate booking in accounts payable. The invoicing party can also automate the accounts receivable processing as the payment includes all relevant data including a reference number. The Swiss QR bill is convenient for the payer and payee.

![QR Bill](https://raw.githubusercontent.com/wiki/manuelbl/SwissQRBill/images/qr-invoice-e1.svg?sanitize=true)

*More [examples](https://github.com/manuelbl/SwissQRBill/wiki/Swiss-QR-Invoice-Examples) can be found in the [Wiki](https://github.com/manuelbl/SwissQRBill/wiki)*

## Features

The Swiss QR bill library:

- generates QR bills as PDF, SVG and PNG files
- adds a QR bill to existing PDF file
- generates payment slip (105mm by 210mm), A4 sheets or QR code only
- is multilingual: German, French, Italian, English, Romansh
- validates the invoice data and provides detailed validation information
- adds or retrieves structured bill information (according to Swico S1)
- parses the invoice data embedded in the QR code
- is easy to use (see example below)
- is small and fast
- is free – even for commecial use (MIT License)
- has only two dependencies (PDFBox and Nayuki's QR code generator)
- is available on Maven Central

## Getting started

The QR bill generator is available at Maven Central. To use it, just add it to your Maven or Gradle project.

If you are using *Maven*, add the below dependency to your `pom.xml`:

    <dependency>
        <groupId>net.codecrete.qrbill</groupId>
        <artifactId>qrbill-generator</artifactId>
        <version>[3.0.0,3.999999]</version>
    </dependency>

If you are using *Gradle*, add the below dependency to your *build.gradle* file:

    compile group: 'net.codecrete.qrbill', name: 'qrbill-generator', version: '3.0.0+'

To generate a QR bill, you first fill in the `Bill` data structure and then call `QRBill.generate`:

    package net.codecrete.qrbill.examples;
    
    import net.codecrete.qrbill.generator.Address;
    import net.codecrete.qrbill.generator.Bill;
    import net.codecrete.qrbill.generator.QRBill;
    
    import java.io.IOException;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    
    public class QRBillExample {
    
        public static void main(String[] args) {
    
            // Setup bill
            Bill bill = new Bill();
            bill.setAccount("CH4431999123000889012");
            bill.setAmountFromDouble(199.95);
            bill.setCurrency("CHF");
    
            // Set creditor
            Address creditor = new Address();
            creditor.setName("Robert Schneider AG");
            creditor.setAddressLine1("Rue du Lac 1268/2/22");
            creditor.setAddressLine2("2501 Biel");
            creditor.setCountryCode("CH");
            bill.setCreditor(creditor);
    
            // more bill data
            bill.setReference("210000000003139471430009017");
            bill.setUnstructuredMessage("Abonnement für 2020");
    
            // Set debtor
            Address debtor = new Address();
            debtor.setName("Pia-Maria Rutschmann-Schnyder");
            debtor.setAddressLine1("Grosse Marktgasse 28");
            debtor.setAddressLine2("9400 Rorschach");
            debtor.setCountryCode("CH");
            bill.setDebtor(debtor);
    
            // Set output format
            BillFormat format = bill.getFormat();
            format.setGraphicsFormat(GraphicsFormat.SVG);
            format.setOutputSize(OutputSize.QR_BILL_ONLY);
            format.setLanguage(Language.DE);

            // Generate QR bill
            byte[] svg = QRBill.generate(bill);
    
            // Save QR bill
            Path path = Paths.get("qrbill.svg");
            try {
                Files.write(path, svg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    
            System.out.println("QR bill saved at " + path.toAbsolutePath());
        }
    }

## API Documention

[Javadoc API documentation](https://javadoc.io/doc/net.codecrete.qrbill/qrbill-generator):

- [QRBill](https://javadoc.io/doc/net.codecrete.qrbill/qrbill-generator/latest/net/codecrete/qrbill/generator/QRBill.html) class – generates Swiss QR bill
- [Bill](https://javadoc.io/doc/net.codecrete.qrbill/qrbill-generator/latest/net/codecrete/qrbill/generator/Bill.html) class – holds bill data
- [Address](https://javadoc.io/doc/net.codecrete.qrbill/qrbill-generator/latest/net/codecrete/qrbill/generator/Address.html) class – holds address data
- [BillFormat](https://javadoc.io/doc/net.codecrete.qrbill/qrbill-generator/latest/net/codecrete/qrbill/generator/BillFormat.html) class – controls QR bill formatting
- and many more


## More information

More information can be found in the [Wiki](https://github.com/manuelbl/SwissQRBill/wiki). It's the joint Wiki for the .NET and the Java version.

## QR Code

For the generation of the QR code itself, [Nayuki's QR code generator](https://github.com/nayuki/QR-Code-generator) is used.

## Other programming languages

A [.NET version](https://github.com/manuelbl/SwissQRBill.NET) of this library is also available. If you are looking for a library for yet another programming language or for a library with professional services, you might want to check out [Services & Tools](https://www.moneytoday.ch/iso20022/movers-shakers/software-hersteller/services-tools/) on [MoneyToday.ch](https://www.moneytoday.ch).

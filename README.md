# Swiss QR Bill

Open-source Java library to generate Swiss QR bills.

For demonstration pruposes, there is code for an Angular UI and a web service in addition to the Java library. Try it yourself and [create a QR bill](https://www.codecrete.net/qrbill).

## Introduction

The Swiss QR bill is the new QR code based payment format that will replace the current payment slip starting at 30 June, 2020. The new payment slip with the QR code is either directly printed at the bottom of an invoice or added to the invoice on a separate sheet. The payer scans the QR code with his/her mobile banking app to initiate the payment. No other data needs to be entered. The payment just needs to be confirmed.

The invoicing party can easily synchronize the received payment with the accounts-receivable accounting as they payment comes with a full set of data including the reference number used on the invoice. So the Swiss QR bill is convenient for the payer and payee.

![QR Bill](https://raw.githubusercontent.com/wiki/manuelbl/SwissQRBill/images/qr-invoice-e1.svg?sanitize=true)

*More [examples](https://github.com/manuelbl/SwissQRBill/wiki/Swiss-QR-Invoice-Examples) can be found in the [Wiki](https://github.com/manuelbl/SwissQRBill/wiki)*

## Features

The Swiss QR bill library:

- generates PDF, SVG and PNG files
- generates A6, A5 and A4 sheets and QR code only
- multilingual: German, French, Italian, English
- validates the invoice data and provides detailed validation information
- can parse the invoice data embedded in the QR code
- is easy to use (see example below)
- is small and fast
- has a single dependency (PDFBox)
- is free – even for commecial use (MIT License)
- is available on Maven Central

**Note**: This library implements version 1.0 of the *Swiss Implementation Guidelines QR-bill*. The document is currently undergoing a revsion. A new version is expected by mid of November 2018. The library will be updated by the end of 2018 to conform to the revised version of the document.

## Getting started

The QR bill generator is available at Maven Central. To use it, just add it to your Maven or Gradle project.

If you are using *Maven*, add the below dependency to your `pom.xml`:

    <dependency>
        <groupId>net.codecrete.qrbill</groupId>
        <artifactId>qrbill-generator</artifactId>
        <version>1.0.0</version>
    </dependency>

If you are using *Gradle*, add the below dependency to your *build.gradle* file:

    compile group: 'net.codecrete.qrbill', name: 'qrbill-generator', version: '1.0.0'

To generate a QR bill, you first fill in the `Bill` data structure and then call `QRBill.generate`:

    package net.codecrete.qrbill.examples;

    import net.codecrete.qrbill.generator.Address;
    import net.codecrete.qrbill.generator.Bill;
    import net.codecrete.qrbill.generator.QRBill;

    import java.io.IOException;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.time.LocalDate;

    public class QRBillExample {

        public static void main(String[] args) {

            // Setup bill
            Bill bill = new Bill();
            bill.setLanguage(Bill.Language.FR);
            bill.setAccount("CH4431999123000889012");
            bill.setAmount(199.95);
            bill.setCurrency("CHF");

            // Set creditor
            Address creditor = new Address();
            creditor.setName("Robert Schneider AG");
            creditor.setStreet("Rue du Lac");
            creditor.setHouseNo("1268/2/22");
            creditor.setPostalCode("2501");
            creditor.setTown("Biel");
            creditor.setCountryCode("CH");
            bill.setCreditor(creditor);

            // Set final creditor
            Address finalCreditor = new Address();
            finalCreditor.setName("Robert Schneider Services Switzerland AG");
            finalCreditor.setStreet("Rue du Lac");
            finalCreditor.setHouseNo("1268/3/1");
            finalCreditor.setPostalCode("2501");
            finalCreditor.setTown("Biel");
            finalCreditor.setCountryCode("CH");
            bill.setFinalCreditor(finalCreditor);

            // more bill data
            bill.setDueDate(LocalDate.of(2019, 10, 31));
            bill.setReference("RF18539007547034");
            bill.setAdditionalInfo(null);

            // Set debtor
            Address debtor = new Address();
            debtor.setName("Pia-Maria Rutschmann-Schnyder");
            debtor.setStreet("Grosse Marktgasse");
            debtor.setHouseNo("28");
            debtor.setPostalCode("9400");
            debtor.setTown("Rorschach");
            debtor.setCountryCode("CH");
            bill.setDebtor(debtor);

            byte[] svg = QRBill.generate(bill, QRBill.BillFormat.A6_LANDSCAPE_SHEET, QRBill.GraphicsFormat.SVG);

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

See Javadoc [API Documentation](https://www.codecrete.net/qrbill-javadoc/).

## More information

More information can be found in the [Wiki](https://github.com/manuelbl/SwissQRBill/wiki).

## QR Code

For the generation of the QR code itself, [Nayuki's QR code generator](https://github.com/nayuki/QR-Code-generator) is used.
As it is not available on Maven Central, it is included in this library (with a modified package name to avoid conflicts).
The library has also been released under the MIT license.

## Other programming languages

If you are looking for a library for a different programming language or for a library with professional services, you might want to check out [Services & Tools](https://www.moneytoday.ch/iso20022/movers-shakers/software-hersteller/services-tools/) on [MoneyToday.ch](https://www.moneytoday.ch).

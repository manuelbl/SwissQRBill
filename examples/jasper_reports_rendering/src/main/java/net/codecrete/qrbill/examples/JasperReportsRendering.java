//
// Swiss QR Bill Generator
// Copyright (c) 2023 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.examples;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRCsvDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

import java.io.File;
import java.io.IOException;

/**
 * Demo application showing to use of JasperReports to generate QR bills.
 */
public class JasperReportsRendering
{
    public static void main(String[] args) throws JRException, IOException {
        var outputFile = new File("invoices.pdf");
        var jasperPrint = JasperFillManager.fillReport(getCompiledReport(), null, getDataSource());
        JasperExportManager.exportReportToPdfFile(jasperPrint, outputFile.getPath());
        System.out.printf("%d bytes written to %s.%n", outputFile.length(), outputFile);
    }

    private static JRDataSource getDataSource() throws JRException
    {
        var is = JRLoader.getLocationInputStream("data/InvoiceData.csv");
        if (is == null)
            throw new RuntimeException("Resource data/InvoiceData.csv not found");
        JRCsvDataSource ds = new JRCsvDataSource(is);
        ds.setUseFirstRowAsHeader(true);
        ds.setRecordDelimiter("\r\n");

        return new QrBillImageDataSource(ds);
    }

    private static JasperReport getCompiledReport() throws JRException, IOException {
        try (var reportStream = JRLoader.getLocationInputStream("jasper/invoices.jasper")) {
            if (reportStream == null)
                throw new RuntimeException("Resource jasper/invoices.jasper not found");
            return (JasperReport) JRLoader.loadObject(reportStream);
        }
    }
}

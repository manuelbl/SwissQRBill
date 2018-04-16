//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.examples.perftest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.QRBill;
import net.codecrete.qrbill.generator.QRBill.BillFormat;
import net.codecrete.qrbill.generator.QRBill.GraphicsFormat;

public class PerformanceTest {

    private static final int BATCH_SIZE = 24;
    private static final int BATCH_COUNT = 1000;

    private ExecutorService executor;
    AtomicInteger counter = new AtomicInteger();


    public static void main(String[] args) {
        PerformanceTest test = new PerformanceTest();
        try {
            test.execute();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    PerformanceTest() {
        executor = Executors.newFixedThreadPool(8);
    }

    void execute() throws InterruptedException {
        // warm up
        addBatchOfWork();
        addBatchOfWork();
        while (counter.intValue() != 2 * BATCH_SIZE) {
            Thread.sleep(100);
        }

        counter.set(0);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < BATCH_COUNT; i++) {
            addBatchOfWork();
        }
        executor.shutdown();
        executor.awaitTermination(100, TimeUnit.SECONDS);
        long endTime = System.currentTimeMillis();

        double billPerSecond = (endTime - startTime) * 1000.0 / counter.intValue();
        System.out.printf("Performance: %f bill/second", billPerSecond);
    }

    void addBatchOfWork() {
        final BillFormat[] sizes = { BillFormat.A6_LANDSCAPE_SHEET, BillFormat.A5_LANDSCAPE_SHEET, BillFormat.A4_PORTRAIT_SHEET };
        final GraphicsFormat[] outputFormats = { GraphicsFormat.SVG, GraphicsFormat.PDF };

        for (BillFormat size : sizes) {
            for (GraphicsFormat format : outputFormats) {
                for (int i = 0; i < 4; i++) {
                    QRBillTask task = new QRBillTask(i, size, format);
                    executor.submit(task);
                }
            }
        }
    }


    class QRBillTask implements Runnable {

        private int billID;
        private BillFormat size;
        private GraphicsFormat outputFormat;

        QRBillTask(int billID, BillFormat size, GraphicsFormat outputFormat) {
            this.billID = billID;
            this.size = size;
            this.outputFormat = outputFormat;
        }

		@Override
		public void run() {
            Bill bill = SampleData.generateBill(billID);
            QRBill.generate(bill, size, outputFormat);
            counter.incrementAndGet();
		}
    }

}

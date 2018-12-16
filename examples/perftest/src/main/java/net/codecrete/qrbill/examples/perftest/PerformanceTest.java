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
import net.codecrete.qrbill.generator.GraphicsFormat;
import net.codecrete.qrbill.generator.OutputSize;
import net.codecrete.qrbill.generator.QRBill;

public class PerformanceTest {

    private static final int BATCH_SIZE = 8;
    private static final int BATCH_COUNT = 1000;

    AtomicInteger counter = new AtomicInteger();

    public static void main(String[] args) {
        PerformanceTest test = new PerformanceTest();
        try {
            test.execute(GraphicsFormat.SVG);
            test.execute(GraphicsFormat.PDF);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    void execute(GraphicsFormat graphicsFormat) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(8);

        // warm up
        counter.set(0);
        addBatchOfWork(executor, graphicsFormat);
        while (counter.intValue() != BATCH_SIZE) {
            Thread.sleep(100);
        }

        counter.set(0);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < BATCH_COUNT; i++) {
            addBatchOfWork(executor, graphicsFormat);
        }
        executor.shutdown();
        executor.awaitTermination(100, TimeUnit.SECONDS);
        long endTime = System.currentTimeMillis();

        double billPerSecond = (endTime - startTime) * 1000.0 / counter.intValue();
        System.out.printf("Performance for %s: %.0f bill/second", graphicsFormat.name(), billPerSecond);
    }

    void addBatchOfWork(ExecutorService executor, GraphicsFormat graphicsFormat) {
        final OutputSize[] sizes = { OutputSize.QR_BILL_ONLY, OutputSize.A4_PORTRAIT_SHEET };

        for (OutputSize size : sizes) {
            for (int i = 0; i < 4; i++) {
                QRBillTask task = new QRBillTask(i, size, graphicsFormat);
                executor.submit(task);
            }
        }
    }


    class QRBillTask implements Runnable {

        private int billID;
        private OutputSize outputSize;
        private GraphicsFormat graphicsFormat;

        QRBillTask(int billID, OutputSize outputSize, GraphicsFormat graphicsFormat) {
            this.billID = billID;
            this.outputSize = outputSize;
            this.graphicsFormat = graphicsFormat;
        }

		@Override
		public void run() {
            Bill bill = SampleData.generateBill(billID);
            bill.getFormat().setOutputSize(outputSize);
            bill.getFormat().setGraphicsFormat(graphicsFormat);

            try {
                QRBill.generate(bill);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }

            counter.incrementAndGet();
		}
    }

}

#!/bin/sh
mvn package
mvn exec:java -Dexec.mainClass="net.codecrete.qrbill.examples.QRBillExample"
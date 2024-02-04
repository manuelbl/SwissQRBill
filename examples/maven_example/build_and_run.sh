#!/bin/sh
mvn package
mvn exec:java -Dexec.mainClass="net.codecrete.qrbill.examples.QRBillExample" -Dexec.args="150.00 210000000003139471430009017 'Spring 2024 Dues' 'Peter' 'addr' 'Zürich'"

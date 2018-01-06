#!/bin/sh
systemctl stop qrbill.service
sleep 5
mv qrbill-service-*.jar ~qrbill/qrbill.jar
systemctl start qrbill.service
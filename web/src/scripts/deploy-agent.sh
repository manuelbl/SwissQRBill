#!/bin/sh
cd ~qrbill
systemctl stop qrbill.service
sleep 5
mv qrbill.jar qrbill.jar.old
mv qrbill.jar.new qrbill.jar
systemctl start qrbill.service
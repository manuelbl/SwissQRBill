#!/bin/sh
cat > /etc/systemd/system/qrbill.service <<END_OF_FILE
[Unit]
Description=Swiss QR Bill Service
After=network.target

[Service]
User=qrbill
ExecStart=/usr/bin/java -jar /usr/share/qrbill/qrbill.jar
SuccessExitStatus=143

[Install]
WantedBy=multi-user.target
END_OF_FILE

groupadd --system qrbill
useradd --system --home /usr/share/qrbill --gid qrbill --shell /bin/false qrbill
mkdir /usr/share/qrbill
chown qrbill /usr/share/qrbill
chgrp qrbill /usr/share/qrbill

cat >/usr/share/qrbill/deploy-qrbill-service.sh <<END_OF_FILE
#!/bin/sh
systemctl stop qrbill.service
sleep 5
mv qrbill-service-*.jar ~qrbill/qrbill.jar
systemctl start qrbill.service
END_OF_FILE
chmod +x /usr/share/qrbill/deploy-qrbill-service.sh

systemctl enable qrbill.service
systemctl start qrbill
systemctl status qrbill

#!/bin/bash
source /etc/profile

1 1 * * * /usr/bin/find  /data2/log/rapid_queue/*.log.* -mtime +10 -delete


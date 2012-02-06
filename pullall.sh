#!/bin/sh
find .. -d 1 -name "masq*" | xargs -t -I {} /bin/sh -c "cd {} ; git pull ; cd .."

#!/bin/sh
find .. -d 1 -name "masq*" | xargs -t -I {} /bin/sh -c "cd {} ; git push origin master --tags ; cd .."

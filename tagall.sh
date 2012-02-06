#!/bin/sh
find .. -d 1 -name "masq*" | xargs -t -I {} /bin/sh -c "cd {} ; git tag v-1.3.0 ; cd .."

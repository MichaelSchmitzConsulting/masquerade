#!/bin/sh
find .. -d 1 -name "masq*" | xargs -t -I {} /bin/sh -c "cd {} ; git tag v-1.1.0 ; cd .."

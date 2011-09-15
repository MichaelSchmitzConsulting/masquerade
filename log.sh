#!/bin/sh
find .. -d 1 -name "masquerade*" | xargs -t -L 1 -I {} git --git-dir={}/.git --no-pager log --oneline $1..

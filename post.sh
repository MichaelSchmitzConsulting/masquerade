#!/bin/sh
echo "Response Setup"
curl --data-binary '<response/>' http://localhost:8888/masquerade/api/provideResponse/345
echo
echo "Request"
curl --data-binary '<bla id="345"/>' http://localhost:8888/masquerade/request/exam/ple

#!/bin/bash

HOST="${1:-www.mig33.com}"

raw_eid=$(
	curl -Lv -X POST -H "Content-Type:application/json" http://$HOST/xml/ -d '{"I": 10, "T": 200, "F": {"1": 1, "2": 8, "3": 310, "4": 1, "5": "infn8loop", "7": "migpy/0.3", "8": "migpy/0.3", "9": 1, "11": 14, "15": "en-US", "19": 1, "23": 0}}' -H 'Content-Length:165' -H 'X-Mig33-JSON-Version:2'|cut -d ":" -f 5 | cut -d '"' -f 2
)
value=$(
	perl -MURI::Escape -e 'print uri_escape($ARGV[0]);' "${raw_eid}"
)
curl http://$HOST/sites/midlet/account/home -H "Cookie: eid=$value" -Lv -k | less
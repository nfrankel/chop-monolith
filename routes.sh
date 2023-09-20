#!/bin/sh
curl -i http://localhost:9180/apisix/admin/routes/1 -H 'X-API-KEY: edd1c9f034335f136f87ad84b625c8f1' -X PUT -d '
{
  "uri": "/*",
  "upstream": {
    "nodes": {
      "chopshop-frontend:3000": 1
    }
  }
}'

curl -i http://localhost:9180/apisix/admin/routes/2 -H 'X-API-KEY: edd1c9f034335f136f87ad84b625c8f1' -X PUT -d '
{
  "uri": "/api*",
  "plugins" : {
    "proxy-rewrite": {
      "regex_uri": [ "/api(.*)", "$1" ]
    }
  },
  "upstream": {
    "nodes": {
      "chopshop-backend:8080": 1
    }
  }
}'

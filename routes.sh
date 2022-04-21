#!/bin/sh
docker run --network chop-monolith_default --rm curlimages/curl:7.81.0 -v -i http://apisix:9180/apisix/admin/routes/1 -H 'X-API-KEY: edd1c9f034335f136f87ad84b625c8f1' -X PUT -d '
{
  "uri": "/*",
  "plugins" : {
    "response-rewrite": {
      "headers": {
        "Cache-Control": "no-cache, no-store, max-age=0, must-revalidate",
        "Expires": 0,
        "Pragma": "no-cache"
      }
    }
  },
  "upstream": {
    "nodes": {
      "chopshop:8080": 1
    }
  }
}'

source .env

docker run --network chop-monolith_default --rm curlimages/curl:7.81.0 -v -i http://apisix:9180/apisix/admin/routes/2 -H 'X-API-KEY: edd1c9f034335f136f87ad84b625c8f1' -X PUT -d '
{
  "methods": ["POST"],
  "uris": ["/price"],
  "plugins": {
    "azure-functions": {
      "function_uri": "https://chopshoppricing.azurewebsites.net/api/JustPrice",
      "authorization": {
        "apikey": "'"$AZURE_FUNCTION_KEY1"'"
      },
      "ssl_verify": false
    }
  }
}'

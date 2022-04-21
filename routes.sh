#!/bin/sh
docker run --network chop-monolith_default --rm curlimages/curl:7.81.0 -v -i http://apisix:9080/apisix/admin/routes/1 -H 'X-API-KEY: edd1c9f034335f136f87ad84b625c8f1' -X PUT -d '
{
  "uri": "/*",
  "upstream": {
    "type": "roundrobin",
    "nodes": {
      "chopshop:8080": 1
    }
  }
}'

source .env

docker run --network chop-monolith_default --rm curlimages/curl:7.81.0 -v -i http://apisix:9080/apisix/admin/routes/2 -H 'X-API-KEY: edd1c9f034335f136f87ad84b625c8f1' -X PUT -d '
{
  "methods": ["POST"],
  "uris": ["/price"],
  "plugins": {
    "azure-functions": {
      "function_uri": "https://chopshoppricing.azurewebsites.net/api/HttpTrigger",
      "authorization": {
        "apikey": "'"$AZURE_FUNCTION_KEY"'"
      },
      "ssl_verify": false
    }
  }
}'

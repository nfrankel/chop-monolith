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

curl -i http://localhost:9180/apisix/admin/routes/3 -H 'X-API-KEY: edd1c9f034335f136f87ad84b625c8f1' -X PUT -d '
{
  "uri": "/api/checkout",
  "plugins" : {
    "pipeline-request": {
      "nodes": [
         {
           "url": "http://chopshop-backend:8080/checkout"
         },
         {
           "url": "http://apisix:9080/api/price"
         }
      ]
    }
  }
}'

source .env

curl -i http://localhost:9180/apisix/admin/routes/4 -H 'X-API-KEY: edd1c9f034335f136f87ad84b625c8f1' -X PUT -d '
{
  "uri": "/api/price",
  "plugins": {
    "azure-functions": {
      "function_uri": "https://chopshoppricing.azurewebsites.net/api/CartLinesAndPrice",
      "authorization": {
        "apikey": "'"$AZURE_FUNCTION_KEY2"'"
      },
      "ssl_verify": false
    }
  }
}'

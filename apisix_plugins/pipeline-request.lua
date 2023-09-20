--
-- Licensed to the Apache Software Foundation (ASF) under one or more
-- contributor license agreements.  See the NOTICE file distributed with
-- this work for additional information regarding copyright ownership.
-- The ASF licenses this file to You under the Apache License, Version 2.0
-- (the "License"); you may not use this file except in compliance with
-- the License.  You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--
local core   = require("apisix.core")
local http   = require("resty.http")
local string = string
local pairs  = pairs


local plugin_schema = {
    type = "object",
    properties = {
        nodes = {
            type = "array",
            minItems = 1,
            items = {
                type = "object",
                properties = {
                    url = {
                        type = "string",
                        minLength = 1
                    },
                    ssl_verify = {
                        type = "boolean",
                        default = true,
                    },
                    timeout = {
                        type = "integer",
                        minimum = 1,
                        maximum = 60000,
                        default = 3000,
                        description = "timeout in milliseconds",
                    },
                    keepalive = {type = "boolean", default = true},
                    keepalive_timeout = {type = "integer", minimum = 1000, default = 60000},
                    keepalive_pool = {type = "integer", minimum = 1, default = 5},
                },
                required = {"url"},
            },
        },
    },
}

local plugin_name = "pipeline-request"

local _M = {
    version  = 0.1,
    priority = 1000,
    name     = plugin_name,
    schema   = plugin_schema,
}


function _M.check_schema(conf)
    local ok, err = core.schema.check(plugin_schema, conf)
    if not ok then
        return false, err
    end

    return true
end


function _M.access(conf, ctx)
    local last_resp, err
    for _, node in ipairs(conf.nodes) do
        -- assembly request parameters
        local params = {
            method = "POST",
            ssl_verify = node.ssl_verify,
            keepalive = node.keepalive,
        }

        -- attaching connection pool configuration
        if node.keepalive then
            params.keepalive_timeout = node.keepalive_timeout
            params.keepalive_pool = node.keepalive_pool
        end

        -- initialize new http connection
        local httpc = http.new()
        httpc:set_timeout(node.timeout)

        if last_resp ~= nil then
            -- setup body from last success response
            params.method = "POST"
            params.body = last_resp.body
        else
            -- setup header, query and body for first request (upstream)
            params.method = core.request.get_method()
            params.headers = core.request.headers()
            params.query = core.request.get_uri_args()
            local body, err = core.request.get_body()
            if err then
                return 503
            end
            if body then
                params.body = body
            end
        end

        -- send request to each node and temporary store response
        last_resp, err = httpc:request_uri(node.url, params)
        if not last_resp then
            return 500, "request failed: " .. err
        end
    end

    -- send all headers from last node's response to client
    for key, value in pairs(last_resp.headers) do
        -- Avoid setting Transfer-Encoding and Connection,
        -- they can be broken for response headers.
        local lower_key = string.lower(key)
        if lower_key == "transfer-encoding"
                or lower_key == "connection" then
            goto continue
        end

        -- set response header
        core.response.set_header(key, value)

        ::continue::
    end

    return 200, last_resp.body
end


return _M

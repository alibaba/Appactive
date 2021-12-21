local cjson = require("cjson.safe")
local kv = require("kv.kv_util")

local function dumpSharedRule()
    local ruleTable = {}
    for _, k in pairs(kv.kvShared:get_keys()) do
        ruleTable[k] = kv.kvShared:get(k)
    end
    ngx.say(cjson.encode(ruleTable))
end

local function querySharedRule(ruleKey)
    local ruleTable = {}
    if ruleKey then
        ruleTable[ruleKey] = kv.kvShared:get(ruleKey)
    end
    ngx.say(cjson.encode(ruleTable))
end

--main
local req_method = ngx.var.request_method
if "GET" ~= req_method then
    kv.print("method not support", 400)
end

local ruleKey = ngx.var.arg_key
if ruleKey then
    querySharedRule(ruleKey)
else
    dumpSharedRule()
end

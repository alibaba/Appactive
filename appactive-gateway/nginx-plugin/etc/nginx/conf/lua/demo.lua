local kv = require("kv.kv_util")

--main
local req_method = ngx.var.request_method
if "GET" ~= req_method then
    kv.print("method not support", 400)
end

local param = ngx.var.arg_param or "nil"
local unitKey = ngx.var.http_unit_key or "nil"
local unit = ngx.var.http_unit or "nil"
local unitType = ngx.var.http_unit_type or "nil"
--ngx.say(ngx.req.raw_header())
kv.print("param is " .. param ..
        "; unit_type is ".. unitType ..
        "; unit_key is ".. unitKey ..
        "; unit is ".. unit
        ,200)

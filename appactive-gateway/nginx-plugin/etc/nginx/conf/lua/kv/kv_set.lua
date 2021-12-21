local cjson = require("cjson.safe")
local kv = require("kv.kv_util")

local function getRuleBody()
    ngx.req.read_body()
    local data = ngx.req.get_body_data()
    if data then
        return data
    end

    local fileName = ngx.req.get_body_file()
    if fileName then
        local file = io.open(fileName, "rb")
        if file then
            data = file:read("*all")
            file:close()
        end
    end
    return data
end

--main
local req_method = ngx.var.request_method
if "PUT" == req_method or "POST" == req_method then
    local data = getRuleBody()
    if data then
        local dataDecoded = cjson.decode(data)
        if not dataDecoded then
            kv.print("set value invalid", 400)
        end
        if dataDecoded.key and dataDecoded.value then
            local f = io.open(kv.storePath .. dataDecoded.key, "w+")
            if f then
                local ret = f:write(cjson.encode(dataDecoded.value))
                f:close()
                if ret then
                    local rule_ver = kv.kvShared:get(dataDecoded.key..kv.versionKey)
                    if rule_ver == nil then
                        rule_ver = 1
                    else
                        rule_ver = rule_ver + 1
                    end
                    kv.kvShared:set(dataDecoded.key..kv.versionKey, rule_ver)
                    kv.kvShared:set(dataDecoded.key, cjson.encode(dataDecoded.value))
                    kv.print("success", 200)
                else
                    kv.print("write disk failed", 500)
                end
            else
                kv.print("open file failed", 500)
            end
        else
            kv.print("null key or value not supported", 400)
        end
    end
end

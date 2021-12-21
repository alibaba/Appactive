local cjson = require("cjson.safe")
local storePath = ngx.config.prefix().."/store/"
local kvShared = ngx.shared.kv_shared_dict
if not kvShared then
    ngx.log(ngx.ERR, "[kv] kv_shared_dict not found")
end

local _M = {
    storePath = storePath,
    kvShared = kvShared,
    versionKey = "_rule_version"
}

function _M.print(msg, code)
    local ret = {}
    ret["code"] = code
    ret["msg"] = msg
    ngx.status = code
    ngx.say(cjson.encode(ret))
	ngx.log(ngx.WARN, "code: ", code, ", msg: ", msg)
    return ngx.exit(ngx.status)
end

function _M.get(key)
    local value = _M[key]
    local now = ngx.now()
    if not value or not value.last_update_time or now - value.last_update_time > 1 then
        local new_data = kvShared:get(key)
        _M[key] = { data = new_data, last_update_time = now }
        return new_data
    else
        return value.data
    end
end

function _M.raw_get(key)
    return kvShared:get(key)
end

return _M

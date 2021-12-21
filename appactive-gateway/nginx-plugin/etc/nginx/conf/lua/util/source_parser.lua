local _M = {}
_M._VERSION = '1.0'

local function split(s, sep)
    local fields = {}
    local sep = sep or ","
    local pattern = string.format("([^%s]+)", sep)
    string.gsub(s, pattern, function(c) fields[#fields + 1] = c end)
    return fields
end

local function cookieFix(source, token)
    if source == "cookie" and string.find(token,"-") ~= nil then
        -- cookie 且包含 中划线
        return "cookie;"..token;
    end
    return source.."_"..string.gsub(token, "-", "_");
end

local function validKey(s)
    -- lua 读取 header cookie 参数 的方式 分别为 http_ cookie_ arg_
    if s ~= "cookie" and  s ~= "header" and s ~= "arg" then
        ngx.log(ngx.ERR,"[unit] json rule key err "..s.." should be in (cookie,header,arg)")
        return nil
    end

    if s == "header" then
        s = "http"
    end

    return s
end

function _M.doParseSource(val)
    local idSource = nil
    if val["source"] == nil or val["tokenKey"] == nil then
        ngx.log(ngx.ERR,"[unit] no source or tokenKey")
        return idSource
    end

    if string.find(val["source"],",") ~= nil then
        local sourceArray = {}
        local arr = split(val["source"],",")
        for i = 1, #arr do
            local sourceKey =  arr[i]
            sourceKey = validKey(sourceKey)
            if sourceKey == nil then
                return
            end
            sourceKey = cookieFix(sourceKey,val["tokenKey"])
            sourceArray[i] = sourceKey
        end
        idSource = sourceArray
    else
        local sourceKey = validKey(val["source"])
        if sourceKey == nil then
            return
        end
        idSource = cookieFix(sourceKey,val["tokenKey"])
    end
    return idSource
end

return _M

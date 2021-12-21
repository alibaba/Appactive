local util = require("util.util")

local _M = {}
_M._VERSION = '1.0'

local function getCookieValue(s,key)
    local i,j = string.find(s,key,0,true)
    local value
    if i == nil then
        return nil
    end

    local q,_ = string.find(s,";",j,true);
    if q == nil then
        value = string.sub(s,j+1)
    else
        value = string.sub(s,j+1,q-1)
    end
    -- 去掉首尾空白
    return (value:gsub("^%s+", ""):gsub("%s+$", ""))
end

local function getValueFromNginx(key)
    if string.find(key,"cookie;") ~= nil then
        key = string.sub(key,8).."="
        local allCookie = (ngx.var.http_cookie)
        if allCookie == nil then
            return nil
        end
        local value = getCookieValue(allCookie,key)
        return value
    end
    return ngx.var[key]
end

local function doFilterUnit(tokenName, id, ruleParsed)
    local unit
    local idUnitMapping = ruleParsed["idUnitMapping"]
    for _, mapping in pairs(idUnitMapping["items"]) do
        for _, act in pairs(mapping["action"]) do
            if unit == nil then
                if act["condKey"] == "@"..tokenName or act["condKey"] == "$"..tokenName then
                    local hit = act["filter"](id, act["val"])
                    if hit then
                        return mapping["name"]
                    end
                end
            end
        end
    end
    return nil
end

local function getUnit(ruleParsed, key)
    local transformedId = key
    for _, transformer in pairs(ruleParsed["idTransformer"]) do
        for _, act in pairs(transformer["action"]) do
            if act["filter"] ~= nil then
                transformedId = act["filter"](transformedId, act["config"])
            end
        end
        if transformedId ~= nil then
            local unit = doFilterUnit(transformer["tokenName"], transformedId, ruleParsed)
            if unit ~= nil then
                return unit
            end
        end
    end
    return nil
end

local function default(parsed_rule)
    if parsed_rule["defaultUnit"] ~= nil then
        local rd = math.random(1,100)
        if parsed_rule["defaultUnit"][rd] ~= nil then
            return parsed_rule["defaultUnit"][rd]
        end
    end
    return -1
end

-- 正常 返回 flag
-- -1 则走 本地
-- -2 则报错
function _M.getUnitForRequest(ruleParsed, isUnitEnabled)

    if isUnitEnabled then
        if not ruleParsed or ruleParsed["idUnitMapping"] == nil then
            return -1
        else
            local unit, key
            local idSource = ruleParsed["idSource"]
            if type(idSource) == "table" then
                for i = 1, #idSource do
                    key =  getValueFromNginx(idSource[i])
                    if key ~= nil then
                        break
                    end
                end
            else
                key = getValueFromNginx(idSource)
            end
            if key ~= nil then
                ngx.var.unit_key = key
                unit = getUnit(ruleParsed,key)
                if unit ~= nil then
                    return unit
                end
            end
            return -1
        end
    else
        return -1
    end
end

return _M

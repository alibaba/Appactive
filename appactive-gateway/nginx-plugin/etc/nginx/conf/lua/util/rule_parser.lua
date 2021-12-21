local _M = {}
_M._VERSION = '1.0'


local function doFilterRange(id, condValue)
    local idNum = tonumber(id)
    if idNum == nil then
        return false
    end
    for _, range in pairs(condValue) do
        local rangeMin = tonumber(range.min)
        local rangeMax = tonumber(range.max)
        if rangeMin == nil or rangeMax == nil then
            ngx.log(ngx.ERR, "[unit] filter route range fail")
            return false
        end
        if idNum >= rangeMin and idNum <= rangeMax then
            return true
        end
    end
    return false
end

local function doFilterExact(id, condValue)
    for _, item in pairs(condValue) do
        if item == id then
            return true
        end
    end
    return false
end

local function doParseUnitMapping(items)
    local res = {}

    for idx, unit in pairs(items) do
        res[idx] = {}
        res[idx]["name"] = unit.name
        res[idx]["action"] = {}

        local idRange ={}
        local actIndex = 0
        for _, condition in pairs(unit.conditions) do
            for condKey, condValue in pairs(condition) do
                actIndex = actIndex + 1
                local condMark = string.sub(condKey, 1, 1)
                res[idx]["action"][actIndex] = {}
                if condMark == "$" then
                    res[idx]["action"][actIndex] = {
                        condKey = condKey,
                        val = condValue,
                        filter = doFilterExact
                    }
                elseif condMark == "@" then
                    local cnt = 0
                    for _, idCondValue in pairs(condValue) do
                        cnt = cnt + 1
                        local idMin, idMax = string.match(idCondValue, "(%d+)~(%d+)")
                        idRange[cnt] = {}
                        idRange[cnt]["min"] = idMin
                        idRange[cnt]["max"] = idMax
                    end
                    res[idx]["action"][actIndex] = {
                        condKey = condKey,
                        val = idRange,
                        filter = doFilterRange
                    }
                end
            end
        end
    end

    return res
end

function _M.doParseMapping(val)
    local idUnitMapping = {}
    idUnitMapping["itemType"] = val.itemType
    idUnitMapping["items"] = doParseUnitMapping(val.items)
    return idUnitMapping
end

return _M

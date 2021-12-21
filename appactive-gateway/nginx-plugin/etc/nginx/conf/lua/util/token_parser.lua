local _M = {}
_M._VERSION = '1.0'

local function doFilterMod(id, config)
    if id == nil then
        return nil
    end

    local idNum = tonumber(id)
    if idNum == nil then
        return id
    end

    local modNum = tonumber(config.mod)
    if modNum == nil or modNum == 0 then
        return id
    end

    return tostring(idNum % modNum)
end

-- hold all filters
local modeTable = {
    mod = doFilterMod,
    other = nil,
}

local function getAction(rule)
    local action = {}
    action[1] = {
        name = rule.id,
        config = rule,
        filter = rule.mod and modeTable.mod or modeTable.other
    }
    return action
end

function _M.doParseTransformer(val)
    local idTransformer = {}
    local count = 1

    for _, rule in pairs(val) do
        idTransformer[count] = {
            tokenName = rule.id,
            action = getAction(rule)
        }
        count = count + 1

    end

    return idTransformer
end

return _M

local cjson = require("cjson.safe")

local _M = {}

function _M.printToJson(obj)
    print(cjson.encode(obj))
end

function _M.stringifyTable(o)
    if type(o) == 'table' then
        local s = '{ '
        for k,v in pairs(o) do
            if type(k) ~= 'number' then k = '"'..k..'"' end
            s = s .. '['..k..'] = ' .. _M.stringifyTable(v) .. ','
        end
        return s .. '} '
    else
        return tostring(o)
    end
end

function _M.listDir(directory)
    local i, t, popen = 0, {}, io.popen
    local pFile = popen('ls "'..directory..'"')
    for filename in pFile:lines() do
        i = i + 1
        t[i] = filename
    end
    pFile:close()
    return t
end

return _M

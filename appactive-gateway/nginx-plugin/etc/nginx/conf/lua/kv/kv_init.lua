local ox = require("util.util")
local kv = require("kv.kv_util")

local function loadStoreFromPath(path)
    local storeDir = ox.listDir(path)
    if storeDir then
        for _, fn in ipairs(storeDir) do
            local f = io.open(path..fn, 'r')
            if f then
                local ruleKey = fn
                local ruleValue = f:read("*all")
                f:close()
                kv.kvShared:set(ruleKey..kv.versionKey, 0)
                kv.kvShared:set(ruleKey, ruleValue)
                ngx.log(ngx.WARN, "[kv] set: ".. ruleKey .."|".. ruleValue)
            else
                ngx.log(ngx.ERR, "[kv] open: "..path..fn.." failed")
            end
        end
    else
        ngx.log(ngx.ERR, "[kv] no rule dir: ", path)
    end
end

--main
local path = kv.storePath
local ok, err = pcall(loadStoreFromPath, path)
if not ok then
    ngx.log(ngx.ERR, "[kv] load from store path failed: ", err)
end

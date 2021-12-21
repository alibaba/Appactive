local kv = require("kv.kv_util")
local ruleChecker = require("util.rule_checker")
local unitFilter = require("util.unit_filter")

local function doMain()
    local ruleKey = ngx.arg[1]
    local unitEnabled = ngx.arg[2]

    local ruleRaw = kv.get(ruleKey)
    local ruleRawVersion = kv.get(ruleKey ..kv.versionKey)
    local ruleParsed = ruleChecker.doCheckRule(ruleRawVersion, ruleKey, ruleRaw)

    local unit = unitFilter.getUnitForRequest(ruleParsed, unitEnabled == '1')
    return unit

end

-- main
local ok, res = pcall(doMain)
if not ok then
    ngx.log(ngx.ERR, "[unit] calc error "..res);
    return -1
else
    ngx.log(ngx.INFO, "[unit] calc "..res);
    return res
end

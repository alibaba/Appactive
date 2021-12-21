package.path = package.path .. ";../../?.lua"
local util = require("util.util")
local cjson = require("cjson.safe")

local ruleChecker = require("util.rule_checker")
local unitFilter = require("util.unit_filter")

function mock_ngx(mock)
    local _ngx = mock
    setmetatable(_ngx, {__index = _G.ngx})
    _G.ngx = _ngx
end

local var = {
    -- 注意数字和字符串
    http_routerId = "12",
    arg_routerId = "123"
}

describe('Busted unit testing framework', function()
    before_each(function()
        mock_ngx({ ERR = "error", WARN = "warn", INFO = "info", log = print, var = var })
    end)
    describe('should be awesome', function()
        it('should be easy to use', function()

            local ruleRaw =[[
                {
                    "idSource": {
                        "source": "header,cookie,arg",
                        "tokenKey": "routerId"
                    },
                    "idTransformer": [
                    {
                        "id": "userIdBetween",
                        "mod": "10000"
                    }],
                    "idUnitMapping": {
                         "items": [{
                                "conditions": [ {
                                    "@userIdBetween": ["0~1999"]
                                }],
                                "name": "center"
                            }, {
                                "conditions": [{
                                    "@userIdBetween": ["2000~9999"]
                                }],
                                "name": "unit"
                            }],
                            "itemType": "UnitRuleItem"
                    }
                }
             ]]
            local ruleParsed = ruleChecker.doCheckRule(1, "1345345s4-2adsadsd-35645sdfsd-4dsfsdf44", ruleRaw)
            print("ruleParsed",util.stringifyTable(ruleParsed))

            print("\n test \n")
            var.http_routerId = "3450"
            local unit = unitFilter.getUnitForRequest(ruleParsed, true)
            print(util.stringifyTable(var), "hit", unit)
            assert.truthy(unit == "unit")

            var.http_routerId = "5678"
            local unit = unitFilter.getUnitForRequest(ruleParsed, true)
            print(util.stringifyTable(var), "hit", unit)
            assert.truthy(unit == "unit")

            var.http_routerId = "12"
            unit = unitFilter.getUnitForRequest(ruleParsed, true)
            print(util.stringifyTable(var), "hit", unit)
            assert.truthy(unit == "center")

            var.http_routerId = "1999"
            unit = unitFilter.getUnitForRequest(ruleParsed, true)
            print(util.stringifyTable(var), "hit", unit)
            assert.truthy(unit == "center")

            var.http_routerId = "2000"
            var.cookie_routerId = "6666"
            unit = unitFilter.getUnitForRequest(ruleParsed, true)
            assert.truthy(unit == "unit")
            print(util.stringifyTable(var), "hit", unit)

            var.http_routerId = "20001"
            unit = unitFilter.getUnitForRequest(ruleParsed, true)
            print(util.stringifyTable(var), "hit", unit)
        end)

    end)
end)

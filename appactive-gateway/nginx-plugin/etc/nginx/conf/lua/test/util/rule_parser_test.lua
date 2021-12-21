package.path = package.path .. ";../../?.lua"
local util = require("util.util")
local cjson = require("cjson.safe")

function mock_ngx(mock)
    local _ngx = mock
    setmetatable(_ngx, {__index = _G.ngx})
    _G.ngx = _ngx
end

describe('Busted unit testing framework', function()
    before_each(function()
        mock_ngx({ ERR = "error", log = print })
    end)
    describe('should be awesome', function()
        it('should be easy to use', function()

            local ruleParser = require("util.rule_parser")
            local raw =[[
                        {
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
             ]]
            local json = cjson.decode(raw)
            local currentRule = ruleParser.doParseMapping(json)
            print(util.stringifyTable(currentRule))

        end)
    end)
end)

package.path = package.path .. ";../../?.lua"
local util = require("util.util")
local cjson = require("cjson.safe")

describe('Busted unit testing framework', function()
    describe('should be awesome', function()
        it('should be easy to use', function()

            local sourceParser = require("util.source_parser")

            local tokenSourceString = [[
                {
                    "tokenKey" : "routerId",
                    "source" : "header"
                }
            ]]
            local tokenSourceRaw =  cjson.decode(tokenSourceString)
            local tokenSource = sourceParser.doParseSource(tokenSourceRaw)

            assert.truthy(type(tokenSource) == "string")
            assert.truthy(tokenSource == "http_routerId")

            tokenSourceString = [[
                {
                    "tokenKey" : "routerId",
                    "source" : "header,cookie"
                }
            ]]
            tokenSourceRaw =  cjson.decode(tokenSourceString)
            tokenSource = sourceParser.doParseSource(tokenSourceRaw)

            util.printToJson(tokenSource)
            assert.truthy(type(tokenSource) == "table")

            tokenSourceString = [[
                {
                    "tokenKey" : "router-Id",
                    "source" : "header,cookie"
                }
            ]]
            tokenSourceRaw =  cjson.decode(tokenSourceString)
            tokenSource = sourceParser.doParseSource(tokenSourceRaw)

            util.printToJson(tokenSource)
            assert.truthy(type(tokenSource) == "table")

        end)
    end)
end)

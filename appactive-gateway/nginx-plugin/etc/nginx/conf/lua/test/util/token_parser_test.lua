package.path = package.path .. ";../../?.lua"
local util = require("util.util")
local cjson = require("cjson.safe")

describe('Busted unit testing framework', function()
    describe('should be awesome', function()
        it('should be easy to use', function()

            local tokenParser = require("util.token_parser")
            local raw =[[
                       [
                            {
                              "id": "userIdBetween",
                              "mod": "10000"
                            }
                       ]
             ]]
            local json = cjson.decode(raw)
            local tokenDefines = tokenParser.doParseTransformer(json)
            print(util.stringifyTable(tokenDefines))

        end)
    end)
end)

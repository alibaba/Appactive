local _M = {}
_M._VERSION = '1.0'

local cjson = require("cjson.safe")

local sourceParser = require("util.source_parser")
local tokenParser = require("util.token_parser")
local ruleParser = require("util.rule_parser")


local function doParseNewRule(ruleRawTable)
    local ruleParsed = {
        idSource = nil,
        idTransformer = nil,
        idUnitMapping = nil,
    }

    if ruleRawTable["idSource"] ~= nil then
        local idSource = sourceParser.doParseSource(ruleRawTable["idSource"])
        if idSource == nil then
            ngx.log(ngx.ERR, "[check] token source error")
            return nil
        end
        ruleParsed.idSource = idSource
    else
        return nil
    end

    if ruleRawTable["idTransformer"] ~= nil then
        local idTransformer = tokenParser.doParseTransformer(ruleRawTable["idTransformer"])
        if idTransformer == nil then
            ngx.log(ngx.ERR, "[check] token define error")
            return nil
        end
        ruleParsed.idTransformer = idTransformer
    else
        return nil
    end

    if ruleRawTable["idUnitMapping"] ~= nil then
        local idUnitMapping = ruleParser.doParseMapping(ruleRawTable["idUnitMapping"])
        if idUnitMapping == nil then
            ngx.log(ngx.ERR, "[check] current rule error")
            return nil
        end
        ruleParsed.idUnitMapping = idUnitMapping
    else
        return nil
    end

    return ruleParsed
end


local function doParseRule(ruleRaw)
    local ruleParsed = {}
    local ruleDecoded = cjson.decode(ruleRaw)
    if ruleDecoded == nil then
        ngx.log(ngx.ERR, "[check] rule decode error")
        return nil
    end

    ruleParsed = doParseNewRule(ruleDecoded)
    if ruleParsed == nil then
        ngx.log(ngx.ERR, "[check] rule parse error")
        return nil
    end
    return ruleParsed
end

function _M.doCheckRule(ruleRawVersion, ruleKey, ruleRaw)
    if ruleRaw == "{}" then
        ngx.log(ngx.WARN, "[check] clean rule cache")
        package.loaded[ruleKey] = nil
        return nil
    end

    local ruleOldParsed = package.loaded[ruleKey]
    if ruleRaw == nil then
        return ruleOldParsed
    end

    if ruleOldParsed and ruleRawVersion ~= nil and ruleOldParsed.version == ruleRawVersion then
        return ruleOldParsed
    else
        ngx.log(ngx.INFO, "[check] using new rule:".. ruleKey)
        ruleRaw = string.gsub(ruleRaw, "%s", "")
        local ruleNewParsed = doParseRule(ruleRaw)
        if ruleNewParsed then
            ngx.log(ngx.WARN, "[check] parsed new rule")
            ruleNewParsed.version = ruleRawVersion
            package.loaded[ruleKey] = ruleNewParsed
            return ruleNewParsed
        else
            return ruleOldParsed
        end
    end
end


return _M

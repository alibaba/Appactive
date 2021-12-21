local ngx_config_prefix = ngx.config.prefix()

local initByLuaFileList = {
	ngx_config_prefix .. "conf/lua/kv/kv_init.lua"
}

--main
for _, f in ipairs(initByLuaFileList) do
    local func, err = loadfile(f)
    if err then
        ngx.log(ngx.ERR, "[init] load lua file failed:", err)
    else
        func()
    end
end

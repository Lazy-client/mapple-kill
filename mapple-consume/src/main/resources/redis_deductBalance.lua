--[[redis操作hash减库存]]
local redis = require "resty.redis"
local cjson = require "cjson"

local red = redis:new()

--local keys = redis.call('hkeys', KEYS[1])
----[[遍历keys]]
--for i = 1, #keys, 1 do
--    --[[拼接操作zset的key]]
--    local str = tostring(KEYS[2]) .. tostring(keys[i])
--    --[[移除有序集合中指定的值]]
--    redis.call('zrem', str, KEYS[1])
--end
----[[根据key删除hash数据]]
--redis.call('del', KEYS[1])
----[[成功返回1]]
--return 1


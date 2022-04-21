--
local hash = KEYS[1]
local userId = KEYS[2]
--
local payment = tonumber(ARGV[1])

--
local banlance = tonumber(redis.call('hget', hash, userId ) or "0")

if banlance -payment < 0 then
    -- 达到限流大小 返回
    return 0;
else
    -- 没有达到阈值 value + 1
    redis.call("HINCRBY",hash, userId, -payment)
    -- 设置过期时间
    return 1
end

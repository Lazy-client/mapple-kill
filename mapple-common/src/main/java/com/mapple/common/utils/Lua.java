package com.mapple.common.utils;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/4/21 14:09
 */

public enum Lua {
    banlance("local hash = KEYS[1]\n" +
            "local userId = KEYS[2]\n" +
            "--\n" +
            "local payment = tonumber(ARGV[1])\n" +
            "\n" +
            "--\n" +
            "local banlance = tonumber(redis.call('hget', hash, userId) or \"0\")\n" +
            "\n" +
            "if banlance -payment < 0 then\n" +
            "    -- 达到限流大小 返回\n" +
            "    return false;\n" +
            "else\n" +
            "    -- 没有达到阈值 value + 1\n" +
            "    redis.call(\"HINCRBY\",hash, userId, -payment)\n" +
            "    -- 设置过期时间\n" +
            "    return true\n" +
            "end");

    private final String lua;

    Lua(String lua) {
        this.lua = lua;
    }

    public String getLua() {
        return lua;
    }
}

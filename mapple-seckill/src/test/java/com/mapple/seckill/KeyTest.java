package com.mapple.seckill;

import com.mapple.common.utils.redis.cons.Key;
import org.junit.Test;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/3/24 0:53
 */
public class KeyTest {
    @Test
    public void test(){
        System.out.println(Key.SKU_PREFIX.name());
    }
}

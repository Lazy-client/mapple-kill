package io.renren;

import io.renren.common.utils.RedisKeyUtils;
import io.renren.common.utils.RedisUtils;
import io.renren.modules.sys.entity.SysUserEntity;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {
    @Autowired
    private RedisUtils redisUtils;

    @Resource
    private HashOperations<String, String, String> hashOperations;

    @Test
    public void contextLoads() {
        SysUserEntity user = new SysUserEntity();
        user.setEmail("qqq@qq.com");
        redisUtils.set("user", user);

        System.out.println(ToStringBuilder.reflectionToString(redisUtils.get("user", SysUserEntity.class)));
    }
    @Resource
    private ValueOperations<String, String> valueOperations;

    @Test
    public void redisHashTest() {

        //删除一个不存在的hash key
        hashOperations.delete(RedisKeyUtils.SKU_PREFIX, "kkkkk" + "-" + "666666");

        //93eac61a1c6c488b9b72acb455928479

        System.out.println(valueOperations.get(RedisKeyUtils.STOCK_PREFIX + "08a5f3373b544924a8a1d9cfea63b666"));



    }
}

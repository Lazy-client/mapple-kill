package io.renren.modules.sys.controller;

import io.renren.common.utils.R;
import io.renren.common.utils.RedisKeyUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/4/5 21:16
 */
@RestController
@RequestMapping("/sys/account")
public class SysAccountController extends AbstractController{

    @Resource
    private ValueOperations<String, String> valueOperations;

    @GetMapping("/public")
    @ApiOperation("获取公共账户信息")
    public R info() {
        return R.ok().put("data", valueOperations.get(RedisKeyUtils.PUBLIC_ACCOUNT));
    }
}

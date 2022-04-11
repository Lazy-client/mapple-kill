package io.renren.modules.app.controller;

import com.baomidou.mybatisplus.extension.api.R;
import io.renren.common.utils.JwtUtilsInCommon;
import io.renren.modules.app.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @author hxx
 * @date 2022/3/23 23:19
 */
@RestController
@RequestMapping("/app/user")
@Api("Mk用户信息查询接口")
public class UserController {

    @Resource
    private UserService userService;

    @ApiOperation(value = "扣减账户余额")
    @GetMapping("deductBalance/{userId}/{payAmount}")
    public R deductBalance(@PathVariable String userId,
                           @PathVariable BigDecimal payAmount) {
        return userService.deductBalance(userId, payAmount);
    }

    @ApiOperation(value = "用户刷新接口")
    @GetMapping("getUserByToken")
    public io.renren.common.utils.R getUserByToken(@RequestHeader String token) {
        String userId = JwtUtilsInCommon.getUserIdByToken(token);
        return io.renren.common.utils.R.ok().put("user", userService.getById(userId));
    }
}

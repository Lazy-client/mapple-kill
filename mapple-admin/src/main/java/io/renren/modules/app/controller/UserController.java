package io.renren.modules.app.controller;

import com.baomidou.mybatisplus.extension.api.R;
import io.renren.modules.app.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

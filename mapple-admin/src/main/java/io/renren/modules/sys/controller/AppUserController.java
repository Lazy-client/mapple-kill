package io.renren.modules.sys.controller;

import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.modules.app.entity.UserEntity;
import io.renren.modules.app.service.UserService;
import io.renren.modules.clients.ConsumeFeignService;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Map;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/4/2 18:37
 */
@RestController
@RequestMapping("/sys/app/user")
public class AppUserController {
    @Resource
    private UserService userService;

    @Resource
    private ConsumeFeignService consumeFeignService;
    /**
     * 列表
     */
    @ApiOperation("查询appUser列表")
    @GetMapping("/list")
    @RequiresPermissions("app:user:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = userService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{userId}")
    @ApiOperation("查询appUser信息")
    @RequiresPermissions("app:user:info")
    public R info(@PathVariable("userId") String userId){
        UserEntity user = userService.getById(userId);
        return R.ok().put("user", user);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("app:user:save")
    public R save(@RequestBody UserEntity user){
        userService.save(user);
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @RequiresPermissions("app:user:update")
    public R update(@RequestBody UserEntity user){
        userService.updateById(user);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除appUser")
    @RequiresPermissions("app:user:delete")
    public R delete(@RequestBody String[] userIds){
        userService.removeByIds(Arrays.asList(userIds));
        return R.ok();
    }

    /*
     * 订单接口，供管理员使用
     */
    @ApiOperation(value = "管理员订单查询", notes = "status参数传入0，即为未支付订单，传入1即为已支付订单")
    @GetMapping("/listOrderForAdmin")
    // @RequiresPermissions("sys:order:list")
    public R listOrderForAdmin(@RequestParam Map<String, Object> params) {
        PageUtils page = consumeFeignService.listForAdmin(params);
        return R.ok().put("page", page);
    }
}

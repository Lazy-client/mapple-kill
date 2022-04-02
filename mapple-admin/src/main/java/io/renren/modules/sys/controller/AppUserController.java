package io.renren.modules.sys.controller;

import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.modules.app.entity.UserEntity;
import io.renren.modules.app.service.UserService;
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
    /**
     * 列表
     */
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
    @RequiresPermissions("app:user:delete")
    public R delete(@RequestBody String[] userIds){
        userService.removeByIds(Arrays.asList(userIds));
        return R.ok();
    }
}

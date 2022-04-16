package io.renren.modules.coupon.controller;


import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.modules.coupon.entity.SessionEntity;
import io.renren.modules.coupon.service.SessionService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 场次表
 *
 * @author sicheng
 * @email sicheng_zhou@qq.com
 * @date 2022-03-13 15:23:08
 */
@RestController
@RequestMapping("/coupon/coupon/session")
public class SessionController {
    @Resource
    private SessionService sessionService;

    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("coupon:session:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = sessionService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("coupon:session:info")
    public R info(@PathVariable("id") String id) {
        SessionEntity session = sessionService.getById(id);

        return R.ok().put("session", session);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("coupon:session:save")
    public R save(@Valid @RequestBody SessionEntity session) {
        String result = sessionService.saveSession(session);
        return R.ok().put("sessionId", result);
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    //@RequiresPermissions("coupon:session:update")
    public R update(@RequestBody SessionEntity session) {
        sessionService.updateById(session);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    //@RequiresPermissions("coupon:session:delete")
    public R delete(@RequestBody String[] ids) {
        List<String> list = Arrays.asList(ids);
        sessionService.delete(list);
        return R.ok();
    }

}

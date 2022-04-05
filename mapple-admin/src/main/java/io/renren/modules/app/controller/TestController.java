package io.renren.modules.app.controller;

import io.renren.modules.clients.ConsumeFeignService;
import io.seata.spring.annotation.GlobalTransactional;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/4/5 19:13
 */
@RestController
@RequestMapping("/app/test")
public class TestController {    //测试接口
    @RequestMapping("/hello")
    public String hello(){
        return "hello";
    }
    @Resource
    private ConsumeFeignService consumeFeignService;

    @GlobalTransactional
    @GetMapping("/test")
    @ApiOperation(value = "测试", notes = "测试")
    public void test3(){
        System.out.println("test3");
        List<String> keys = consumeFeignService.getTimeOrders(20 * 60 * 1000L, 1649156871590L);
        //归还库存
        System.out.println("归还库存");
        System.out.println(keys);
        throw new RuntimeException("测试异常");
    }
}

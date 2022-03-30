package com.mapple.common.config;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.alibaba.fastjson.JSON;
import com.mapple.common.utils.result.CommonResult;
import org.springframework.stereotype.Component;
import springfox.documentation.spring.web.json.Json;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * @author hxx
 * @date 2022/3/30 10:50
 */
@Component
public class SentinelHandler implements BlockExceptionHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        if (e instanceof FlowException){
            map.put("code",4444);
            map.put("msg","流控限制");
        }
        if (e instanceof DegradeException){
            map.put("code",5555);
            map.put("msg","降级");
        }
        if (e instanceof ParamFlowException){
            map.put("code",6666);
            map.put("msg","热点参数异常");
        }
        if (e instanceof SystemBlockException){
            map.put("code",7777);
            map.put("msg","系统异常");
        }
        if (e instanceof AuthorityException){
            map.put("code",8888);
            map.put("msg","授权异常");
        }
        httpServletResponse.setStatus(200);
        httpServletResponse.setHeader("Content-Type","application/json;charset=UTF-8");
        httpServletResponse.getWriter().write(JSON.toJSONString(map));
    }
}

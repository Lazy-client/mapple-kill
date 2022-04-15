package com.mapple.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/3/27 14:08
 */
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.cloud.gateway")
public class GatewayFlowLimitConfig {

    private int flowMax1;
    private int burstMax1;
    private int flowMax2;
    private int burstMax2;
    private List<RouteDefinition> routes = new ArrayList<>();
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private final List<ViewResolver> viewResolvers;
    private final ServerCodecConfigurer serverCodecConfigurer;

    public GatewayFlowLimitConfig(ObjectProvider<List<ViewResolver>> viewResolversProvider, ServerCodecConfigurer serverCodecConfigurer) {
        this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    // 初始化一个限流的过滤器
    @Bean(name = "sentinelGatewayFlowLimitFilter")
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public GlobalFilter sentinelGatewayFilter() {
        return new SentinelGatewayFilter();
    }

    public void setRoutes(List<RouteDefinition> routes) {
        this.routes = routes;
    }


    // 配置初始化的限流参数
    @PostConstruct
    public void initGatewayRules() {

        logger.info(routes.toString());


        Set<GatewayFlowRule> flowRules = routes
                .stream()
                .map(route -> {
                            if (ServiceName.MAPPLE_SECKILL.getService().equals(route.getId()))
                                return new GatewayFlowRule(route.getId())//资源名称,对应路由id
                                        .setCount(flowMax1) // 限流阈值
                                        .setBurst(burstMax1) //瞬时爆发的流量
                                        .setIntervalSec(1);// 统计时间窗口，单位是秒，默认是 1 秒

                            return new GatewayFlowRule(route.getId())
                                    .setCount(flowMax2)
                                    .setBurst(burstMax2)
                                    .setIntervalSec(1);
                        }
                ).collect(Collectors.toSet());
        GatewayRuleManager.loadRules(flowRules);
    }

    // 配置限流的异常处理器
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SentinelGatewayBlockExceptionHandler sentinelGatewayBlockExceptionHandler() {
        return new SentinelGatewayBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
    }

    // 自定义限流异常页面
    @PostConstruct
    public void initBlockHandlers() {
        BlockRequestHandler blockRequestHandler = (serverWebExchange, throwable) -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("code", 666);
            map.put("message", "流量过大,接口被限流了");
            return ServerResponse.status(HttpStatus.OK).
                    contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(map));
        };
        GatewayCallbackManager.setBlockHandler(blockRequestHandler);
    }
}

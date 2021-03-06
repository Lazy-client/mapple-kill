package com.mapple.gateway.filter;

import com.mapple.gateway.utils.CryptogramUtil;
import com.mapple.gateway.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/2/18 21:29
 */
@Component
@Slf4j
public class AuthorizeFilter implements GlobalFilter, Ordered {

    private static final String AUTHORIZE_TOKEN = "token";
    @Value("${admin.path}")
    private String adminPath;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        log.info(adminPath);
        //1. 获取请求
        ServerHttpRequest request = exchange.getRequest();
        //2. 则获取响应
        ServerHttpResponse response = exchange.getResponse();
        //3. 如果是登录请求则放行
        if (request.getURI().getPath().startsWith(adminPath) || request.getURI().getPath().startsWith("/coupon")) {
            return chain.filter(exchange);
        }
        //4. 获取请求头
        HttpHeaders headers = request.getHeaders();
        //5. 请求头中获取令牌
        String token = headers.getFirst(AUTHORIZE_TOKEN);

        //6. 判断请求头中是否有令牌
        if (!StringUtils.isEmpty(token)) {
            Claims claims = JwtUtils.getClaimByToken(CryptogramUtil.doDecrypt(token));
            if (claims == null || JwtUtils.isTokenExpired(claims.getExpiration())) {
                //7. 响应中放入返回的状态吗, 没有权限访问
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                //8. 返回
                return response.setComplete();
            }
            return chain.filter(exchange);
        }
        //7. 响应中放入返回的状态吗, 没有权限访问
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        //8. 返回
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return 3;
    }
}

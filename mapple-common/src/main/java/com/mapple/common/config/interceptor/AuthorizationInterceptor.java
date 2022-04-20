/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.mapple.common.config.interceptor;


import com.mapple.common.config.interceptor.annotation.Login;
import com.mapple.common.exception.RRException;
import com.mapple.common.utils.CryptogramUtil;
import com.mapple.common.utils.jwt.JwtUtils;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 权限(Token)验证
 *
 * @author Mark sunlightcs@gmail.com
 */
@Component
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {


    public static final String USER_KEY = "userId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Login annotation;
        if (handler instanceof HandlerMethod) {
            annotation = ((HandlerMethod) handler).getMethodAnnotation(Login.class);
        } else {
            return true;
        }

        if (annotation == null) {
            return true;
        }

        //获取用户凭证
        String token = request.getHeader(JwtUtils.header);
        if (StringUtils.isBlank(token)) {
            token = request.getParameter(JwtUtils.header);
        }

        //凭证为空
        if (StringUtils.isBlank(token)) {
            throw new RRException(JwtUtils.header + "不能为空", HttpStatus.UNAUTHORIZED.value());
        }
        //  todo 对国密解密拿到jwt
        //  todo 后续操作不变，相当于对jwt二次封装

        Claims claims = JwtUtils.getClaimByToken(CryptogramUtil.doDecrypt(token));
        if (claims == null || JwtUtils.isTokenExpired(claims.getExpiration())) {
            throw new RRException(JwtUtils.header + "失效，请重新登录", HttpStatus.UNAUTHORIZED.value());
        }
        //设置userId到request里，后续根据userId，获取用户信息
        request.setAttribute(USER_KEY, claims.getSubject());
        return true;
    }
}

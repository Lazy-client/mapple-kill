/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.mapple.common.utils.jwt;

import com.mapple.common.utils.CryptogramUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * jwt工具类
 *
 * @author Mark sunlightcs@gmail.com
 */
//@ConfigurationProperties(prefix = "renren.jwt")
//@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    private static final String secret = JwtConstants.secret;
    private static final long expire = JwtConstants.expire;
    private static final String header = JwtConstants.header;

    /**
     * 用国密生成jwt token
     *
     * @param userId
     * @return
     */
    public static String  generateToken(long userId) {
        Date nowDate = new Date();
        //过期时间
        Date expireDate = new Date(nowDate.getTime() + expire * 1000);
        String jwtToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                //用户唯一id标识
                .setSubject(userId + "")
                .setIssuedAt(nowDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
        //***用国密sm4（cbc模式）加密***
        return CryptogramUtil.doEncrypt(jwtToken);
    }

    public static Claims getClaimByToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.debug("validate is token error ", e);
            return null;
        }
    }

    public static String getUserId(String jwt) {
        final Claims claim = getClaimByToken(jwt);
        if (claim == null)
            return null;
        return claim.getSubject();
    }
    private String getUserIdByToken(String token) {
        String jwt = CryptogramUtil.doDecrypt(token);
        return JwtUtils.getUserId(jwt);
    }

    /**
     * token是否过期
     *
     * @return true：过期
     */
    public static boolean isTokenExpired(Date expiration) {
        return expiration.before(new Date());
    }
}

/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.modules.app.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.renren.common.utils.CryptogramUtil;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * jwt工具类
 *
 * @author Mark sunlightcs@gmail.com
 */
//@ConfigurationProperties(prefix = "renren.jwt")
@Component
@Data
public class JwtUtils {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private String secret = JwtConstants.secret;
    private long expire = JwtConstants.expire;
    private String header = JwtConstants.header;

    /**
     * 用国密生成jwt token
     * @param userId
     * @return
     */
    public String generateToken(String userId) {
        Date nowDate = new Date();
        //过期时间
        Date expireDate = new Date(nowDate.getTime() + expire * 1000);
        String jwtToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                //用户唯一id标识
                .setSubject(userId)
                .setIssuedAt(nowDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, "f4e2e52034348f86b67cde581c0f9eb5[www.renren.io]")
                .compact();
        //***用国密sm4（cbc模式）加密***
        return CryptogramUtil.doEncrypt(jwtToken);
    }

    public Claims getClaimByToken(String token) {
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

    /**
     * token是否过期
     * @return true：过期
     */
    public boolean isTokenExpired(Date expiration) {
        return expiration.before(new Date());
    }
}

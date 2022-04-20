package io.renren;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.renren.common.utils.CryptogramUtil;
import io.renren.modules.app.utils.JwtConstants;
import org.junit.Test;

import java.util.Date;

import static io.renren.modules.app.utils.JwtConstants.expire;

/**
 * @author zsc
 * @version 1.0
 * @date 2022/2/15 21:33
 */
public class REnren {
    @Test
    public void test(){

        Date nowDate = new Date();
        //过期时间
        Date expireDate = new Date(nowDate.getTime() + expire * 1000);
        String jwtToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                //用户唯一id标识
                .setSubject("")
                .setIssuedAt(nowDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, JwtConstants.secret)
                .compact();
        //***用国密sm4（cbc模式）加密***
        System.out.println(CryptogramUtil.doEncrypt(jwtToken));
    }
}

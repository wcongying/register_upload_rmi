package com.nicole.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;


public class JWTUtil {

    /**
     * 生成jwt的方法
     * @param date 签发时间
     * @param secrety 私钥
     * @param
     * @return
     */
    public static String getJWT(String id, String userName, Date date, String secrety) {
        //使用DES对用户ID和登录时间加密，生成Token
        JwtBuilder jwtBuilder= Jwts.builder().setIssuer(userName).setId(id)
                .setIssuedAt(date)
                .signWith(SignatureAlgorithm.HS256,secrety);

        String token = jwtBuilder.compact();
        return token;
    }

    /**
     * 解析token
     * @param token token
     * @param secrety 私钥
     * @return
     */
    public static Claims parseJWT(String token, String secrety) {
        Claims claims=Jwts.parser().setSigningKey(secrety).parseClaimsJws(token ).getBody();
        return claims;
    }
}

package com.xcc.common.utils;

import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 生成JSON Web令牌的工具类
 */
public class JwtHelper {

    //private static long tokenExpiration = 365 * 24 * 60 * 60 * 1000;
    //改成6个小时
    private static long tokenExpiration = 6 * 60 * 60 * 1000;

    //private static String tokenSignKey = "123456";
    //token秘钥修改
    private static String tokenSignKey = "shxcc101203301";

    public static String createToken(Integer userId, String username) {
        String token = Jwts.builder()
                .setSubject("AUTH-USER")
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .claim("userId", userId)
                .claim("username", username)
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }

    public static Long getUserId(String token) {
        try {
            if (StringUtils.isEmpty(token)) return null;

            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
            Claims claims = claimsJws.getBody();
            Integer userId = (Integer) claims.get("userId");
            return userId.longValue();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getUsername(String token) {
        try {
            if (StringUtils.isEmpty(token)) return "";

            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
            Claims claims = claimsJws.getBody();
            return (String) claims.get("username");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void main(String[] args) {
        String token = JwtHelper.createToken(2, "王刚");
        System.out.println(token);
        System.out.println(JwtHelper.getUserId(token));
        System.out.println(JwtHelper.getUsername(token));
    }

    //从Controller传过来HttpServletRequest，用于获取用户名
    public static String getUserNameByServlet(HttpServletRequest request){
        //获取请求头token字符串
        String token = request.getHeader("token");

        //从token字符串中获取用户名称
        String username = JwtHelper.getUsername(token);

        //返回用户名
        return username;
    }
}
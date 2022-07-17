package com.zzw.service.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.zzw.exception.conditionException;

import java.util.Calendar;
import java.util.Date;

public class tokenUtil {

    private static final String issurer = "签发者";

    //创建令牌方法
    public static String generateToken(Long userId) throws Exception {

        //加密算法
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());

        //过期时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE,60);

        return JWT.create().withKeyId(String.valueOf(userId))
                           .withIssuer(issurer)
                            .withExpiresAt(calendar.getTime())
                            .sign(algorithm);

    }
    public static String generateRefreshToken(Long userId) throws Exception {
        //加密算法
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());

        //过期时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH,7);

        return JWT.create().withKeyId(String.valueOf(userId))
                .withIssuer(issurer)
                .withExpiresAt(calendar.getTime())
                .sign(algorithm);
    }



    //解密算法
    public static Long verifyToken(String token){
        try {
            //加密算法
            Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
            JWTVerifier verifier = JWT.require(algorithm).build();
            System.out.println("-----------------------------------------");
            DecodedJWT jwtt = verifier.verify(token);
            System.out.println("-----------------------------------------");
            String userID = jwtt.getKeyId();
            System.out.println(userID+"-----------------------------------------");
            System.out.println(userID);
            Long uid = Long.valueOf(userID);
            System.out.println(uid+"-----------------------------------------");
            return  uid;
        }catch (TokenExpiredException e){
            throw new conditionException("555","token过期!");
        }catch (Exception e){
            throw new conditionException("非法用户token");
        }

    }


}

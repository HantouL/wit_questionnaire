package com.example.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtils {
    @Value("${spring.security.jwt.key}")
    private String key;

    @Value("${spring.security.jwt.expire}")
    private int expire;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    /**
     * 拉黑jwt令牌
     * @param headerToken
     * @return
     */
    public boolean invalidateJwt(String headerToken){
        String token = this.convertToken(headerToken);
        if(token==null) return false;
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try{
            DecodedJWT jwt = jwtVerifier.verify(token);
            String id = jwt.getId();
            return deleteToken(id,jwt.getExpiresAt());
        }catch (JWTVerificationException e){
            return false;
        }
    }

    /**
     * 删除令牌(将令牌加入redis中的黑名单)
     * @param uuid
     * @param time
     * @return
     */
    private boolean deleteToken(String uuid,Date time){
        if(isInvalidToken(uuid))
            return false;
        Date now = new Date();
        //这里要注意,Redis存不了负数的过期时间,所以与0作比较取max
        long expire = Math.max(time.getTime() - now.getTime(),0);
        stringRedisTemplate.opsForValue().set(Const.JWT_BLACK_LIST+uuid,"",expire, TimeUnit.MILLISECONDS);
        return true;
    }

    private boolean isInvalidToken(String uuid){
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(Const.JWT_BLACK_LIST + uuid));
    }

    /**
     * 解析jwt令牌
     * @param headerToken
     * @return
     */
    public DecodedJWT resolveJwt(String headerToken){
        String token = this.convertToken(headerToken);
        if(token==null) return null;
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try{
            DecodedJWT jwt = jwtVerifier.verify(token);
            //比较过期日期
            Date expires = jwt.getExpiresAt();
            //验证是否在黑名单
            if(this.isInvalidToken(jwt.getId()))
                return null;
            return new Date().after(expires)?null:jwt;
        }catch (JWTVerificationException e){//解析出现异常时会隐式抛出异常,需要手动捕获
            return null;
        }

    }

    /**
     * 将jwt解析为用户信息
     * @param jwt
     * @return
     */
    public UserDetails toUser(DecodedJWT jwt){
        Map<String, Claim> claims = jwt.getClaims();
        return User
                .withUsername(claims.get("name").asString())
                .password("******")
                .authorities(claims.get("authorities").asArray(String.class))
                .build();
    }

    /**
     * 解析jwt令牌中的id
     * @param jwt
     * @return
     */
    public Integer toID(DecodedJWT jwt){
        Map<String, Claim> claims = jwt.getClaims();
        return claims.get("id").asInt();
    }

    public String toRole(DecodedJWT jwt){
        Map<String, Claim> claims = jwt.getClaims();
        return claims.get("role").asString();
    }

    /**
     * 创建jwt令牌
     * @return
     */
    public String createJwt(UserDetails details,int id,String username,String role){
        Algorithm algorithm = Algorithm.HMAC256(key);
        Date expire = expireTime();
        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())//每个令牌都有唯一对应的UUID
                .withClaim("id",id)
                .withClaim("name",username)
                .withClaim("role",role)
                .withClaim("authorities",details.getAuthorities()
                                                    .stream()
                                                    .map(GrantedAuthority::getAuthority)
                                                    .toList())
                .withExpiresAt(expire)//过期时间
                .withIssuedAt(new Date())//签发时间
                .sign(algorithm);
    }

    public Date expireTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,expire*24);
        return calendar.getTime();
    }

    public String convertToken(String headerToken){
        if(headerToken==null||!headerToken.startsWith("Bearer "))
            return null;
        return headerToken.substring(7);
    }
}

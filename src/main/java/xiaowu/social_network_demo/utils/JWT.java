package xiaowu.social_network_demo.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import xiaowu.social_network_demo.enums.UserRole;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWT {

    @Value("${app.jwt.secret}")
    private static String SECRET_KEY;//配置签名密钥

    @Value("${app.jwt.expirationMs}")
    private static Long expirationMs;//配置过期时间


    public static String createJWT(String teacherId){

        Map<String,Object> claims = new HashMap<>();
        claims.put("teacherId",teacherId);
        claims.put("role", UserRole.TEACHER);


        return Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                .setClaims(claims)
                .compact();
    }

    public static  Map<String,Object> parseJWT(String token){

        return Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }

}

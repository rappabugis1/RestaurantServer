package util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.typesafe.config.ConfigFactory;


public class JWTUtil {



    public void verifyJWT (String token ){
        String secret = ConfigFactory.load().getString("play.http.secret.key");

        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("server")
                .build();
        DecodedJWT jwt = verifier.verify(token);
    }

    public String getSignedToken(Long userId, String usertype) {
        String secret = ConfigFactory.load().getString("play.http.secret.key");


        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
                .withIssuer("server")
                .withClaim("user_id", userId)
                .withClaim("user_type", usertype)
                .sign(algorithm);
    }

}

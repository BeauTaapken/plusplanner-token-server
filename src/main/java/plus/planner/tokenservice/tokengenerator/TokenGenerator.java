package plus.planner.tokenservice.tokengenerator;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import plus.planner.tokenservice.models.Role;

import java.util.Date;
import java.util.UUID;

public class TokenGenerator {
    private ObjectMapper objectMapper;
    private Algorithm algorithm;

    public TokenGenerator(Algorithm algorithm){
        this.algorithm = algorithm;
        this.objectMapper = new ObjectMapper();
    }

    public String getNewToken(String uid, String usrnm, Role[] permissions) throws JsonProcessingException {
        String permissionsstr = objectMapper.writeValueAsString(permissions);
        return JWT.create()
                .withIssuer("plus-planner-token-service")
                .withJWTId(UUID.randomUUID().toString())
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + (60 * 60 * 1000)))
                .withClaim("uid", uid)
                .withClaim("unm", usrnm)
                .withClaim("pms", permissionsstr)
                .sign(algorithm);
    }
}

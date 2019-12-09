package plus.planner.tokenservice.controllers;

import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import plus.planner.tokenservice.models.Role;
import plus.planner.tokenservice.tokengenerator.TokenGenerator;

import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;

import static plus.planner.tokenservice.tokengenerator.PemUtils.readPrivateKeyFromFile;

@RestController
@RequestMapping("/token")
public class TokenController {
    @Autowired
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    private TokenGenerator generator;

    public TokenController() throws IOException {
        java.security.Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider()
        );
        this.objectMapper = new ObjectMapper();
        this.generator = new TokenGenerator(
                Algorithm.RSA512(
                        null,
                        (RSAPrivateKey) readPrivateKeyFromFile("src/main/resources/PrivateKey.pem", "RSA")));
    }

    @RequestMapping("/gettoken/{ftoken}")
    public String getNewToken(@PathVariable("ftoken") String ftoken) throws JsonProcessingException {
        Role[] permissions = restTemplate.getForObject("http://plus-planner-role-management-service/role/read/" + "sdfghjk", Role[].class);
        return generator.getNewToken("sdfghjk", "test", permissions);
    }

}

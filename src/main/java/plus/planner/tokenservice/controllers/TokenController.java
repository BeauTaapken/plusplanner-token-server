package plus.planner.tokenservice.controllers;

import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import plus.planner.tokenservice.models.Role;
import plus.planner.tokenservice.models.UserData;
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

    @RequestMapping("/gettoken")
    public ResponseEntity getNewToken(@RequestHeader("FToken") String ftoken) throws JsonProcessingException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + ftoken);
            HttpEntity<String> entity = new HttpEntity<String>(headers);
            RestTemplate r = new RestTemplate();
            ResponseEntity<UserData> userData = r.exchange("https://api.fhict.nl/people/me", HttpMethod.GET, entity, UserData.class);
            Role[] permissions = restTemplate.getForObject("http://plus-planner-role-management-service/role/read/" + "sdfghjk", Role[].class);
            return new ResponseEntity(generator.getNewToken(userData.getBody(), permissions), HttpStatus.ACCEPTED);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
    }

}

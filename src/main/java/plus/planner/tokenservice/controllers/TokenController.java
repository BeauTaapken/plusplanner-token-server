package plus.planner.tokenservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import plus.planner.tokenservice.models.Role;
import plus.planner.tokenservice.models.UserData;
import plus.planner.tokenservice.tokengenerator.TokenGenerator;

@CrossOrigin
@RestController
@RequestMapping("/token")
public class TokenController {
    private final Logger logger = LoggerFactory.getLogger(TokenController.class);
    private final RestTemplate restTemplate;
    private final TokenGenerator generator;

    public TokenController(RestTemplate restTemplate, TokenGenerator tokenGenerator) {
        this.restTemplate = restTemplate;
        this.generator = tokenGenerator;
    }

    @GetMapping(value = "/gettoken")
    public ResponseEntity getNewToken(@RequestHeader("FToken") String ftoken) throws JsonProcessingException {
        logger.info("constructing fontys request");
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + ftoken);
        final HttpEntity<String> entity = new HttpEntity<>(headers);
        final RestTemplate r = new RestTemplate();
        logger.info("sending fontys request");
        final ResponseEntity<UserData> userData = r.exchange("https://api.fhict.nl/people/me", HttpMethod.GET, entity, UserData.class);
        logger.info("adding user");
        final HttpEntity httpEntity = new HttpEntity("{\"userid\":\"" + userData.getBody().getUid() +
                "\",\"username\":\"" + userData.getBody().getDisplayName() +
                "\",\"photo\":\"" + userData.getBody().getPhoto() + "\"}");
        restTemplate.postForObject("https://plus-planner-role-management-service/user/save", httpEntity, UserData.class);
        logger.info("getting roles for userid: {}", userData.getBody().getUid());
        final Role[] permissions = restTemplate.getForObject("https://plus-planner-role-management-service/role/read/" + userData.getBody().getUid(), Role[].class);
        logger.info("generating token");
        final String token = generator.getNewToken(userData.getBody(), permissions);
        logger.info("making response entity");
        ResponseEntity<String> responseEntity = new ResponseEntity(token, HttpStatus.ACCEPTED);
        logger.info("returning response entity");
        return responseEntity;
    }

}

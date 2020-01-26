package plus.planner.tokenservice.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import plus.planner.tokenservice.models.Role;
import plus.planner.tokenservice.models.UserData;

@Component
public class TokenProvider implements ITokenProvider {
    private final RestTemplate restTemplate;

    @Autowired
    public TokenProvider(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void postUser(UserData userData) {

        final HttpEntity httpEntity = new HttpEntity("{\"userid\":\"" + userData.getUid() +
                "\",\"username\":\"" + userData.getDisplayName() +
                "\",\"photo\":\"" + userData.getPhoto() + "\"}");
        restTemplate.postForObject("https://plus-planner-role-management-service/user/save", httpEntity, UserData.class);
    }

    @Override
    public Role[] getRoles(String userid) {
        return restTemplate.getForObject("https://plus-planner-role-management-service/role/read/" + userid, Role[].class);
    }
}

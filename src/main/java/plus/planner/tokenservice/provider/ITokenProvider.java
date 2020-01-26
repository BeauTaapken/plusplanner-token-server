package plus.planner.tokenservice.provider;

import plus.planner.tokenservice.models.Role;
import plus.planner.tokenservice.models.UserData;

public interface ITokenProvider {
    void postUser(UserData userData);
    Role[] getRoles(String userid);
}

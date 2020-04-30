package kz.beibarys.spring.voenkaProject.Services;

import kz.beibarys.spring.voenkaProject.Entities.Users;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    public Users registerUser(Users user);
    public boolean isUserAlreadyPresent(Users user);
}

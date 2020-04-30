package kz.beibarys.spring.voenkaProject.Controllers;

import kz.beibarys.spring.voenkaProject.Entities.Users;
import kz.beibarys.spring.voenkaProject.Repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;

public class BaseController {
    @Autowired
    private UsersRepository userRepository;

    public Users getUserData(){
        Users userData = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            User secUser = (User)authentication.getPrincipal();
            userData = userRepository.findByDeletedAtNullAndEmail(secUser.getUsername());
        }
        return userData;
    }
}

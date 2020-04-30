package kz.beibarys.spring.voenkaProject.Services.iml;

import kz.beibarys.spring.voenkaProject.Entities.Roles;
import kz.beibarys.spring.voenkaProject.Entities.Users;
import kz.beibarys.spring.voenkaProject.Repositories.RolesRepository;
import kz.beibarys.spring.voenkaProject.Repositories.UsersRepository;
import kz.beibarys.spring.voenkaProject.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class userServiceImpl implements UserService {
    @Autowired
    UsersRepository usersRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    RolesRepository rolesRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {


        Users user = usersRepository.findByDeletedAtNullAndEmail(s);
        if(user==null){
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        User secUser = new User(user.getEmail(), user.getPassword(), user.getRoles());
        return secUser;
    }

    public Users registerUser(Users user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Set<Roles> role =new HashSet();
        Roles roleUser=rolesRepository.getOne(2L);
        role.add(roleUser);
        user.setRoles(role);
        user.setImage_path(null);
        user.setImage_name(null);
        //user.setChecked(false);
        return usersRepository.save(user);
    }

    @Override
    public boolean isUserAlreadyPresent(Users user) {
        return false;
    }
}

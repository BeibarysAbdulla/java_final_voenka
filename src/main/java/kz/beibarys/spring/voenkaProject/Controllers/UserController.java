package kz.beibarys.spring.voenkaProject.Controllers;

import kz.beibarys.spring.voenkaProject.Entities.Message;
import kz.beibarys.spring.voenkaProject.Entities.Roles;
import kz.beibarys.spring.voenkaProject.Entities.Users;
import kz.beibarys.spring.voenkaProject.Repositories.MessageRepository;
import kz.beibarys.spring.voenkaProject.Repositories.RolesRepository;
import kz.beibarys.spring.voenkaProject.Repositories.UsersRepository;
import kz.beibarys.spring.voenkaProject.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.thymeleaf.util.StringUtils;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping(value = "userCon")//
public class UserController extends BaseController implements WebMvcConfigurer {
    @Autowired
    UserService userService;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    RolesRepository rolesRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    MessageRepository messageRepository;

    @Value("${file.upload-dir}")
    public String uploadPath;


    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("annonymous/registr").setViewName("annonymous/registr");
    }

    @GetMapping(value = "/registrPage/{soz}")
    public String registrPage(Model model,@PathVariable String soz){
        model.addAttribute("soz",soz);
        return "annonymous/registr";
    }

    @PostMapping(value = "/registr")
    public String  register(@ModelAttribute("userForm") Users userForm , BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            return "/registr";
        }

        Users user = usersRepository.findByDeletedAtNullAndEmail(userForm.getEmail());

        if (user!=null)
            return "redirect:/userCon/registrPage/This nick name already exist!";
        if(userForm.getPassword().length()<6 && userForm.getPassword()!=userForm.getPasswordConfirm()){
            return "redirect:/userCon/registrPage/Password is too short<br>Passwords do not match";
        }else {
            if (userForm.getPassword().length() < 6) {
                return "redirect:/userCon/registrPage/Password is too short";
            }

            if (!userForm.getPassword().equals(userForm.getPasswordConfirm())) {
                return "redirect:/userCon/registrPage/Passwords do not match";
            }
        }

        String fileName = StringUtils.substringBefore(userForm.getEmail(),"@");
        File folder = new File(uploadPath+"/users/"+fileName);
        if(!folder.exists()){
            boolean jai = folder.mkdir();
            if(jai){

                userService.registerUser(userForm);

                return "redirect:/main/login/You was registrate successfully! <br> Now you can enter the our website:)";
            }else
                return "redirect:/main/login/papka ashylmady";


        }
        else
            return "redirect:/main/login/ondai adam bar eken bkakbakabnk";

    }
    @PostMapping(value = "/updateProfile")
    public String updateProfile(@RequestParam(name = "name") String name,
                                @RequestParam(name = "surname") String surname,
                                @RequestParam(name="email") String email){
        Users user = getUserData();
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        usersRepository.save(user);
        return "redirect:/main/profile?message="+"profile was changed successfully!";
    }

    @PostMapping(value = "/updatePassword")
    public String updatePassword(@RequestParam(name = "password") String password,
                                 @RequestParam(name = "passwordConfirm") String passwordConfirm){
        if (!password.equals(passwordConfirm))
            return "redirect:/main/profile?message="+"password confirm not equal password!";
        else {
            Users user = getUserData();
            user.setPassword(passwordEncoder.encode(password));
            user.setPasswordConfirm(passwordConfirm);
            usersRepository.save(user);
            return "redirect:/main/profile?message="+"password was changed successfully !";

        }
    }
    @PostMapping(path = "/updateDoc")
    public String uploadDocument(@RequestParam("file") MultipartFile file) throws IOException {
        Users user = getUserData();
        String fileName = StringUtils.substringBefore(user.getEmail(),"@");
        if(file!=null){
            File uploadDir = new File(uploadPath+"/users/"+fileName+"/docs");

            if(!uploadDir.exists()){
                uploadDir.mkdir();
            }
            Path path = Paths.get(uploadDir+"/"+ file.getOriginalFilename());
            try {
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(uploadDir+"/")
                    .path(file.getOriginalFilename())
                    .toUriString();
            user.setDoc_byte(file.getBytes());
            user.setDoc_type(file.getContentType());
            user.setDoc_name(file.getOriginalFilename());
            user.setDoc_path(fileDownloadUri);
            user.setUpdatedAt(new Date());
            usersRepository.save(user);

            //List<Message> messages = messageRepository.getAllByTypeEquals("Bad");
            List<Message> messages = messageRepository.findAllByUser(user);

            if (messages!=null){
                for (Message message :messages) {
                    if (message.getType().equals("Bad") || message.getType().equals("Norm"))
                        messageRepository.delete(message);
                }
            }


        }
        return "redirect:/main/profile?message="+"Document file was changed successfully!";
    }

    @PostMapping(value = "/findUser")
    public String findUser(@RequestParam(name="id") Long id){
        Users user = usersRepository.getOne(id);

        return "redirect:/main/admin_profile";
    }

}

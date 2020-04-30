package kz.beibarys.spring.voenkaProject.Controllers;

import kz.beibarys.spring.voenkaProject.Entities.*;
import kz.beibarys.spring.voenkaProject.Entities.Users;
import kz.beibarys.spring.voenkaProject.Repositories.MessageRepository;
import kz.beibarys.spring.voenkaProject.Repositories.NewsRepository;
import kz.beibarys.spring.voenkaProject.Repositories.RolesRepository;
import kz.beibarys.spring.voenkaProject.Repositories.UsersRepository;
import kz.beibarys.spring.voenkaProject.Services.NewService;
import kz.beibarys.spring.voenkaProject.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.springframework.security.crypto.codec.Base64.encode;

@Controller
@RequestMapping(value = "admin")
public class AdminController extends BaseController{
    @Autowired
    UsersRepository usersRepository;
    @Autowired
    UserService userService;
    @Autowired
    RolesRepository rolesRepository;
    @Autowired
    NewsRepository newsRepository;
    @Autowired
    NewService newService;
    @Autowired
    MessageRepository messageRepository;

    @Value("${file.upload-dir}")
    public String uploadPath;

    @GetMapping(value = "/more/{id}")
    public String morePage(@PathVariable Long id, Model model) throws UnsupportedEncodingException {
        Users user =usersRepository.getOne(id);
        List<Message> messages =messageRepository.findAll();
        model.addAttribute("user",user);
        model.addAttribute("messages",messages);
        // model.addAttribute("imageForm",new Images());
        if(user.getImage_byte()!=null){
            byte[] encodeBase64 = encode(user.getImage_byte());
            String base64Encoded = new String(encodeBase64, "UTF-8");
            model.addAttribute("avatar",base64Encoded);
        }
        return "admin/userProf";
    }

    @PostMapping(value = "/addPost")
    public String addPost(@RequestParam("file") MultipartFile file,
                          @RequestParam(name = "title") String title,
                          @RequestParam(name="short_content") String shrt_cont,
                          @RequestParam(name="content") String content) throws IOException {
        News news = null;
        if(title.isEmpty() || content.isEmpty() || file.isEmpty()){
            String success = "barlygyn toltr shlyak!";
            return "redirect:/main/admin_profile?message="+success;

        }else{
            File uploadDir = new File(uploadPath+"/News/");
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile+"."+file.getOriginalFilename();
            Path path = Paths.get(uploadDir+"/"+ resultFileName);
            try {
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(uploadDir+"/")
                    .path(file.getOriginalFilename())
                    .toUriString();


            news=new News(title,content,fileDownloadUri,resultFileName,file.getBytes());
            news.setCreatedAt(new Date());
            news.setUser(getUserData());
            news.setShort_content(shrt_cont);
            //news.setEncode(base64Encoded);
            newService.addNews(news);
            String success = "Post kosyldy";
            return "redirect:/main/admin_profile?message="+success;

        }
        }

        @PostMapping(value = "/changePost")
        public String changePost(@RequestParam(name = "post_id") Long post_id,
                                 @RequestParam(name="title") String title,
                                 @RequestParam(name = "content") String content){
        News post = newsRepository.getOne(post_id);
        post.setTitle(title);
        post.setContent(content);
        newsRepository.save(post);
        return "redirect:/main/";
        }


        //////------------------------------Messages--------------------------------------------

        @PostMapping(value = "/sendMessage")
        public String sendMessage(@RequestParam(name = "type") String type,
                                  @RequestParam(name="content") String content,
                                  @RequestParam(name="user_id") Long id){
        Users user = usersRepository.getOne(id);
       // user.setChecked(true);
        //usersRepository.save(user);
        Message message = new Message(content,type,user);
        messageRepository.save(message);

        if(type.equals("Bad") || type.equals("Norm")){
                user.setUpdatedAt(null);
                usersRepository.save(user);
            }
////dytyyghg

        return "redirect:/admin/more/"+id;
        }

}

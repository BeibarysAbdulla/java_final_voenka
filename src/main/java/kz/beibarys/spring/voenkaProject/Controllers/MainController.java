package kz.beibarys.spring.voenkaProject.Controllers;

import kz.beibarys.spring.voenkaProject.Entities.Comment;
import kz.beibarys.spring.voenkaProject.Entities.Message;
import kz.beibarys.spring.voenkaProject.Entities.News;
import kz.beibarys.spring.voenkaProject.Entities.Users;
import kz.beibarys.spring.voenkaProject.Repositories.*;
import kz.beibarys.spring.voenkaProject.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping(value = {"main","/"})
public class MainController extends BaseController {
    @Autowired
    UsersRepository usersRepository;
    @Autowired
    UserService userService;
    @Autowired
    RolesRepository rolesRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    NewsRepository newsRepository;
    @Autowired
    MessageRepository messageRepository;

    @GetMapping(path = "/")
    public String index(Model model,@RequestParam(name = "p",defaultValue = "1") int page){
        page = (page<=0) ? 1 : page;
        Pageable pageable = PageRequest.of(page-1, 6);
        Page<News> news = newsRepository.findAllByDeletedAtNullOrderByCreatedAtDesc(pageable);
        int totalPages = news.getTotalPages();
        if(totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1,totalPages).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("user",getUserData());
        model.addAttribute("news",news);
        return "index";
    }
    //////////--------------News,Comments----------------------------------------------------------

    @GetMapping(path = "/learn_more/{id}")
    public String learnMore(@PathVariable Long id,Model model) throws UnsupportedEncodingException {
        News post = newsRepository.getOne(id);
        model.addAttribute("post",post);
        model.addAttribute("kirgen_user",getUserData());
        if(post.getImage_byte()!=null){
            byte[] encodeBase64 = Base64.encode(post.getImage_byte());
            String base64Encoded = new String(encodeBase64, "UTF-8");
            model.addAttribute("post_image",base64Encoded);
        }
        ///////////////////////////////////////////////////////////
        List<Comment> comments = commentRepository.findAll();
        if(!comments.isEmpty())
            model.addAttribute("comments",comments);

        ////////////////////////////////////

        return "learn_more_page";
    }

    @PostMapping(value = "/addComment")
    public String addComment(@RequestParam(name = "comment") String comment_content,
                             @RequestParam(name="post_id") Long post_id){
        Comment comment=new Comment(newsRepository.getOne(post_id),getUserData(),comment_content);
        comment.setCreatedAt(new Date());
        commentRepository.save(comment);

        return "redirect:/main/learn_more/"+post_id;
    }

    @PostMapping(value = "/deleteComment")
    public String deleteComment( @RequestParam(name="post_id") Long post_id,
                                 @RequestParam(name = "comment_id") Long comment_id){
        Comment comment = commentRepository.getOne(comment_id);
        comment.setDeletedAt(new Date());
        commentRepository.delete(comment);
        return "redirect:/main/learn_more/"+post_id;
    }

    @PostMapping(value = "/changeComment")
    public String changeComment(@RequestParam(name="comment_id") Long com_id,
                                @RequestParam(name="comment") String comment){
        Comment com = commentRepository.getOne(com_id);
        com.setComment(comment);
        commentRepository.save(com);
        return "redirect:/main/learn_more/"+com.getNews().getId();
    }
   /////////////////////////////////////////////////////////////////////////////////////////////////

    @GetMapping(value = "/about")
    public String about(Model model){return "about";}

    @GetMapping(value = "/login/{soz}")
    public String login(Model model, @PathVariable String soz){
        model.addAttribute("soz",soz);
        return "annonymous/login";
    }
//----------------------------------------------Admin-------------------------------------------------------
    @GetMapping(value = "/admin_profile")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String adminProfile(Model model,
                               @RequestParam(name = "p",defaultValue = "1") int page,
                              /* @RequestParam(name = "p_2",defaultValue = "1") int page_2,*/
                               @RequestParam(name = "message",defaultValue = "null") String message){
       // List<Users> users = usersRepository.findAll();

        page = (page<=0) ? 1 : page;
        Pageable pageable = PageRequest.of(page-1, 6);

        /*page_2 = (page_2<=0) ? 1 : page_2;
        Pageable pageable_2 = PageRequest.of(page_2-1, 6);
*/
        Page<Users> users_null = usersRepository.findAllByDeletedAtNullAndUpdatedAtNull(pageable);
        List<Users> users_not_null=usersRepository.findAllByUpdatedAtNotNullOrderByUpdatedAtDesc();

        int totalPages = users_null.getTotalPages();
        if(totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1,totalPages).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        List<Message> sobshs = messageRepository.getAllByTypeEquals("Good");


            for (Message hat:sobshs) {
                users_not_null.remove(hat.getUser());
            }


       // List<Users> users_otken = usersRepository.findAll().removeAll(sobsh.get());



        model.addAttribute("message", message);


        model.addAttribute("good_mas",sobshs);
        model.addAttribute("admin",getUserData());
        model.addAttribute("users_null",users_null);
        model.addAttribute("users_not_null",users_not_null);
        return "admin/profile";
    }
//---------------------------------------------User----------------------------------------------------------
    @GetMapping(value = "/profile")
    @PreAuthorize("hasRole('ROLE_USER')")
    public String userProfile(Model model,@RequestParam(name = "message",defaultValue="null") String message) throws UnsupportedEncodingException {
        Users user =getUserData();
        model.addAttribute("user",user);
        model.addAttribute("message",message);
        List<Message> messages =messageRepository.findAll();
        model.addAttribute("messages",messages);

       // model.addAttribute("imageForm",new Images());
        if(user.getImage_byte()!=null){
            byte[] encodeBase64 = Base64.encode(user.getImage_byte());
            String base64Encoded = new String(encodeBase64, "UTF-8");
            model.addAttribute("avatar",base64Encoded);
        }
        return "user/profile";}



}

package kz.beibarys.spring.voenkaProject.Controllers;

import kz.beibarys.spring.voenkaProject.Entities.Users;
import kz.beibarys.spring.voenkaProject.Repositories.UsersRepository;
import org.apache.tomcat.util.file.ConfigurationSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.thymeleaf.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping(value = "file")
public class FileController extends BaseController{
    @Autowired
    UsersRepository usersRepository;


    @Value("${file.upload-dir}")
    public String uploadPath;

    @PostMapping(value = "/imageUpload")
    public String uploadImage(@RequestParam("file") MultipartFile file,@RequestParam(name = "user_id") Long id) throws IOException {
        Optional<Users> user =usersRepository.findById(id);
        String fileName = StringUtils.substringBefore(user.get().getEmail(),"@");
        if(file!=null){
            File uploadDir = new File(uploadPath+"/users/"+fileName);
            if(!uploadDir.exists()){
                uploadDir.mkdir();
            }

            //System.out.println(fileName);
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
                    .path(resultFileName)
                    .toUriString();
            user.get().setImage_path(fileDownloadUri);
            user.get().setImage_name(resultFileName);
            user.get().setImage_byte(file.getBytes());
            usersRepository.save(user.get());
        }
        return "redirect:/main/profile";
    }

    @PostMapping(path = "/uploadDoc")
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
        }
            return "redirect:/main/profile";
    }

    @GetMapping("/download/{id}")
    public ResponseEntity downloadFromDB(@PathVariable Long id) {
        Users user = usersRepository.getOne(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(user.getDoc_type()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + user.getDoc_name() + "\"")
                .body(user.getDoc_byte());
    }

    }


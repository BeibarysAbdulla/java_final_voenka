package kz.beibarys.spring.voenkaProject.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.File;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="users")
public class Users extends BaseEntity {
    @Email
    @NotNull
    @Column(name="email")
    private String email;

    @NotNull
    @Column(name = "password")
    private String password;

    @Column(name="passwordConfirm")
    private String passwordConfirm;

    @NotNull
    @Column(name="name")
    private String name;

    @NotNull
    @Column(name="surname")
    private String surname;

    @Column(name="image_path")
    private String image_path;

    @Column(name = "image_name")
    private String image_name;

    @Lob
    @Column(name ="image_byte")
    private byte[] image_byte;

    @Column(name="document_path")
    private String doc_path;

    @Column(name = "document_name")
    private String doc_name;

    @Column(name = "document_type")
    private String doc_type;


    @Lob
    @Column(name ="document_byte")
    private byte[] doc_byte;

    /*@Column(name="checked")
    private boolean checked;*/

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Roles> roles;


}

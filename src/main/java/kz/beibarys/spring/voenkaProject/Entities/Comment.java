package kz.beibarys.spring.voenkaProject.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import javax.persistence.*;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="comments")
public class Comment extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "newPost")
    private News news;

    @ManyToOne
    @JoinColumn(name = "author")
    private Users user;

    @Column(name = "comment")
    private String comment;
}

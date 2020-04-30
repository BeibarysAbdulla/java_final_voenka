package kz.beibarys.spring.voenkaProject.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "news")
public class News extends BaseEntity{
    @Column(name="title")
    private String title;

    @Lob
    @Column(name="short_content")
    private String short_content;

    @Lob
    @Column(name="content")
    private String content;

    @Column(name="image_path")
    private String image_path;

    @Column(name = "image_name")
    private String image_name;

    @Lob
    @Column(name ="image_byte")
    private byte[] image_byte;

   /* @Column(name="encode")
    private String encode;
*/
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private Users user;


    public News(String title, String content, String image_path, String image_name, byte[] image_byte) {
        super();
        this.title=title;
        this.content=content;
        this.image_path=image_path;
        this.image_name=image_name;
        this.image_byte=image_byte;
    }
}

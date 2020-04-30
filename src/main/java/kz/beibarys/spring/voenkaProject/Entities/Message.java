package kz.beibarys.spring.voenkaProject.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "message")
public class Message extends BaseEntity {
    @Column(name = "content")
    private String content;

    @Column(name = "type_message")
    private String type;

    @ManyToOne
    @JoinColumn(name="getUser")
    private Users user;
}

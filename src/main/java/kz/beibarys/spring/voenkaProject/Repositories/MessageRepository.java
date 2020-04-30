package kz.beibarys.spring.voenkaProject.Repositories;

import kz.beibarys.spring.voenkaProject.Entities.Message;
import kz.beibarys.spring.voenkaProject.Entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message,Long> {
    Message findByUser(Users user);
    List<Message> getAllByTypeEquals(String good);

    List<Message> findAllByUser(Users user);
}

package kz.beibarys.spring.voenkaProject.Repositories;

import kz.beibarys.spring.voenkaProject.Entities.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;



public interface CommentRepository extends JpaRepository<Comment,Long> {
    Page<Comment> findAll(Pageable pageable);
}

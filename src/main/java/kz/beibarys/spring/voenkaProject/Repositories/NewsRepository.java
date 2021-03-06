package kz.beibarys.spring.voenkaProject.Repositories;

import kz.beibarys.spring.voenkaProject.Entities.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsRepository extends JpaRepository<News,Long> {
     Page<News> findAllByDeletedAtNullOrderByCreatedAtDesc(Pageable pageable);

     List<News> findAllByDeletedAtNull();
}

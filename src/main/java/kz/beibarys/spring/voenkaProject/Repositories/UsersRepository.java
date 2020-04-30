package kz.beibarys.spring.voenkaProject.Repositories;

import kz.beibarys.spring.voenkaProject.Entities.Users;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UsersRepository extends JpaRepository<Users,Long> {
   // @Query(value = "SELECT distinct FROM Users as u JOIN CheckUser WHERE u.id=CheckUser.user.id AND CheckUser.checked=FALSE")
    Users findByDeletedAtNullAndEmail(String email);

    List<Users> findAllByUpdatedAtNotNullOrderByUpdatedAtDesc();

   // @Query(value = "select distinct from Users as u join Message as me")
    List<Users> findAll();
    Page<Users> findAllByDeletedAtNullAndUpdatedAtNull(Pageable pegeable);
    List<Users> findAllByDeletedAtNull();

}

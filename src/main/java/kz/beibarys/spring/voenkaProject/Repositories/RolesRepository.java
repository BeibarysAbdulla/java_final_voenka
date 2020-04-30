package kz.beibarys.spring.voenkaProject.Repositories;

import kz.beibarys.spring.voenkaProject.Entities.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolesRepository extends JpaRepository<Roles,Long> {
    Roles getById(Long id);
}

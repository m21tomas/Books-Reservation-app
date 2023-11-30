package soft.project.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import soft.project.demo.enums.Role;
import soft.project.demo.model.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, Integer>{

	List<Authority> findByAuthority(Role role);

}

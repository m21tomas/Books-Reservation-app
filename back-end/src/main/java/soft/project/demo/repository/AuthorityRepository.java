package soft.project.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import soft.project.demo.enums.Role;
import soft.project.demo.model.Authority;
import soft.project.demo.model.User;

public interface AuthorityRepository extends JpaRepository<Authority, Integer>{

	List<Authority> findByAuthority(Role role);
	
	List<Authority> findByUser(User user);

	@Modifying
    @Query("DELETE FROM Authority a WHERE a.user.id = :userId")
    void deleteByUserId(@Param("userId") Integer userId);
}

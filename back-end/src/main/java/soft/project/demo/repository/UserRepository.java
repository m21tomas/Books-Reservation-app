package soft.project.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import soft.project.demo.enums.Role;
import soft.project.demo.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	
	Optional<User> findByUsername(String username);

	void deleteByUsername(String username);
	
	@Query("SELECT u FROM User u LEFT JOIN Authority a ON u.id=a.user.id WHERE a.authority=?1")
	List<User> findUsersByAuthority(Role role);
}

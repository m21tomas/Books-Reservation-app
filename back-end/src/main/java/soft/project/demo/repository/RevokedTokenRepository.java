package soft.project.demo.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import soft.project.demo.model.RevokedToken;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, Integer> {
	boolean existsByToken(String token);

	@Query("SELECT CASE WHEN COUNT(rt) > 0 THEN true ELSE false END FROM RevokedToken rt WHERE rt.token = :token AND rt.revocationDate <= :date")
	boolean existsByTokenAndRevocationDateBeforeOrEqualTo(@Param("token") String token, @Param("date") LocalDateTime date);

	RevokedToken findByToken(String token);
}

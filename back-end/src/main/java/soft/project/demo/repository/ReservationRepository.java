package soft.project.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import soft.project.demo.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Integer>{
	
	@Query(value="SELECT * FROM Reservation A JOIN Books B ON A.book_id=B.id WHERE B.title=?1", nativeQuery = true)
	Optional<List<Reservation>> findByBookTitle(String title);
	
	@Query("Select r FROM Reservation r JOIN User u ON r.user.id=u.id WHERE u.username=?1")
	Optional<List<Reservation>> findByUser(String username);
	
	@Query(value="SELECT * FROM Reservation ORDER BY id", countQuery = "SELECT count(*) FROM Reservation", nativeQuery = true)
	Page<Reservation> findAPageOfAll(Pageable pageable); 
}

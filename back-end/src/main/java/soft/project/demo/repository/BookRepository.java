package soft.project.demo.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import soft.project.demo.model.Book;

public interface BookRepository extends JpaRepository<Book, Integer> {

	Optional<Book> findByTitle(String title);
	
	@Query(value="SELECT * FROM Books ORDER BY id", countQuery = "SELECT count(*) FROM Books", nativeQuery = true)
	Page<Book> findAPageOfAll(Pageable pageable);

	@Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(concat('%', ?1,'%'))")
	Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);

}

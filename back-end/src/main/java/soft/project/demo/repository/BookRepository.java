package soft.project.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import soft.project.demo.model.Book;

public interface BookRepository extends JpaRepository<Book, Integer> {

}

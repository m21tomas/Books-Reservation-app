package soft.project.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import soft.project.demo.model.FavoriteBook;

public interface FavoriteBookRepository extends JpaRepository<FavoriteBook, Integer> {

}

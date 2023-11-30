package soft.project.demo.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import soft.project.demo.dto.CategoryDTO;
import soft.project.demo.exception.ExistingBookCategoryException;
import soft.project.demo.exception.NonExistingBookCategoryException;
import soft.project.demo.model.Book;
import soft.project.demo.model.Category;
import soft.project.demo.repository.BookRepository;
import soft.project.demo.repository.CategoryRepository;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository categoryRepo;
	
	@Autowired
	private BookRepository bookRepo;
	
	@Transactional(readOnly = true)
	public Category findByName (String name) {
		return categoryRepo.findByName(name).orElse(null);
	}
	
	@Transactional(readOnly = true)
	public Category findById (int id) {
		return categoryRepo.findById(id).orElse(null);
	}
	
	@Transactional(readOnly = true)
	public List<Category> findAllCategories () {
		return categoryRepo.findAll();
	}
	
	@Transactional
	public boolean createCategory (CategoryDTO catDto) {
		Optional<Category> existingCategory = categoryRepo.findByName(catDto.getCategoryName());
		
		if(existingCategory.isPresent()) {
			throw new ExistingBookCategoryException(catDto.getCategoryName() + " category already exists.");
		}
		
		Category newCategory = new Category();
		
		newCategory.setName(catDto.getCategoryName());
		
		Category savedCategory = categoryRepo.save(newCategory);
		
		return categoryRepo.findById(savedCategory.getId()).isPresent();
	}
	
	@Transactional
	public boolean updateCategoryById (int id, CategoryDTO catDto) {
		Category eCat = findById(id);
		
		if(eCat != null) {
			if(catDto != null) {	
				eCat.setName(catDto.getCategoryName());
				categoryRepo.save(eCat);
				return true;
			}
			else {
				throw new NullPointerException("Category object is null or wrong or there is no category");
			}
		}
		else {
			throw new NonExistingBookCategoryException("There is no books category with id: "+id);
		}
	}
	
	@Transactional
	public boolean deleteCategoryByName (String nameToDel) {
		Category catToDel = findByName(nameToDel);
		
		if (catToDel != null) {
	        // Retrieve the "No Category" category or create it if it doesn't exist
	        Category noCategory = categoryRepo.findByName("No Category").orElseGet(() -> {
	            Category newCategory = new Category("No Category");
	            return categoryRepo.save(newCategory);
	        });

	        // Retrieve the books associated with the category to be deleted
	        Set<Book> booksToUpdate = catToDel.getBooks();

	        for (Book book : booksToUpdate) {
	            book.setCategory(noCategory);
	        }

	        // Save the updated books
	        bookRepo.saveAll(booksToUpdate);

	        // Delete the category
	        categoryRepo.delete(catToDel);

	        return true;
	    } else {
	        return false;
	    }
	}
	
}

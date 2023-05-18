package soft.project.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import soft.project.demo.dto.CategoryDTO;
import soft.project.demo.exception.ExistingBookCategoryException;
import soft.project.demo.exception.NonExistingBookCategoryException;
import soft.project.demo.model.Category;
import soft.project.demo.repository.CategoryRepository;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository categoryRepo;
	
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
		
		if(catToDel != null) {
			categoryRepo.delete(catToDel);
			return true;
		}
		else {
			return false;
		}
	}
	
}

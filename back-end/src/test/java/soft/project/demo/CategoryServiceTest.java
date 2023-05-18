package soft.project.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import soft.project.demo.dto.CategoryDTO;
import soft.project.demo.exception.ExistingBookCategoryException;
import soft.project.demo.exception.NonExistingBookCategoryException;
import soft.project.demo.model.Category;
import soft.project.demo.service.CategoryService;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class CategoryServiceTest {
	
	@Autowired
	private CategoryService catService;
	
	@Test
	@Order(1)
	void testCreateCategory () {
		CategoryDTO catDto = new CategoryDTO("Novel");
		
		boolean created = catService.createCategory(catDto);
		
		assertTrue(created);
		
		ExistingBookCategoryException exception = assertThrows(ExistingBookCategoryException.class, () -> {catService.createCategory(catDto);});
		
		assertEquals("Novel category already exists.", exception.getMessage());
	}
	
	@Test
	@Order(2)
	void testFindAllAndById() {
		List<Category> list = catService.findAllCategories();
		
		assertTrue(list.size()>0);
		
		int theId = -1;
		for(Category item: list) {
			theId = item.getId();
		}
		
		assertTrue(theId != -1 && theId>0);
		
		Category obj = catService.findById(theId);
		
		assertTrue(obj != null && obj.getName().equals("Novel"));
	}
	
	@Test
	@Order(3)
	void testUpdateCategoryById() {
		List<Category> list = catService.findAllCategories();
		
		int theId = -1;
		for(Category item: list) {
			theId = item.getId();
		}
		
		CategoryDTO newCategory = new CategoryDTO("Fantastic");
		
		boolean updateStatus = catService.updateCategoryById(theId, newCategory);
		
		assertTrue(updateStatus && catService.findById(theId).getName().equals("Fantastic"));
		
		final int theIdCopy = theId;
		NonExistingBookCategoryException exception = assertThrows (NonExistingBookCategoryException.class, () -> {
			catService.updateCategoryById(theIdCopy*4+4, newCategory);
		});
		
		assertEquals("There is no books category with id: "+(theId*4+4), exception.getMessage());
		
		NullPointerException exc = assertThrows (NullPointerException.class, () -> {
			catService.updateCategoryById(theIdCopy, null);
		});
		
		assertEquals("Category object is null or wrong or there is no category", exc.getMessage());
	}
	
	@Test
	@Order(4)
	void testDeleteCategoryName () {
		boolean deleteStatus = catService.deleteCategoryByName("Fantastic");
		assertTrue(deleteStatus);
		
		boolean falseStatus = catService.deleteCategoryByName("Novel");
		
		assertFalse(falseStatus);
	}
	
}

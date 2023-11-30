package soft.project.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class CategoryServiceTest {
	
	@Autowired
	private CategoryService catService;
	
	@Test
	@Order(1)
	void testCreateCategory () {
		CategoryDTO catDto = new CategoryDTO("TestRomance");
		
		boolean created = catService.createCategory(catDto);
		
		assertTrue(created);
		
		ExistingBookCategoryException exception = assertThrows(ExistingBookCategoryException.class, () -> {catService.createCategory(catDto);});
		
		assertEquals(catDto.getCategoryName()+ " category already exists.", exception.getMessage());
	}
	
	@Test
	@Order(2)
	void testFindAllAndById() {
		List<Category> list = catService.findAllCategories();
		
		assertTrue(list.size()>0);
		
		int theId = -1;
		for(Category item: list) {
			if(item.getName().equals("TestRomance")) {
				theId = item.getId();
				break;
			}
		}
		
		assertTrue(theId != -1 && theId>0);
		
		Category obj = catService.findById(theId);
		
		assertTrue(obj != null && obj.getName().equals("TestRomance"));
	}
	
	@Test
	@Order(3)
	void testUpdateCategoryById() {
		List<Category> list = catService.findAllCategories();
		
		int theId = -1;
		for(Category item: list) {
			if(item.getName().equals("TestRomance")) {
				theId = item.getId();
				break;
			}
		}
		
		CategoryDTO newCategory = new CategoryDTO("TestFantastic");
		
		boolean updateStatus = catService.updateCategoryById(theId, newCategory);
		
		assertTrue(updateStatus && catService.findById(theId).getName().equals("TestFantastic"));
		
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
	void testDeleteCreatedCategoryNames () {
		boolean deleteStatus = catService.deleteCategoryByName("TestFantastic");
		assertTrue(deleteStatus);
		
		boolean falseStatus = catService.deleteCategoryByName("TestRomance");
		
		assertFalse(falseStatus);
		
		if(catService.findByName("No Category") != null) {
			catService.deleteCategoryByName("No Category");
		}
		
		assertNull(catService.findByName("No Category"));
	}
	
}

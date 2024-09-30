package soft.project.demo.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import soft.project.demo.dto.CategoryDTO;
import soft.project.demo.model.Category;
import soft.project.demo.service.CategoryService;

@RestController
@RequestMapping(path = "/api/category")
@Tag(name = "Category Controller")
public class CategoryController {
	
	private static final Logger LOG = LoggerFactory.getLogger(CategoryController.class);
	
	@Autowired
	private CategoryService catService;
	
	@Secured({"Administrator"})
	@PostMapping("/newCategory")
	@Operation(summary = "Create new book category by request body")
	public ResponseEntity<String> createCategory(@RequestBody CategoryDTO catDTO){
		
		String principalName = SecurityContextHolder.getContext().getAuthentication().getName();
		
		if(catService.createCategory(catDTO)) {
			LOG.info("[{}]: New category - {} - created", principalName, catDTO.getCategoryName());
			return new ResponseEntity<String>("New category - "+catDTO.getCategoryName()+" - created", HttpStatus.CREATED);
		}
		LOG.error("[{}]: Category - {} - is NOT created", principalName, catDTO.getCategoryName());
		return new ResponseEntity<String>("Category - "+catDTO.getCategoryName()+" - is NOT created", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@Secured({"Administrator"})
	@PostMapping("/newCategory/{categoryName}")
	@Operation(summary = "Create new book category by path variable")
	public ResponseEntity<String> createCategoryByPathVariable(@PathVariable String categoryName){
		
		String principalName = SecurityContextHolder.getContext().getAuthentication().getName();
		
		CategoryDTO catDtoObj = new CategoryDTO(categoryName);
		
		if(catService.createCategory(catDtoObj)) {
			LOG.info("[{}]: New category - {} - created", principalName, categoryName);
			return new ResponseEntity<String>("New category - "+categoryName+" - created", HttpStatus.CREATED);
		}
		LOG.error("[{}]: Category - {} - is NOT created", principalName, categoryName);
		return new ResponseEntity<String>("Category - "+categoryName+" - is NOT created", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@GetMapping("/{id}")
	@Operation(summary = "Get Category by id")
	public ResponseEntity<?> getCategoryById(int id){
		String principalName = SecurityContextHolder.getContext().getAuthentication().getName();
		
		Category respEnt = catService.findById(id);
		if(respEnt != null) {
			LOG.info("[{}]: Returning category - {}", principalName, respEnt.getName());
			return new ResponseEntity<Category>(respEnt, HttpStatus.OK);
		}
		else {
			LOG.error("[{}]: No category with id of {}", principalName, id);
			return new ResponseEntity<String>("No category with id of "+id, HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/listAll")
	@Operation(summary = "Get the list of all categories")
	public ResponseEntity<?> getAllCategories(){
		String principalName = SecurityContextHolder.getContext().getAuthentication().getName();
		
		List<Category> catList = catService.findAllCategories();
		
		if(catList.isEmpty()) {
			LOG.error("[{}]: No categories", principalName);
			return new ResponseEntity<String>("No any categories", HttpStatus.NOT_FOUND);
		}
		else {
			LOG.info("[{}]: Returning the list of categories", principalName);
			return ResponseEntity.ok().body(catList);
		}
	}
	
	@Secured({ "Administrator" })
	@GetMapping(path = "/page")
	@Operation(summary = "Get a page of categories")
	public Page<Category> getPageOfCategories(@RequestParam("page") int page, @RequestParam("size") int size){
		String adminNickname = SecurityContextHolder.getContext().getAuthentication().getName();
		
		Sort.Order order = new Sort.Order(Sort.Direction.ASC, "id");
		
		Pageable pageable = PageRequest.of(page, size, Sort.by(order));
		
		LOG.info("[{}]: "+"Sending a page of categories", adminNickname);
		
		return catService.getPageOfAllCategories(pageable);
	}
	
	@Secured({"Administrator"})
	@PutMapping("/updateCategory/{id}")
	@Operation(summary = "Change book category title")
	public ResponseEntity<String> updateCategory(@PathVariable Integer id, @RequestBody CategoryDTO catDto){
		String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
		
		if(catService.updateCategoryById(id, catDto)) {
			LOG.info("[{}]: Category title changed", currentUsername);
			return new ResponseEntity<String>("Category title changed", HttpStatus.OK);
		} else {
			LOG.error("[{}]: Category not changed", currentUsername);
			return new ResponseEntity<String>("Category not changed", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Secured({ "Administrator" })
	@DeleteMapping(path = "/delete/{title}")
	@Operation(summary = "Delete category")
	public ResponseEntity<String> deleteCategory(@PathVariable final String title){
		String adminNickname = SecurityContextHolder.getContext().getAuthentication().getName();
		catService.deleteCategoryByName(title);
		if(catService.findByName(title) == null) {
			LOG.info("[{}]: Category - {} - deleted ", adminNickname , title);
			return new ResponseEntity<String>("Category - "+title+" - deleted", HttpStatus.OK);
		}else {
			LOG.error("[{}]: Category - {} - not deleted ", adminNickname , title);
			return new ResponseEntity<String>("Category - "+title+" - not deleted", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}

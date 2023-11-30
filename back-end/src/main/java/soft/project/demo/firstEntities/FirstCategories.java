package soft.project.demo.firstEntities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import soft.project.demo.dto.CategoryDTO;
import soft.project.demo.exception.ExistingBookCategoryException;
import soft.project.demo.repository.CategoryRepository;
import soft.project.demo.service.CategoryService;

@Component
@DependsOn("firstAdmin")
public class FirstCategories {
	
	private static final Logger LOG = LoggerFactory.getLogger(FirstCategories.class);
	
	@Autowired
	private CategoryService categoryService;
	 
	@Autowired
	private CategoryRepository categoryRepo;

	 @PostConstruct
	 public void initializeCategories() {
		 try {
			 if(categoryRepo.findAll().isEmpty()) {
				  LOG.info("There are no any categories in repository. Creating new example categories...");
			      String[] bookGenres = {
			          "Classic", "Dystopian Novel", "Fantasy", "Romance", "Classic",
			          "Magical Realism", "Mystery", "Fantasy", "Adventure", "Historical Fiction",
			          "Classic", "Adventure", "Fantasy", "Mystery", "Classic",
			          "Horror", "Gothic Fiction", "Modernist", "Dystopian Novel", "Classic"
			      };
		
			      for (String genre : bookGenres) {
			          createCategory(genre);
			      }
			      LOG.info("New categories created and added to the Cetegory repository");
			 }
		 }
		 catch (Exception e){
	            LOG.error("Error during category initialization: {}", e.getMessage());
	            // Log the exception and continue with other bean initializations
	        }
	  }

	  private void createCategory(String categoryName) {
		  try {
			  CategoryDTO categoryDTO = new CategoryDTO(categoryName);
			  categoryService.createCategory(categoryDTO);
		  } catch (ExistingBookCategoryException e) {
	          LOG.warn("Category '{}' already exists. Skipping creation.", categoryName);
	      }
	  }
}

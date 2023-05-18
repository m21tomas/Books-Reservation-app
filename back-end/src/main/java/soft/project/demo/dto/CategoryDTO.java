package soft.project.demo.dto;

public class CategoryDTO {
	
	private String categoryName;
	
	public CategoryDTO() {}

	public CategoryDTO(String categoryName) {
		super();
		this.categoryName = categoryName;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	
	
}

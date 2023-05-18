package soft.project.demo.exception;

public class NonExistingBookCategoryException extends RuntimeException {

	private static final long serialVersionUID = 7897865783797697121L;
	
	private String message;
	
	public NonExistingBookCategoryException() {}

	public NonExistingBookCategoryException(String message) {
		super();
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}

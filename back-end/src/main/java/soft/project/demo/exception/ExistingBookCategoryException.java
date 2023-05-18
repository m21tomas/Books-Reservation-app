package soft.project.demo.exception;

public class ExistingBookCategoryException extends RuntimeException {

	private static final long serialVersionUID = 3001995083409772614L;
	
	private String message;
	
	public ExistingBookCategoryException() {}

	public ExistingBookCategoryException(String message) {
		super();
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}

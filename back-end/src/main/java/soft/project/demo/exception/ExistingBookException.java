package soft.project.demo.exception;

public class ExistingBookException extends RuntimeException {
	
	private static final long serialVersionUID = 7983847354927497414L;
	
	private String message;
	public ExistingBookException() {}
	
	public ExistingBookException(String string) {
		super();
		this.message = string;
	}

	public String getMessage() {
		return message;
	}

}

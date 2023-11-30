package soft.project.demo.exception;

public class InvalidIsbnException extends RuntimeException {

	private static final long serialVersionUID = -2199954432782401224L;
	
	private String message;

	public InvalidIsbnException(String message) {
		super();
		this.message = message;
	}
	
	public InvalidIsbnException() {}

	public String getMessage() {
		return message;
	}
}

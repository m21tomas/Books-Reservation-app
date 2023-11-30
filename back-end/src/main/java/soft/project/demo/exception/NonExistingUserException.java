package soft.project.demo.exception;

public class NonExistingUserException extends RuntimeException {

	private static final long serialVersionUID = 3273971891565966297L;

	private String message;

	public NonExistingUserException() {}
	public NonExistingUserException(String message) {
		super(message);
		this.message = message;
	}
	public String getMessage() {
		return message;
	}

}

package soft.project.demo.exceptionHandling.exception;

public class ExistingUserException extends RuntimeException {

	private static final long serialVersionUID = -3850426152472732458L;
	private String message;

	public ExistingUserException() {}
	public ExistingUserException(String message) {
		super(message);
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
}

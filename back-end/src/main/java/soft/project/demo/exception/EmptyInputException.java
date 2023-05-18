package soft.project.demo.exception;

public class EmptyInputException extends RuntimeException {

	private static final long serialVersionUID = 1888470992234450784L;
	private String message;
    public EmptyInputException(String message) {
        super(message);
        this.message = message;
    }
    public EmptyInputException() {
    }
	public String getMessage() {
		return message;
	}
}

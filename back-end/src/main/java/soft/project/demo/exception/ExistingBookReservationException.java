package soft.project.demo.exception;

public class ExistingBookReservationException extends RuntimeException {

	private static final long serialVersionUID = 5558525082765838950L;
	
	private String message;
	
	public ExistingBookReservationException(){}

	public ExistingBookReservationException(String message) {
		super();
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}

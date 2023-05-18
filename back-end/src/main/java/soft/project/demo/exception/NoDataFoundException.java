package soft.project.demo.exception;

public class NoDataFoundException extends RuntimeException {

	private static final long serialVersionUID = -9181220243628707829L;

	public NoDataFoundException() {
        super("No data found");
    }
}

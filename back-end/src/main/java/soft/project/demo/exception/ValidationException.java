package soft.project.demo.exception;

import org.springframework.validation.BindingResult;

public class ValidationException extends RuntimeException {

	private static final long serialVersionUID = 7070757967168673204L;
	private BindingResult bindingResult;

	public ValidationException() {}
	public ValidationException(BindingResult bindingResult) {
		this.bindingResult = bindingResult;
	}
	public BindingResult getBindingResult() {
		return bindingResult;
	}
}

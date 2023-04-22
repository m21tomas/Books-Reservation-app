package soft.project.demo.exceptionHandling.advisor;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import soft.project.demo.exceptionHandling.exception.EmptyInputException;
import soft.project.demo.exceptionHandling.exception.ExistingUserException;
import soft.project.demo.exceptionHandling.exception.NoDataFoundException;

@ControllerAdvice
public class MyControllerAdvice extends ResponseEntityExceptionHandler {
	
	@ExceptionHandler(NoDataFoundException.class)
    public ResponseEntity<Object> handleNodataFoundException(
        NoDataFoundException ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", "No data found");

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

	@ExceptionHandler(EmptyInputException.class)
	public ResponseEntity<Object> handleEmptyInputException(EmptyInputException elementException)
	{
		Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", elementException.getMessage());
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ExistingUserException.class)
	public ResponseEntity<Object> handleExistingUserException(ExistingUserException el) {
		Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", el.getMessage());
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}
}

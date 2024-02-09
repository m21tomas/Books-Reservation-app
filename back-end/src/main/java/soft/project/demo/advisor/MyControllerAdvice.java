package soft.project.demo.advisor;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import soft.project.demo.exception.EmptyInputException;
import soft.project.demo.exception.ExistingBookCategoryException;
import soft.project.demo.exception.ExistingBookException;
import soft.project.demo.exception.ExistingBookReservationException;
import soft.project.demo.exception.ExistingUserException;
import soft.project.demo.exception.InvalidIsbnException;
import soft.project.demo.exception.NonExistingUserException;
import soft.project.demo.exception.NoDataFoundException;
import soft.project.demo.exception.NonExistingBookCategoryException;
import soft.project.demo.exception.NonExistingBookException;

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
	
	@ExceptionHandler(NonExistingUserException.class)
	public ResponseEntity<Object> handleNonExistingUserException(NonExistingUserException el) {
		Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", el.getMessage());
		return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(ExistingBookCategoryException.class)
	public ResponseEntity<Object> handleExistingBookCategoryException(ExistingBookCategoryException el) {
		Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", el.getMessage());
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(NonExistingBookCategoryException.class)
	public ResponseEntity<Object> handleNonExistingBookCategoryException(NonExistingBookCategoryException mes) {
		Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", mes.getMessage());
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(NonExistingBookException.class)
	public ResponseEntity<Object> handleNonExistingBookException(NonExistingBookException mes){
		Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", mes.getMessage());
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ExistingBookReservationException.class)
	public ResponseEntity<Object> handleExistingBookReservationException(ExistingBookReservationException mes){
		Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", mes.getMessage());
		return new ResponseEntity<>(body, HttpStatus.CONFLICT);
	}
	
	@ExceptionHandler(ExistingBookException.class)
	public ResponseEntity<Object> handleExistingBookException(ExistingBookException mes){
		Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", mes.getMessage());
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Object> handleDataIntegrityViolationException(
	  DataIntegrityViolationException ex, WebRequest request) {
		Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
	    return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
	    body.put("error", "Invalid argument");
	    body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
	
	@ExceptionHandler(InvalidIsbnException.class)
	public ResponseEntity<Object> handleInvalidIsbnException(InvalidIsbnException ex){
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
	    body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}
}

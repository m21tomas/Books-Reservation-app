package soft.project.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import soft.project.demo.dto.ReservationDTO;
import soft.project.demo.exception.EmptyInputException;
import soft.project.demo.exception.ExistingBookReservationException;
import soft.project.demo.model.Book;
import soft.project.demo.model.Reservation;
import soft.project.demo.model.UserInfo;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class ReservationServiceTest {
	
	@Autowired
	private ReservationService resService;
	@Autowired
	private BookService bookService;
	@Autowired
	private UserService userService;
	
	@Test
	@Order(1)
	void testCreateReservation () {
		List<UserInfo> users = userService.getAllUsers();
		
		String username = "";
		
		Optional<String> getUsername = users.stream()
		        .filter(user -> user.getRoles().size() == 1)
		        .filter(user -> user.getRoles().stream().anyMatch(auth -> auth.getAuthority().equals("Reader")))
		        .map(UserInfo::getUsername)
		        .findFirst();
		
		if(getUsername.isPresent()) {
			username = getUsername.get();
		}
		
		final String existingUsername = username;
		
		List<Book> books = bookService.findAll();
		
		int bookId = books.get(books.size()/2).getId();
		
		List<ReservationDTO> userResList = Collections.emptyList();
		
		try {
			userResList = resService.findByUser(username);	
		}
		catch(EmptyInputException exception) {
			assertEquals("User " + username + " has no reservations", exception.getMessage());
		}
		
		String bookTitle = bookService.findById(bookId).getTitle();
		
		if(!userResList.isEmpty()) {
			Optional<ReservationDTO> exisitingReservation = userResList.stream()
			.filter(res -> res.getBookid()==bookId)
			.filter(res -> 
				(res.getReservationDate().isBefore(LocalDateTime.now()) ||
				 res.getReservationDate().isEqual(LocalDateTime.now())) &&
				(res.getReturnDate().isAfter(LocalDate.now()) ||
				 res.getReturnDate().isEqual(LocalDate.now()))
			)
			.findFirst();
			
			if(exisitingReservation.isPresent()) {
				ExistingBookReservationException exception = assertThrows(ExistingBookReservationException.class, () -> {resService.addReservation(existingUsername, bookId, 31);});
				
				assertEquals("User " + existingUsername + " has already reserved the same book in the given time interval", exception.getMessage());
			}
			else {
				Reservation testRes = resService.addReservation(username, bookId, 31);
				
				assertEquals(username, testRes.getUser().getUsername());
				assertEquals(bookTitle, testRes.getBook().getTitle());
				assertTrue(LocalDate.now().isBefore(testRes.getReturnDate()));
			}
		}
		else {
			Reservation testRes = resService.addReservation(username, bookId, 31);
			
			assertEquals(username, testRes.getUser().getUsername());
			assertEquals(bookTitle, testRes.getBook().getTitle());
			assertTrue(LocalDate.now().isBefore(testRes.getReturnDate()));
		}
	}
	
	
	
	
}

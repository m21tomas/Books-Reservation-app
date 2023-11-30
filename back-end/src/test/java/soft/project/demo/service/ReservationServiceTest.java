package soft.project.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

import jakarta.transaction.Transactional;
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
		
		String manager = "";
		
		Optional<String> getManager = users.stream()
		        .filter(user -> user.getRoles().size() == 2)
		        .filter(user -> user.getUsername().equals("manager@manager.lt"))
		        .map(UserInfo::getUsername)
		        .findFirst();
		
		if(getManager.isPresent()) {
			manager = getManager.get();
		}
		
		String admin = "";
		
		Optional<String> getAdmin = users.stream()
		        .filter(user -> user.getRoles().size() == 1)
		        .filter(user -> user.getUsername().equals("admin@admin.lt"))
		        .map(UserInfo::getUsername)
		        .findFirst();
		
		if(getAdmin.isPresent()) {
			admin = getAdmin.get();
		}
		
		final String existingUsername = username;
		
		List<Book> books = bookService.findAll();
		
		int bookId = books.get(books.size()/2).getId();
		
		int bookId2 = books.get(books.size()/4).getId();
		
		assertNotNull(resService.addReservation(manager, bookId, 20));
		
		assertNotNull(resService.addReservation(admin, bookId2, 10));
		
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
	
	@Transactional
	@Test
	@Order(2)
	void testReadReservation() {
		List<Reservation> reservations = resService.findAllReservations();
		
		assertTrue(reservations.size() > 0);
		
		int id = 0;
		
		for(Reservation res : reservations) {
			if(res.getUser().getUsername().equals("reader@reader.lt")) {
				id = res.getId();
				break;
			}
		}
		
		assertNotNull(resService.findById(id));
		
		List<ReservationDTO> reservationsDTO = resService.findAllReservationsDto();
		
		assertEquals(reservations.size(), reservationsDTO.size());
		
		assertEquals(3, reservationsDTO.size());
		
		ReservationDTO resDto = resService.findByIdDto(id);
		
		assertNotNull(resDto);
		
		String bookTitle = resDto.getBookTitle();
		
		String username = resDto.getUsername();
		
		List<ReservationDTO> listRes = resService.findByBookTitle(bookTitle);
		
		assertTrue(listRes.size() == 2);
		
		for(ReservationDTO dtoItem : listRes) {
			if(dtoItem.getBookTitle().equals(bookTitle)) {
				assertEquals(bookTitle, dtoItem.getBookTitle());
			}
		}
		
		List<ReservationDTO> listResByUser = resService.findByUser(username);
		
		assertTrue(listResByUser.size() > 0);
		
		assertEquals(1, listResByUser.size());
		
		for(ReservationDTO dtoItem2 : listResByUser) {
			if(dtoItem2.getUsername().equals(username)) {
				assertEquals(username, dtoItem2.getUsername());
				break;
			}
		}
		
		var response = resService.getAllReservationsPage(0, 5);
		
		List<ReservationDTO> resers = response.getContent();
		int pageItemsNum = response.getContent().size();
		
		for(ReservationDTO item : resers) {
			if(item.getBookTitle().equals(bookTitle)) {
				assertEquals(bookTitle, item.getBookTitle());
				break;
			}
		}
		
		assertEquals(3, resers.size());
		
		assertEquals(3, pageItemsNum);
	}
	
	
}

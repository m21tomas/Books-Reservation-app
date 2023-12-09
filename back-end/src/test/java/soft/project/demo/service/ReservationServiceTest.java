package soft.project.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
import soft.project.demo.dto.BookRequestDTO;
import soft.project.demo.dto.ReservationDTO;
import soft.project.demo.enums.ReservationStatus;
import soft.project.demo.exception.EmptyInputException;
import soft.project.demo.exception.ExistingBookReservationException;
import soft.project.demo.exception.NonExistingBookException;
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
		
		final String existingUser = username;
		final String existingAdmin = admin;
		final String existingManager = manager;
		
		List<Book> books = bookService.findAll();
		
		int bookId = books.get(books.size()/2).getId();
		
		int bookId2 = books.get(books.size()/4).getId();
		
		List<ReservationDTO> userResList = Collections.emptyList();
		List<ReservationDTO> adminResList = Collections.emptyList();
		List<ReservationDTO> managerResList = Collections.emptyList();
		
		try {
			userResList = resService.findByUser(username);	
		}
		catch(EmptyInputException exception) {
			assertEquals("User " + username + " has no reservations", exception.getMessage());
		}
		try {
			adminResList = resService.findByUser(admin);	
		}
		catch(EmptyInputException exception) {
			assertEquals("User " + admin + " has no reservations", exception.getMessage());
		}
		try {
			managerResList = resService.findByUser(manager);	
		}
		catch(EmptyInputException exception) {
			assertEquals("User " + manager + " has no reservations", exception.getMessage());
		}
		
		String bookTitle = bookService.findById(bookId).getTitle();
		String book2Title = bookService.findById(bookId2).getTitle();
		
		Optional<ReservationDTO> userRes = userResList.stream()
			.filter(res -> res.getBookid()==bookId)
			.filter(res -> 
				(res.getReservationDate().isBefore(LocalDateTime.now()) ||
				 res.getReservationDate().isEqual(LocalDateTime.now())) &&
				(res.getReturnDate().isAfter(LocalDate.now()) ||
				 res.getReturnDate().isEqual(LocalDate.now()))
			)
			.findFirst();
		Optional<ReservationDTO> adminRes = adminResList.stream()
				.filter(res -> res.getBookid()==bookId2)
				.filter(res -> 
					(res.getReservationDate().isBefore(LocalDateTime.now()) ||
					 res.getReservationDate().isEqual(LocalDateTime.now())) &&
					(res.getReturnDate().isAfter(LocalDate.now()) ||
					 res.getReturnDate().isEqual(LocalDate.now()))
				)
				.findFirst();
		Optional<ReservationDTO> managerRes = managerResList.stream()
				.filter(res -> res.getBookid()==bookId)
				.filter(res -> 
					(res.getReservationDate().isBefore(LocalDateTime.now()) ||
					 res.getReservationDate().isEqual(LocalDateTime.now())) &&
					(res.getReturnDate().isAfter(LocalDate.now()) ||
					 res.getReturnDate().isEqual(LocalDate.now()))
				)
				.findFirst();
			
		if(userRes.isPresent()) {
			ExistingBookReservationException exception = assertThrows(ExistingBookReservationException.class, () -> {resService.addReservation(existingUser, bookId, 31);});
			
			assertEquals("User " + existingUser + " has already reserved the same book in the given time interval", exception.getMessage());
		}
		else {
			Reservation testRes = resService.addReservation(username, bookId, 31);
			
			assertEquals(username, testRes.getUser().getUsername());
			assertEquals(bookTitle, testRes.getBook().getTitle());
			assertTrue(LocalDate.now().isBefore(testRes.getReturnDate()));
		}
		
		if(managerRes.isPresent()) {
			ExistingBookReservationException exception = assertThrows(ExistingBookReservationException.class, () -> {resService.addReservation(existingManager, bookId, 20);});
			
			assertEquals("User " + existingManager + " has already reserved the same book in the given time interval", exception.getMessage());
		}
		else {
			Reservation testRes2 = resService.addReservation(manager, bookId, 20);
			
			assertEquals(manager, testRes2.getUser().getUsername());
			assertEquals(bookTitle, testRes2.getBook().getTitle());
			assertTrue(LocalDate.now().isBefore(testRes2.getReturnDate()));
		}
		
		if(adminRes.isPresent()) {
			ExistingBookReservationException exception = assertThrows(ExistingBookReservationException.class, () -> {resService.addReservation(existingAdmin, bookId2, 10);});
			
			assertEquals("User " + existingAdmin + " has already reserved the same book in the given time interval", exception.getMessage());
		}
		else {
			Reservation testRes3 = resService.addReservation(admin, bookId2, 10);
			
			assertEquals(admin, testRes3.getUser().getUsername());
			assertEquals(book2Title, testRes3.getBook().getTitle());
			assertTrue(LocalDate.now().isBefore(testRes3.getReturnDate()));
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
	
	@Test
	@Order(3)
	void testUpdateReservation() {
		List<ReservationDTO> reservationsDTO = resService.findAllReservationsDto();
		
		int resUserId = -1, resAdminId = -1, resManagerId = -1;
		int resNum = 0;
		ReservationDTO takenUserDto = new ReservationDTO();
		ReservationDTO takenAdminDto = new ReservationDTO();
		ReservationDTO takenManagerDto = new ReservationDTO();
		
		for(ReservationDTO dto : reservationsDTO) {
			resNum = dto.getId();
			if(dto.getUsername().equals("manager@manager.lt")) {
				resManagerId = dto.getId();
				takenManagerDto = dto;
			} else
			if(dto.getUsername().equals("reader@reader.lt")) {
				resUserId = dto.getId();
				takenUserDto = dto;
			} else
			if(dto.getUsername().equals("admin@admin.lt")) {
				resAdminId = dto.getId();
				takenAdminDto = dto;
			}
		}
		
		final int finalResNum = resNum;
		
		NonExistingBookException exc = assertThrows(NonExistingBookException.class, () -> {resService.updateReservationData(new ReservationDTO(), finalResNum+1);});
		
		assertEquals("Update: No such reservation with id: "+(finalResNum+1), exc.getMessage());
		
		List<Book> books = bookService.findAll();
		
		int changedId = books.get(books.size()-1).getId();
		
		takenManagerDto.setBookid(changedId);
		
		ReservationDTO changedRes1 = resService.updateReservationData(takenManagerDto, resManagerId);
		
		assertEquals(changedId, changedRes1.getBookid());
		
		changedRes1.setReservationDate(changedRes1.getReservationDate().minus(2, ChronoUnit.DAYS));
		
		changedRes1.setReservationDate(changedRes1.getReservationDate().minusHours(2));
		
		changedRes1.setReturnDate(changedRes1.getReturnDate().plusDays(1));
		
		ReservationDTO changedRes2 = resService.updateReservationData(changedRes1, resManagerId);
		
		assertTrue(takenManagerDto.getReservationDate().isAfter(changedRes2.getReservationDate()));
		
		assertTrue(takenManagerDto.getReturnDate().isBefore(changedRes2.getReturnDate()));
		
		takenAdminDto.setStatus(ReservationStatus.RESERVED);
		
		ReservationDTO changedAdminRes = resService.updateReservationData(takenAdminDto, resAdminId);
		
		assertEquals(ReservationStatus.RESERVED, changedAdminRes.getStatus());
		
		takenUserDto.setStatus(ReservationStatus.RETURNED);
		
		ReservationDTO changedReaderRes = resService.updateReservationData(takenUserDto, resUserId);
		
		assertEquals(ReservationStatus.RETURNED, changedReaderRes.getStatus());
		
		Book book = bookService.findById(takenUserDto.getBookid());
		
		BookRequestDTO bookResChange = new BookRequestDTO(book.getTitle(), book.getAuthor(), book.getSummary(), book.getIsbn(),
				book.getYear(), book.getPages(), book.getCirculation(), book.getReservationNumber()-1, book.getCategory().getName());
		
		assertTrue(bookService.updateBookData(bookResChange, takenUserDto.getBookid()));
		
		assertEquals(0, bookService.findById(11).getReservationNumber());
		
		changedReaderRes.setBookid(books.get(books.size()-2).getId());
		
		ReservationDTO changedReaderRes2 = resService.updateReservationData(changedReaderRes, resUserId);
		
		assertEquals(books.get(books.size()-2).getTitle(), changedReaderRes2.getBookTitle());

		changedRes2.setUsername("reader@reader.lt");
		
		ReservationDTO changedManagerToReader = resService.updateReservationData(changedRes2, resManagerId);
		
		assertEquals("reader@reader.lt", changedManagerToReader.getUsername());
	
	}
	
	@Test
	@Order(4)
	void testChangeResStatus() {
		List<ReservationDTO> reservationsDTO = resService.findAllReservationsDto();
		int resNum = 0;
		int resId1 = -1, resId2 = -1, resId3 = -1;
		
		for(ReservationDTO dto : reservationsDTO) {
			resNum = dto.getId();
			if(dto.getUsername().equals("reader@reader.lt") &&
			   dto.getBookid() == 19 &&
			  (dto.getReservationDate().isBefore(LocalDateTime.now()) ||
			   dto.getReservationDate().isEqual(LocalDateTime.now())) &&
			  (dto.getReturnDate().isAfter(LocalDate.now()) ||
			   dto.getReturnDate().isEqual(LocalDate.now()))
			  ) {
				resId1 = dto.getId();
			} else
			if(dto.getUsername().equals("reader@reader.lt") &&
			   dto.getBookid() == 20 &&
			  (dto.getReservationDate().isBefore(LocalDateTime.now()) ||
			   dto.getReservationDate().isEqual(LocalDateTime.now())) &&
			  (dto.getReturnDate().isAfter(LocalDate.now()) ||
			   dto.getReturnDate().isEqual(LocalDate.now()))
			  ) {
				resId2 = dto.getId();
			} else
			if(dto.getUsername().equals("admin@admin.lt") &&
			   dto.getBookid() == 6 &&
			  (dto.getReservationDate().isBefore(LocalDateTime.now()) ||
			   dto.getReservationDate().isEqual(LocalDateTime.now())) &&
			  (dto.getReturnDate().isAfter(LocalDate.now()) ||
			   dto.getReturnDate().isEqual(LocalDate.now()))
			  ) {
				resId3 = dto.getId();
			}
		}
		
		final int finResNum = resNum;
		
		NonExistingBookException exception = assertThrows(NonExistingBookException.class, () -> {resService.changeReservationStatus(finResNum+1, "Rejected");});
		
		assertEquals("Change Status: No such reservation with id: "+(finResNum+1), exception.getMessage());
		
		ReservationDTO stat1 = resService.changeReservationStatus(resId1, "Reserved");
		ReservationDTO stat2 = resService.changeReservationStatus(resId2, "Returned");
		ReservationDTO stat3 = resService.changeReservationStatus(resId3, "returned");
		
		assertEquals("Reserved", stat1.getStatus().getStatus());
		assertEquals("Returned", stat2.getStatus().getStatus());
		assertEquals("Returned", stat3.getStatus().getStatus());
		
		final Integer finResId1 = resId1;
		IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, () -> {resService.changeReservationStatus(finResId1, "Burned");});
		
		assertEquals("Change Status: Invalid reservation status: " + "Burned", exception2.getMessage());
	}
	
	@Test
	@Order(5)
	void testDeleteReservation() {
		List<ReservationDTO> reservationsDTO = resService.findAllReservationsDto();
		int resNum = 0;
		int resId1 = -1, resId2 = -1, resId3 = -1;
		
		for(ReservationDTO dto : reservationsDTO) {
			resNum = dto.getId();
			if(dto.getUsername().equals("reader@reader.lt") &&
			   dto.getBookid() == 19 &&
			  (dto.getReservationDate().isBefore(LocalDateTime.now()) ||
			   dto.getReservationDate().isEqual(LocalDateTime.now())) &&
			  (dto.getReturnDate().isAfter(LocalDate.now()) ||
			   dto.getReturnDate().isEqual(LocalDate.now()))
			  ) {
				resId1 = dto.getId();
			} else
			if(dto.getUsername().equals("reader@reader.lt") &&
			   dto.getBookid() == 20 &&
			  (dto.getReservationDate().isBefore(LocalDateTime.now()) ||
			   dto.getReservationDate().isEqual(LocalDateTime.now())) &&
			  (dto.getReturnDate().isAfter(LocalDate.now()) ||
			   dto.getReturnDate().isEqual(LocalDate.now()))
			  ) {
				resId2 = dto.getId();
			} else
			if(dto.getUsername().equals("admin@admin.lt") &&
			   dto.getBookid() == 6 &&
			  (dto.getReservationDate().isBefore(LocalDateTime.now()) ||
			   dto.getReservationDate().isEqual(LocalDateTime.now())) &&
			  (dto.getReturnDate().isAfter(LocalDate.now()) ||
			   dto.getReturnDate().isEqual(LocalDate.now()))
			  ) {
				resId3 = dto.getId();
			}
		}
		
		final int finResNum = resNum;
		
		NonExistingBookException exception = assertThrows(NonExistingBookException.class, () -> {resService.deleteReservation(finResNum*3);});
		
		assertEquals("Delete: No such reservation to delete with id: "+finResNum*3, exception.getMessage());
		
		assertTrue(resService.deleteReservation(resId1));
		assertTrue(resService.deleteReservation(resId2));
		assertTrue(resService.deleteReservation(resId3));
		
		EmptyInputException exception2 = assertThrows(EmptyInputException.class, () -> {resService.findAllReservationsDto();});
		
		assertEquals("No any reservations", exception2.getMessage());
	}
}

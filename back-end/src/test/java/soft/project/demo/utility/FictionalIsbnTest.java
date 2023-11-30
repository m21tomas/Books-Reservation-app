package soft.project.demo.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import soft.project.demo.model.Book;
import soft.project.demo.service.BookService;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class FictionalIsbnTest {
	
	@Autowired
	private BookService bookService;
	
	@Test
	@Order(1)
	void testCreateFictionalIsbn () {
		List<Book> books = bookService.findAll();
		String isbn = null;
		
		System.out.println();
		System.out.println("Generating fictional ISBN strings: ");
		for(int i = 1; i <= 20; i++) {
			isbn = FictionalIsbn.makeUniqueFictionalIsbn(books, i%7);
			System.out.println(i+". "+isbn);
		}
		System.out.println();
		assertEquals(17, isbn.length());
	}
	
	@Test
	@Order(2)
	void testValidateIsbn () {
		List<Book> books = bookService.findAll();
		String isbn = "978-0-2285-7607-5";
		
		assertTrue(FictionalIsbn.isValidIsbn(isbn, books));
	}
}

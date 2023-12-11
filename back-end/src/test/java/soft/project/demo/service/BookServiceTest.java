package soft.project.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Random;

import org.apache.tika.Tika;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import soft.project.demo.dto.BookRequestDTO;
import soft.project.demo.dto.CategoryDTO;
import soft.project.demo.exception.ExistingBookException;
import soft.project.demo.exception.NonExistingBookException;
import soft.project.demo.model.Book;
import soft.project.demo.model.UserInfo;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class BookServiceTest {
	@Autowired
	private UserService userService;
	
	@Autowired
	private BookService bookService;
	
	@Autowired
	private CategoryService catService;
	
	@Test
	@Order(1)
	void testCreateBook () { 
		CategoryDTO catDto = new CategoryDTO("Cat1");
		CategoryDTO horDto = new CategoryDTO("Cat2");
		
		boolean created = catService.createCategory(catDto);
		boolean crHor = catService.createCategory(horDto);
		
		assertTrue(created);
		assertTrue(crHor);
		
		BookRequestDTO bookDto = new BookRequestDTO();
		bookDto.setTitle("title");
		bookDto.setAuthor("author");
		bookDto.setSummary("summary");
		bookDto.setIsbn("978-1-75182-512-8");
		bookDto.setYear(2000);
		bookDto.setPages(10);
		bookDto.setCirculation(5);
		bookDto.setReservations(0);
		bookDto.setCategory("Cat1");
		BookRequestDTO bookDto2 = new BookRequestDTO();
		bookDto2.setTitle("title2");
		bookDto2.setAuthor("author2");
		bookDto2.setSummary("summary2");
		bookDto2.setIsbn("978-1-75183-512-7");
		bookDto2.setYear(2001);
		bookDto2.setPages(100);
		bookDto2.setCirculation(50);
		bookDto2.setReservations(0);
		bookDto2.setCategory("Cat2");
		BookRequestDTO horBookDto = new BookRequestDTO("The Dark Half", "Stephen King", 
				"Thad Beaumont is an author and recovering alcoholic who lives in the town of Ludlow, Maine. "
				+ "Thad's own books – cerebral literary fiction – are not very successful. "
				+ "However, under the pen name \"George Stark\", he writes highly successful crime novels "
				+ "about a psychopathic killer named Alexis Machine.", "978-1-75184-512-6", 1989, 431, 100, 0,"Horror");
		
		Book createdBook = bookService.addNewBook(bookDto);
		Book createdBook2 = bookService.addNewBook(bookDto2);
		Book createdBook3 = bookService.addNewBook(horBookDto);
		ExistingBookException exception = assertThrows(ExistingBookException.class, () -> {bookService.addNewBook(bookDto2);});
		
		assertEquals("title", createdBook.getTitle());
		assertEquals("title2", createdBook2.getTitle());
		assertEquals("The Dark Half", createdBook3.getTitle());
		assertEquals("Such book already exists. There is the book with the same title and author.", exception.getMessage());
	}
	
	@Test
	@Order(2)
	void testCreateBookWithAnImage() {
		BookRequestDTO bookDto = new BookRequestDTO();
		bookDto.setTitle("New Book With Image");
		bookDto.setAuthor("Tomas Mockaitis");
		bookDto.setSummary("Instructions how to add an image and asociate it with the Book object");
		bookDto.setIsbn("978-1-75185-512-5");
		bookDto.setYear(2023);
		bookDto.setPages(20);
		bookDto.setCirculation(3);
		bookDto.setReservations(0);
		bookDto.setCategory("Cat2");
		
		MultipartFile file = null;
		
		try {
			List<Book> books = bookService.findAll();
			int num = -1;
            for(Book book : books) {
            	if(book.getId() > num) {
            	   num = book.getId();
            	}
            }
		file = createMultipartFileFromURL("https://cdn.pixabay.com/photo/2014/09/05/18/32/old-books-436498_1280.jpg", num+1);
			// file = createMultipartFileFromURL("https://pngimg.com/uploads/pineapple/pineapple_PNG2755.png", num+1);
			// file = createMultipartFileFromURL("https://i.ibb.co/PDj4JKs/Daisy-Romashka-256px-5.gif", num+1);
			// file = createMultipartFileFromURL("https://stsci-opo.org/STScI-01EVST1KQK0K9H9DKHS13XT6F3.tif", num+1);
            // file = createMultipartFileFromURL("https://getsamplefiles.com/download/bmp/sample-5.bmp", num+1);
            //file = createMultipartFileFromURL("https://www.svgrepo.com/show/424894/react-logo-programming.svg", num+1);
			// https://icon-icons.com/download/255100/ICO/512/
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			Book createdBook = bookService.addNewBookWithImage(bookDto, file);
			assertEquals("New Book With Image", createdBook.getTitle());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static MockMultipartFile createMultipartFileFromURL(String imageUrl, int num) throws IOException {
        URL url = new URL(imageUrl);
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        
        try (InputStream inputStream = connection.getInputStream()) {
        	byte[] imageData = inputStream.readAllBytes(); // Read image data into a byte array
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageData);
            Tika tika = new Tika();
            String contentType = tika.detect(byteArrayInputStream);
            String fileExtension = getFileExtension(contentType);
            
            System.out.println("\nBookServiceTest: File size: "+imageData.length);
            System.out.println("BookServiceTest: Content type: "+contentType);
            System.out.println("BookServiceTest: File extension: "+fileExtension+"\n");
            
            

            // Convert image to MockMultipartFile
            return new MockMultipartFile(
                    "Book "+num,
                    "Book "+num+"." + fileExtension,
                    contentType,
                    byteArrayInputStream
            );
        } catch (IOException e) {
            // Handle the exception when there is no internet connection or openStream fails
            e.printStackTrace();
            throw new IOException("Failed to retrieve the image from the URL: " + imageUrl, e);
        }
    }

	private static String getFileExtension(String contentType) {
	    if (contentType.startsWith("image/")) {
	        return contentType.substring("image/".length());
	    } else {
	        return "bin";
	    }
	}

	@Test
	@Order(3)
	void testFindAllBooks () {
		List<Book> books = bookService.findAll();
		
		assertTrue(books.size() > 0);
	}
	
	@Test
	@Order(4)
	void testUpdateBookData() {
		List<Book> books = bookService.findAll();
		
		assertTrue(books.size() > 0);
		
		int bookId = 0;
		for(Book book : books) {
			if(book.getTitle().equals("title2")) {
				bookId = book.getId();
				break;
			}
		}
		
		Book theBook = bookService.findById(bookId);
		
		assertEquals(bookId, theBook.getId());
		
		BookRequestDTO data = new BookRequestDTO();
		data.setAuthor(theBook.getAuthor());
		data.setCategory(theBook.getCategory().getName());
		data.setCirculation(theBook.getCirculation());
		data.setReservations(theBook.getReservationNumber());
		data.setIsbn(theBook.getIsbn());
		data.setPages(theBook.getPages());
		data.setSummary(theBook.getSummary());
		data.setTitle("Another Title");
		data.setYear(theBook.getYear());
		
		boolean status = bookService.updateBookData(data, bookId);
		
		assertTrue(status);
		
		Book updatedBook = bookService.findById(bookId);
		
		assertEquals("Another Title", updatedBook.getTitle());
	}
	
	@Test
	@Order(5)
	void testUpdateBookImage() {
		
		try {
			MultipartFile file = null;
			List<Book> books = bookService.findAll();
			int num = -1;
			int photoBookId = -1;
            for(Book book : books) {
            	if(book.getId() > num) {
            	   num = book.getId();
            	}
            	if(book.getPhoto() != null) {
            		photoBookId = book.getId();
            		System.out.println("FOUND IMAGE BOOK with id: "+book.getId()+" image name: "+book.getPhoto());
            	}
            }
            file = createMultipartFileFromURL("https://cdn.pixabay.com/photo/2012/03/01/00/55/flowers-19830_1280.jpg", num+1);
            assertTrue(bookService.updateBookImage(file, photoBookId));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Order(6)
	void testFavouriteBooks() {
		Random rand = new Random();
		List<UserInfo> users = userService.getAllUsers();
		
		int userId = users.get(rand.nextInt(users.size())).getUserId();
		
		List<Book> allBooks = bookService.findAll();
		
		int randomBookId = allBooks.get(rand.nextInt(allBooks.size())).getId();
		
		UserInfo userInfo = bookService.addBookToFavorites(userId, randomBookId);
		
		assertEquals(userId, userInfo.getUserId());
		
		int bookWithImageId = bookService.findByName("New Book With Image").getId();
		
		userInfo = bookService.addBookToFavorites(userId, bookWithImageId);
		
		int anotherTitleBookId = bookService.findByName("Another Title").getId();
		
		userInfo = bookService.addBookToFavorites(userId, anotherTitleBookId);
		
		for(Book book : userInfo.getFavoriteBooks()) {
			if(book.getId() == randomBookId) {
				assertEquals(randomBookId, book.getId());
			} else
				if(book.getId() == bookWithImageId) {
					assertEquals(bookWithImageId, book.getId());
				} else
					if(book.getId() == anotherTitleBookId) {
						assertEquals(anotherTitleBookId, book.getId());
					}
		}
		
		userInfo = bookService.removeBookFromFavorites(userId, randomBookId);
		
		for(Book book : userInfo.getFavoriteBooks()) {
			assertFalse(book.getId() == randomBookId);
		}
	}
	
	@Test
	@Order(7)
	void testDeleteBooks() {
		List<Book> books = bookService.findAll();
		
		assertTrue(books.size() > 0);
		
		int bookId = 0;
		for(Book book : books) {
			if(book.getTitle().equals("title")) {
				bookId = book.getId();
				break;
			}
		}
		
		Book theBook = bookService.findById(bookId);
		
		System.out.println("BookServiceTest - testDeleteBook: deleting book by title: "+ theBook.getTitle());
		
		assertEquals(bookId, theBook.getId());
		
		boolean delStatus = bookService.deleteBook(bookId);
		
		assertTrue(delStatus);
		
		final int delId = bookId;
		
		NonExistingBookException exception = assertThrows(NonExistingBookException.class, () -> {bookService.deleteBook(delId);});
		
		assertEquals("Update: There is no book which id is: "+delId, exception.getMessage()); 
		
		for(Book book : bookService.findAll()) {
			if(book.getTitle().equals("New Book With Image")) {				
				bookService.deleteBook(book.getId());
			}
			if(book.getTitle().equals("Another Title")) {			
				bookService.deleteBook(book.getId());
			}
			if(book.getTitle().equals("The Dark Half")) {			
				bookService.deleteBook(book.getId());
			}
		}
		
		assertNull(bookService.findByName("New Book With Image"));
		assertNull(bookService.findByName("Another Title"));
		assertNull(bookService.findByName("The Dark Half"));
		
		catService.deleteCategoryByName("Cat1");
		catService.deleteCategoryByName("Cat2");
		
		if(catService.findByName("No Category") != null) {
			catService.deleteCategoryByName("No Category");
			
			assertNull(catService.findByName("No Category"));
		}	
	}
	
}

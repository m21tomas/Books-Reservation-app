package soft.project.demo.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import soft.project.demo.dto.BookRequestDTO;
import soft.project.demo.dto.BookResponseDTO;
import soft.project.demo.exception.ExistingBookException;
import soft.project.demo.exception.NonExistingBookCategoryException;
import soft.project.demo.exception.NonExistingBookException;
import soft.project.demo.exception.NonExistingUserException;
import soft.project.demo.model.Book;
import soft.project.demo.model.Category;
import soft.project.demo.model.User;
import soft.project.demo.model.UserInfo;
import soft.project.demo.repository.BookRepository;
import soft.project.demo.repository.CategoryRepository;
import soft.project.demo.repository.UserRepository;
import soft.project.demo.utility.FictionalIsbn;

@Service
public class BookService {
	@Autowired
	private UserService userService;

	@Autowired
	private CategoryService catService;
	
	@Autowired
	private BookRepository bookRepo;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private CategoryRepository categoryRepo;
	
	@Transactional(readOnly = true)
	public Book findByName (String name) {
		return bookRepo.findByTitle(name).orElse(null);
	}
	
	@Transactional(readOnly = true)
	public Book findById (int id) {
		return bookRepo.findById(id).orElse(null);
	}
	
	@Transactional(readOnly = true)
	public List<Book> findAll() {
		return bookRepo.findAll();
	}
	
	public String getRandomIsbn(int publicantSize) {
		return FictionalIsbn.makeUniqueFictionalIsbn(findAll(), publicantSize);
	}
	
	public String getCloseISBNToThisISBN(String isbn) {
		return FictionalIsbn.getClosePossibleIsbn(isbn, findAll());
	}
	
	@Transactional
	public Book addNewBookWithImage (BookRequestDTO bookDto, MultipartFile file) throws IOException {
		
		Book checkBook = findByName(bookDto.getTitle());
		
		if(checkBook != null) {
			if(checkBook.getTitle().equals(bookDto.getTitle()) && 
					checkBook.getAuthor().equals(bookDto.getAuthor())) {
				throw new ExistingBookException("Such book already exists. There is the book with the same title and author.");
			}
		}
		Long numberOfSavedBytes = 0L;
		Book book = new Book();
		
		book.setTitle(bookDto.getTitle());
		book.setAuthor(bookDto.getAuthor());
		List<Category> categories = catService.findAllCategories();
		boolean categorySet = false;
		for(Category item : categories) {
			if(item.getName().equals(bookDto.getCategory())) {
				book.setCategory(item);
				categorySet = true;
			}
		}
		if(!categorySet) {
			if(bookDto.getCategory() == null)
				throw new NonExistingBookCategoryException("There is no any book category selected!");
			else
				throw new NonExistingBookCategoryException("No such book category like: "+bookDto.getCategory());
		}
		
		if(FictionalIsbn.isValidIsbn(bookDto.getIsbn(), findAll())) {			
			book.setIsbn(bookDto.getIsbn());
		}
		
		book.setYear(bookDto.getYear());
		book.setPages(bookDto.getPages());
		book.setSummary(bookDto.getSummary());
		book.setCirculation(bookDto.getCirculation());
		book.setReservationNumber(bookDto.getReservations());
		
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		
		book.setPhoto(fileName);
		
		String uploadDir = "book-images/";
		
        Path uploadPath = Paths.get(uploadDir);
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        InputStream inputStream = file.getInputStream();
        try {
        	Path filePath = uploadPath.resolve(fileName);
        	numberOfSavedBytes = Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);	    
        } catch (IOException ioe) {        
        	throw new IOException("Could not save image file: " + fileName, ioe);
        } 
        
        inputStream.close();
        if (numberOfSavedBytes > 0)
        return bookRepo.save(book);
        else return null;
	}
	
	@Transactional
	public Book addNewBook (BookRequestDTO bookDto) {
		
		Book checkBook = findByName(bookDto.getTitle());
		
		if(checkBook != null) {
			if(checkBook.getTitle().equals(bookDto.getTitle()) && 
					checkBook.getAuthor().equals(bookDto.getAuthor())) {
				throw new ExistingBookException("Such book already exists. There is the book with the same title and author.");
			}
		}
		
		Book book = new Book();
		
		book.setTitle(bookDto.getTitle());
		book.setAuthor(bookDto.getAuthor());
		
		List<Category> categories = catService.findAllCategories();
		boolean categorySet = false;
		for(Category item : categories) {
			if(item.getName().equals(bookDto.getCategory())) {
				book.setCategory(item);
				categorySet = true;
			}
		}
		if(!categorySet) {
			if(bookDto.getCategory() == null)
				throw new NonExistingBookCategoryException("There is no any book category selected!");
			else
				throw new NonExistingBookCategoryException("No such book category like: "+bookDto.getCategory());
		}
		
		if(FictionalIsbn.isValidIsbn(bookDto.getIsbn(), findAll())) {			
			book.setIsbn(bookDto.getIsbn());
		}
		book.setYear(bookDto.getYear());
		book.setPages(bookDto.getPages());
		book.setSummary(bookDto.getSummary());
		book.setCirculation(bookDto.getCirculation());
		book.setReservationNumber(bookDto.getReservations());
		book.setPhoto(null);
		
        return bookRepo.save(book);
	}
	
	@Transactional(readOnly = true)
	public Page<BookResponseDTO> getAllBooksPage (Pageable pageable) {
		
		Page<Book> books = bookRepo.findAPageOfAll(pageable);
		 
		Page<BookResponseDTO> dtoPage = books.map(new Function<Book, BookResponseDTO>(){

			@Override
			public BookResponseDTO apply(Book t) {
				BookResponseDTO dtoObject = new BookResponseDTO();
				
				dtoObject.setId(t.getId());
				dtoObject.setAuthor(t.getAuthor());
				dtoObject.setCategory(t.getCategory().getName());
				dtoObject.setCirculation(t.getCirculation());
				dtoObject.setIsbn(t.getIsbn());
				dtoObject.setPages(t.getPages());
				dtoObject.setSummary(t.getSummary());
				dtoObject.setTitle(t.getTitle());
				
				InputStream inputStream = null;
				
				if(t.getPhoto() != null) {
					String imageName = t.getPhoto();
					
					try {
						inputStream = new FileInputStream("book-images/"+imageName);
					} catch(FileNotFoundException e) {
						e.printStackTrace();
					}
					
					byte[] imageBytes = null;
					
					try {
						imageBytes = StreamUtils.copyToByteArray(inputStream);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					dtoObject.setImage(imageBytes);
				}
				else {
					dtoObject.setImage(null);
				}
				
				try {
					if(inputStream != null) {
	                   inputStream.close();
	                }
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				return dtoObject;
			}
		});
		return dtoPage;
	}
	
	@Transactional(readOnly = true)
	public Page<BookResponseDTO> getBooksPageFilteredByTitle (String title, Pageable pageable) {
		
		Page<Book> books = bookRepo.findByTitleContainingIgnoreCase(title, pageable);
		 
		Page<BookResponseDTO> dtoPage = books.map(new Function<Book, BookResponseDTO>(){

			@Override
			public BookResponseDTO apply(Book t) {
				BookResponseDTO dtoObject = new BookResponseDTO();
				
				dtoObject.setId(t.getId());
				dtoObject.setAuthor(t.getAuthor());
				dtoObject.setCategory(t.getCategory().getName());
				dtoObject.setCirculation(t.getCirculation());
				dtoObject.setIsbn(t.getIsbn());
				dtoObject.setPages(t.getPages());
				dtoObject.setSummary(t.getSummary());
				dtoObject.setTitle(t.getTitle());
				
				InputStream inputStream = null;
				
				if(t.getPhoto() != null) {
					String imageName = t.getPhoto();
					
					try {
						inputStream = new FileInputStream("book-images/"+imageName);
					} catch(FileNotFoundException e) {
						e.printStackTrace();
					}
					
					byte[] imageBytes = null;
					
					try {
						imageBytes = StreamUtils.copyToByteArray(inputStream);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					dtoObject.setImage(imageBytes);
				}
				else {
					dtoObject.setImage(null);
				}
				
				try {
					if(inputStream != null) {
	                   inputStream.close();
	                }
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				return dtoObject;
			}
		});
		return dtoPage;
	}
	
	@Transactional
	public boolean updateBookData (BookRequestDTO data, Integer id) {
		Book book = findById(id);
		
		if(book == null) {
			throw new NonExistingBookException("Update: There is no book which id is: "+id);
		}
		else {
			if(!book.getAuthor().equals(data.getAuthor())) book.setAuthor(data.getAuthor());
			if(!book.getCategory().getName().equals(data.getCategory())) {
				List<Category> categories = catService.findAllCategories();
				boolean catFound = false;
				for(Category cat : categories) {
					if(cat.getName().equals(data.getCategory())) {
						book.setCategory(cat);
						catFound = true;
						break;
					}
				}
				if(!catFound) {					
					book.setCategory(new Category(data.getCategory()));
				}
			}
			if(!book.getCirculation().equals(data.getCirculation())) book.setCirculation(data.getCirculation());
			if(!book.getReservationNumber().equals(data.getReservations())) book.setReservationNumber(data.getReservations());
			if(!book.getIsbn().equals(data.getIsbn())) {
				List<Book> books = findAll();
				if(FictionalIsbn.isValidIsbn(data.getIsbn(), books))
				book.setIsbn(data.getIsbn());
			}
			if(!book.getPages().equals(data.getPages())) book.setPages(data.getPages());
			if(!book.getSummary().equals(data.getSummary())) book.setSummary(data.getSummary());
			if(!book.getTitle().equals(data.getTitle())) book.setTitle(data.getTitle());
			bookRepo.save(book);
			return true;
		}
	}
	
	@Transactional
	public boolean updateBookImage(MultipartFile file, Integer id) throws IOException {
		
		Book book = findById(id);
		
		if(book != null) {
			
			String uploadDir = "book-images/";
            if(book.getPhoto() != null) {
				
		        Path filePath = Paths.get(uploadDir).resolve(book.getPhoto());
		        
		        try {
		        	System.out.println("DELETING "+filePath);
					Files.delete(filePath);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Book newBook = book;
			Long numberOfSavedBytes = 0L;
			Book savedBook = null;
			
			String fileName = StringUtils.cleanPath(file.getOriginalFilename());
			
			System.out.println("REPLACED WITH FILENAME: "+ fileName);
			newBook.setPhoto(fileName);
	        		
	        Path uploadPath = Paths.get(uploadDir);
	        
	        if (!Files.exists(uploadPath)) {
	            Files.createDirectories(uploadPath);
	        }   
			
	        InputStream inputStream = file.getInputStream();
	        try {
	        	Path filePath = uploadPath.resolve(fileName);
	        	numberOfSavedBytes = Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);	    
	        } catch (IOException ioe) {        
	        	throw new IOException("Could not save image file: " + fileName, ioe);
	        } 
	        
	        inputStream.close();
	        
	        savedBook = bookRepo.save(newBook);
			
			if(savedBook != null && numberOfSavedBytes != 0) return true;
			
			return false;
		}
		else throw new NonExistingBookException("Update: There is no book which id is: "+id);
	}
	
	@Transactional
	public boolean deleteBook(Integer id) {
		Book book = findById(id);
		
		if(book != null) {
			
			if(book.getPhoto() != null) {
				String uploadDir = "book-images/";
				
		        Path filePath = Paths.get(uploadDir).resolve(book.getPhoto());
		        
		        try {
					Files.delete(filePath);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			 // Find users who have the book in their favoriteBooks collection
		    List<User> usersWithBook = userRepo.findByFavoriteBooksContaining(book);

		    // Remove the book from each user's favoriteBooks collection
		    for (User user : usersWithBook) {
		        user.getFavoriteBooks().remove(book);
		        userRepo.save(user); // Make sure to save the user after modifying the collection
		    }
			
			// Get the associated category
			Category category = book.getCategory();
			
			// Remove the book from the set of books in the category
		    category.getBooks().remove(book);
		    book.setCategory(null); // Set the category to null

		    // Save the updated category and delete the book
		    categoryRepo.save(category);		
			bookRepo.delete(book); 
		}
		else throw new NonExistingBookException("Update: There is no book which id is: "+id);
		
		Book checkDeletedbook = findByName(book.getTitle());
		
		if(checkDeletedbook == null) return true;
		else return false;
	}
	
	@Transactional
	public UserInfo addBookToFavorites(int userId, int bookId) {
		User user = userRepo.findById(userId).orElse(null);
		
		if(user == null) {
			throw new NonExistingUserException("No user with id: "+userId);
		}

		Book book = findById(bookId);
		
		if(book == null) {
			throw new NonExistingBookException("No book with id: "+bookId);
		}
		
		user.getFavoriteBooks().add(book);
		
		User updatedUser = userRepo.save(user);
		
		Set<Book> favBooks = updatedUser.getFavoriteBooks();
		
		boolean added = false;
		
		for(Book theBook : favBooks) {
			if(theBook.getId() == bookId) {
				added = true;
				break;
			}
		}
		
		if(!added) {
			throw new NonExistingBookException("The book with id: "+bookId+" was not added to favorites set");
		}
		
		UserInfo userinfo = userService.findUserById(userId, user.getUsername());
		
		return userinfo;
	}
	
	@Transactional
	public UserInfo removeBookFromFavorites(int userId, int bookId) {
		User user = userRepo.findById(userId).orElse(null);
		
		if(user == null) {
			throw new NonExistingUserException("No user with id: "+userId);
		}

		Book book = findById(bookId);
		
		if(book == null) {
			throw new NonExistingBookException("No book with id: "+bookId);
		}
		
		user.getFavoriteBooks().remove(book);
		
		User updatedUser = userRepo.save(user);
		
		Set<Book> favBooks = updatedUser.getFavoriteBooks();
		
		boolean removed = true;
		
		for(Book theBook : favBooks) {
			if(theBook.getId() == bookId) {
				removed = false;
				break;
			}
		}
		
		if(!removed) {
			throw new NonExistingBookException("The book with id: "+bookId+" was not removed from the favorites set");
		}
		
		UserInfo userinfo = userService.findUserById(userId, user.getUsername());
		
		return userinfo;
	}
}

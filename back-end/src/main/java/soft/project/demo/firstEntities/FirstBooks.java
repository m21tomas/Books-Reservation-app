package soft.project.demo.firstEntities;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import soft.project.demo.dto.BookRequestDTO;
import soft.project.demo.model.Book;
import soft.project.demo.repository.BookRepository;
import soft.project.demo.service.BookService;
import soft.project.demo.utility.FictionalIsbn;

@Component
@DependsOn("firstCategories")
public class FirstBooks {
	
	private static final Logger LOG = LoggerFactory.getLogger(FirstBooks.class);
	
	@Autowired
    private BookService bookService;
	
	@Autowired
	private BookRepository bookRepo;
	
	private static String[] regPubCirc = new String[20];

    @PostConstruct
    public void addExampleBooks() {
    	if(bookRepo.findAll().isEmpty()) {
    		
    		LOG.info("There are no any books in repository. Creating example books...");
    		
    		// Example authors
	        String[] authors = {
	            "Harper Lee", "George Orwell", "J.K. Rowling", "Jane Austen", "F. Scott Fitzgerald",
	            "Gabriel Garcia Marquez", "Agatha Christie", "J.R.R. Tolkien", "Mark Twain", "Leo Tolstoy",
	            "Ernest Hemingway", "Herman Melville", "George R.R. Martin", "Arthur Conan Doyle", "Victor Hugo",
	            "Stephen King", "Emily Bronte", "William Faulkner", "Ray Bradbury", "Charles Dickens"
	        };
	
	        // Example book titles corresponding to authors
	        String[] bookTitles = {
	            "To Kill a Mockingbird", "1984", "Harry Potter and the Philosopher's Stone", "Pride and Prejudice", "The Great Gatsby",
	            "One Hundred Years of Solitude", "Murder on the Orient Express", "The Hobbit", "The Adventures of Tom Sawyer", "War and Peace",
	            "The Old Man and the Sea", "Moby-Dick", "A Game of Thrones", "Sherlock Holmes", "Les Miserables",
	            "The Shining", "Wuthering Heights", "The Sound and the Fury", "Fahrenheit 451", "A Tale of Two Cities"
	        };
	
	        // Example book genres corresponding to titles
	        String[] bookGenres = {
	            "Classic", "Dystopian Novel", "Fantasy", "Romance", "Classic",
	            "Magical Realism", "Mystery", "Fantasy", "Adventure", "Historical Fiction",
	            "Classic", "Adventure", "Fantasy", "Mystery", "Classic",
	            "Horror", "Gothic Fiction", "Modernist", "Dystopian Novel", "Classic"
	        };
	        
	        int[] years = {
	                1960, 1949, 1997, 1813, 1925, 1967, 1934, 1937, 1876, 1869,
	                1952, 1851, 1998, 1887, 1862, 1977, 1847, 1929, 1953, 1859
	            };
	        
	        String isbn = "";
	        
	        try {
		        // Adding example books
	        	Random random = new Random();
	        	int circ =0;
	        	List<Book> books = bookService.findAll();
		        for (int i = 0; i < authors.length; i++) {
		        	circ = random.nextInt(20 - 1 + 1) + 1;
		            BookRequestDTO bookDTO = new BookRequestDTO();
		            bookDTO.setTitle(bookTitles[i]);
		            bookDTO.setAuthor(authors[i]);
		            bookDTO.setSummary("Summary for " + bookTitles[i]);
		            isbn = FictionalIsbn.makeUniqueFictionalIsbn(books, random.nextInt(5 - 3 + 1) + 3); //(100 + i * 5);
		            bookDTO.setIsbn(isbn);
		            bookDTO.setYear(years[i]);
		            bookDTO.setPages(300 + i * 10);
		            bookDTO.setCirculation(circ); //(100 + i * 5);
		            bookDTO.setReservations(0);
		            bookDTO.setCategory(bookGenres[i]);
		
		            // Adding books to the system
		            bookService.addNewBook(bookDTO);
		        }
		        
		        LOG.info("New books created to the books repository");
	        } catch (RuntimeException e){
	        	// Handle the exception (e.g., log an error message)
	            System.err.println("Error generating unique ISBN: " + e.getMessage());
	            LOG.error("\u001B[31mNew books not created and not saved to the repository. Or maybe only part of it created and saved\u001B[0m");

	        }
	        
	    }
    }
    
    @SuppressWarnings("unused")
	private static String makeUniqueFictionalIsbn(int circ) {
    	Random random = new Random();
    	StringBuilder isbnBuilder = new StringBuilder();
    	String isbn = "";
    	String reg_pubPart = "";
    	int prefix = 978;
    	int regGroup = 1;
    	int j = 0, registrant = 0;
    	int publication = 0;
    	int checkDigit = 0;
    	int digit = 0;
    	char digitChar = '\u0000';
    	int product = 0;
    	int minRange = 1;
        int regRange = 99999, pubRange = 99999;
        int checkReg = 0, checkPub = 0, checkCirc = 0;
        int circLength = 0;
        boolean loop = false;
        int maxIterations = 50000;  // Set a reasonable maximum number of iterations
        int iterations = 0;  // Counter to track the number of iterations
        
        do {
        	loop = false;
        	regRange = 99999;
	        registrant = random.nextInt(regRange - minRange + 1) + minRange;
	        
	        pubRange = 999;
	        publication = random.nextInt(pubRange - minRange + 1) + minRange;
	        
	        for(j = 0; j < regPubCirc.length; j++) {
	        	if(regPubCirc[j] == null) break;
	        	checkReg = Integer.parseInt(regPubCirc[j].substring(0, 5));
	        	checkPub = Integer.parseInt(regPubCirc[j].substring(6, 9));
	        	circLength = String.valueOf(regPubCirc[j]).length()-10;
	        	checkCirc = Integer.parseInt(regPubCirc[j].substring(10, 10 + circLength));
	        	
	        	if(registrant == checkReg && 
	        	   (publication <= checkPub && 
	        		publication+circ >= checkPub ||
	        		publication >= checkPub &&
	        		publication <= checkPub+checkCirc)
	        	  ) {
	        		loop = true;
	        		break;
	        	} else
	        	if(registrant < checkReg && 
	 	           (registrant + (publication + circ) / 1000 > checkReg ||
	 	            registrant + (publication + circ) / 1000 == checkReg &&
	 	            (publication + circ) % 1000 >= checkPub)
	 	          ) {
	 	        	loop = true;
	 	        	break;
	 	        } else
	 	        if(registrant > checkReg && 
	 		 	   (checkReg + (checkPub + checkCirc) / 1000 > registrant ||
	 		 		checkReg + (checkPub + checkCirc) / 1000 == registrant &&
	 		 	    (checkPub + checkCirc) % 1000 >= publication)
	 		 	  ) {
	 		 	    loop = true;
	 		 	    break;
	 		 	}
	        }
	        
	        iterations++;

	        if (iterations > maxIterations) {
	            // Handle the case where the loop exceeds the maximum number of iterations
	        	String str1 = "Exceeded maximum iterations. Adjust parameters to avoid infinite loop.";
	        	String str2 = "\nregRange = "+String.valueOf(regRange)+" is too low value for all the books you try to find isbn";
	        	String str3 = "\npubRange = "+String.valueOf(pubRange)+" is also too low value for all the books you try to find isbn";
	        	String str4 = "\ncirc = "+String.valueOf(circ)+" is clearly to high value for all the books you try to find isbn";
	        	String str = str1+str2+str3+str4;
	            throw new RuntimeException(str);
	        }
	        
        }while(loop);
        
        reg_pubPart = String.format("%05d", registrant)+'-'+String.format("%03d", publication)+'-'+String.valueOf(circ);
        
        regPubCirc[j] = reg_pubPart;
        
        if(j == 19) {
        	for(j = 0; j < regPubCirc.length; j++) {
        		System.out.println(j+1+". "+regPubCirc[j]);
        	}
        }
        
        isbnBuilder.append(prefix)
        .append(regGroup)
        .append(String.format("%05d", registrant))
        .append(String.format("%03d", publication));
        
        for (int i = 0; i < isbnBuilder.length(); i++) {
            digitChar = isbnBuilder.charAt(i);

            digit = Character.getNumericValue(digitChar);

            product = product + digit * (i % 2 == 0 ? 1 : 3);
        }
        
        if(product%10 == 0) checkDigit = 0;
        else checkDigit = 10 - product%10;
        
        isbnBuilder.setLength(0);
        
        isbnBuilder.append(prefix).append("-")
        .append(regGroup).append("-")
        .append(String.format("%05d", registrant)).append("-")
        .append(String.format("%03d", publication)).append("-")
        .append(checkDigit);
        
        isbn = isbnBuilder.toString();
        
    	return isbn;
    }
}

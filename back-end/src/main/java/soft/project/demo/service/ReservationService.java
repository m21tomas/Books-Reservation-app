package soft.project.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import soft.project.demo.dto.ReservationDTO;
import soft.project.demo.enums.ReservationStatus;
import soft.project.demo.exception.EmptyInputException;
import soft.project.demo.exception.ExistingBookException;
import soft.project.demo.exception.ExistingBookReservationException;
import soft.project.demo.exception.NonExistingBookException;
import soft.project.demo.exception.NonExistingUserException;
import soft.project.demo.model.Book;
import soft.project.demo.model.Reservation;
import soft.project.demo.model.User;
import soft.project.demo.repository.BookRepository;
import soft.project.demo.repository.ReservationRepository;
import soft.project.demo.repository.UserRepository;

@Service
public class ReservationService {
	@Autowired
	private ReservationRepository reserRepo;
	
	@Autowired
	private BookRepository bookRepo;
	
	@Autowired
	private UserRepository userRepo;
	
	@Transactional(readOnly = true)
	public Reservation findById (int id) {
		return reserRepo.findById(id).orElse(null);
	}
	
	@Transactional(readOnly = true)
	public ReservationDTO findByIdDto (int id) {
		Reservation res = reserRepo.findById(id).orElse(null);
		
		if(res != null)
			return reservationToDTO(res);
		else throw new EmptyInputException("No reservation which id: "+ id);
	}
	
	@Transactional(readOnly = true)
	public List<ReservationDTO> findByBookTitle (String title) {
		List<Reservation> list = reserRepo.findByBookTitle(title).orElse(Collections.emptyList());
		
		if(!list.isEmpty()) {
			return list.stream()
		            .map(this::reservationToDTO)
		            .collect(Collectors.toList());
		}
		else {
			throw new EmptyInputException("No reservations by the provided book title: "+ title);
		}
	}
	
	@Transactional(readOnly = true)
	public List<ReservationDTO> findByUser (String username) {
		List<Reservation> list = reserRepo.findByUser(username).orElse(Collections.emptyList());
		
		if(!list.isEmpty()) {
			return list.stream()
		            .map(this::reservationToDTO)
		            .collect(Collectors.toList());
		}
		else {
			throw new EmptyInputException("User "+username+" has no reservations");
		}
	}
	
	@Transactional(readOnly = true)
	public List<Reservation> findAllReservations(){
		return reserRepo.findAll();
	}
	
	@Transactional(readOnly = true)
	public List<ReservationDTO> findAllReservationsDto(){
		List<Reservation> list = reserRepo.findAll();
		
		if(!list.isEmpty()) {
			return list.stream()
		            .map(this::reservationToDTO)
		            .collect(Collectors.toList());
		}
		else {
			throw new EmptyInputException("No any reservations");
		}
	}
	
	@Transactional(readOnly = true)
	public Page<ReservationDTO> getAllReservationsPage(Pageable pageable) {
	    Page<Reservation> reservations = reserRepo.findAPageOfAll(pageable);

	    return reservations.map(res -> {
	        ReservationDTO dtoObj = new ReservationDTO();

	        dtoObj.setId(res.getId());
	        dtoObj.setUsername(res.getUser().getUsername());
	        dtoObj.setBookReservationISBN(res.getBookEditionISBN());
	        dtoObj.setBookid(res.getBook().getId());
	        dtoObj.setBookTitle(res.getBook().getTitle());
	        dtoObj.setBookAuthor(res.getBook().getAuthor());
	        dtoObj.setReservationDate(res.getReservationDate());
	        dtoObj.setReturnDate(res.getReturnDate());
	        dtoObj.setStatus(res.getStatus());

	        return dtoObj;
	    });
	}

	private ReservationDTO reservationToDTO (Reservation obj) {
		ReservationDTO dtoObj = new ReservationDTO();
		
		dtoObj.setId(obj.getId());
		dtoObj.setUsername(obj.getUser().getUsername());
		dtoObj.setBookReservationISBN(obj.getBookEditionISBN());
		dtoObj.setBookid(obj.getBook().getId());
		dtoObj.setBookTitle(obj.getBook().getTitle());
		dtoObj.setBookAuthor(obj.getBook().getAuthor());
		dtoObj.setReservationDate(obj.getReservationDate());
		dtoObj.setReturnDate(obj.getReturnDate());
		dtoObj.setStatus(obj.getStatus());
		
		return dtoObj;
	}
	
	@SuppressWarnings("unused")
	private static String makeEditionFictionalIsbn(String bookISBN, int editionNum) {
    	StringBuilder isbnBuilder = new StringBuilder();
    	
    	String cleanedIsbn = bookISBN.replace("-", "");
    	
    	// Parse components to integers
        int prefix = Integer.parseInt(cleanedIsbn.substring(0, 3));
        int registrationGroup = Integer.parseInt(cleanedIsbn.substring(3, 4));
        int registrant = Integer.parseInt(cleanedIsbn.substring(4, 9));
        int publication = Integer.parseInt(cleanedIsbn.substring(9, 12));
        int checkDigit = Integer.parseInt(cleanedIsbn.substring(12));
        int digit = 0;
        char digitChar = '\u0000';
    	int product = 0;
        
        if(publication + editionNum > 999) {
        	registrant = registrant + (publication + editionNum) / 1000;
        	publication = (publication + editionNum) % 1000;
        }
        else {
        	publication = publication + editionNum;
        }
        
        isbnBuilder.append(prefix)
        .append(registrationGroup)
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
        .append(registrationGroup).append("-")
        .append(String.format("%05d", registrant)).append("-")
        .append(String.format("%03d", publication)).append("-")
        .append(checkDigit);
    	
    	return isbnBuilder.toString();
    }
	
	@Transactional
	public Reservation addReservation (String username, int bookId, int daysToReserve) {
		User user = userRepo.findByUsername(username).orElse(null);
		int editionNum = 0;
		
		if(user == null) {
			throw new NonExistingUserException("No such username "+username+" to make reservation");
		}
		
		Book book = bookRepo.findById(bookId).orElse(null);
		
		if(book == null) {
			throw new NonExistingBookException("No such book whic id is "+ Integer.toString(bookId) + " to make reservation");
		}
		
		if(daysToReserve <= 0) {
			throw new EmptyInputException("There should be positive integer value of the days the book is reserved. But it was entered: "+ Integer.toString(daysToReserve));
		}
		
		// Retrieve all reservations for the user
	    List<Reservation> userReservations = reserRepo.findByUser(username).orElse(Collections.emptyList());

	    // Check if the user has already reserved the same book in the given time interval
	    for (Reservation existingReservation : userReservations) {
	        // Check if the existing reservation is for the same book
	        if (existingReservation.getBook().getId().equals(bookId)) {
	            LocalDateTime existingDateTime = existingReservation.getReservationDate();
	            LocalDate existingReturnDate = existingReservation.getReturnDate();

	            LocalDateTime newDateTime = LocalDateTime.now();
	            LocalDate newReturnDate = LocalDate.now().plusDays(daysToReserve);
	            /*
	            if ((existingDateTime.isBefore(newDateTime) &&
	            		newDateTime.isBefore(existingReturnDate.atStartOfDay())) ||
	            	(newDateTime.isBefore(existingDateTime) &&
	            		existingDateTime.isBefore(newReturnDate.atStartOfDay()))
	               ){
	            	 throw new ExistingBookReservationException("User " + username + " has already reserved the same book in the given time interval");
	            }
	            */
	            if ((existingDateTime.isBefore(newDateTime) || existingDateTime.isEqual(newDateTime)) &&
	                    (newDateTime.isBefore(existingReturnDate.atStartOfDay()) || newDateTime.isEqual(existingReturnDate.atStartOfDay())) ||
	                (newDateTime.isBefore(existingDateTime) || newDateTime.isEqual(existingDateTime)) &&
	                    (existingDateTime.isBefore(newReturnDate.atStartOfDay()) || existingDateTime.isEqual(newReturnDate.atStartOfDay()))) {
	            	throw new ExistingBookReservationException("User " + username + " has already reserved the same book in the given time interval");
	            }
	        }
	    }
		
		if(book.getReservationNumber() >= book.getCirculation()) {
			
			List<Reservation> resList = findAllReservations();	
			
			int minDays  = Integer.MAX_VALUE;
			
			for(Reservation res : resList) {
		        LocalDate returnDate = res.getReturnDate();
		        LocalDateTime currentDateTime = LocalDateTime.now();

		        long daysUntilReturn = ChronoUnit.DAYS.between(currentDateTime.toLocalDate(), returnDate);

		        if(daysUntilReturn < minDays) {
		            minDays = (int) daysUntilReturn;
		        }
		    }
			
			if (minDays == 0) {
		        throw new ExistingBookException("All available books reserved. But today one copy of it should be available. Check back anytime later");
		    } else {
		        throw new ExistingBookException("All available books reserved. The closest available book will be in " + minDays + " days");
		    }
		}
		else {
			editionNum = book.getReservationNumber()+1;
			book.setReservationNumber(editionNum);
		}
		
		Reservation res = new Reservation(daysToReserve);
		
		res.setBookEditionISBN(book.getIsbn()+' '+editionNum);
		
		res.setStatus(ReservationStatus.PENDING);
		
		res.setBook(book);
		
		res.setUser(user);
		
		return reserRepo.save(res);
	}
	
	@Transactional
	public ReservationDTO changeReservationStatus (Integer id, String status) {
		Reservation res = findById(id);
		
		if(res == null) {
			throw new NonExistingBookException("Change Status: No such reservation with id: "+id);
		}
		
		ReservationStatus checkStatus = null;
	    for (ReservationStatus reservationStatus : ReservationStatus.values()) {
	        if (reservationStatus.getStatus().equalsIgnoreCase(status)) {
	        	checkStatus = reservationStatus;
	            break;
	        }
	    }
	    
	    if (checkStatus == null) {
	        throw new IllegalArgumentException("Change Status: Invalid reservation status: " + status);
	    }
	    
	    int num = 0;
	    if((res.getStatus() == ReservationStatus.RESERVED ||
	        res.getStatus() == ReservationStatus.PENDING)	
	       &&
	       (checkStatus == ReservationStatus.RETURNED ||
	    	checkStatus == ReservationStatus.REJECTED)) 
	    {
	        num = res.getBook().getReservationNumber()-1;
	        if(num < 0) num = 0;
	    	res.getBook().setReservationNumber(num);
	    } 
	    else
	    if((res.getStatus() != ReservationStatus.PENDING &&
	        res.getStatus() != ReservationStatus.RESERVED)
	       && 
	       (checkStatus == ReservationStatus.PENDING ||
	        checkStatus == ReservationStatus.RESERVED)) 
	    {
	    	num = res.getBook().getReservationNumber()+1;
	        if(num > res.getBook().getCirculation()) {
	        	List<Reservation> resList = findAllReservations();
	        	
	        	int minDays  = Integer.MAX_VALUE;
				
				for(Reservation reser : resList) {
			        LocalDate returnDate = reser.getReturnDate();
			        LocalDateTime currentDateTime = LocalDateTime.now();

			        long daysUntilReturn = ChronoUnit.DAYS.between(currentDateTime.toLocalDate(), returnDate);

			        if(daysUntilReturn < minDays) {
			            minDays = (int) daysUntilReturn;
			        }
			    }
				
				if (minDays == 0) {
			        throw new ExistingBookException("All available books reserved. But today one copy of it should be available. Check back anytime later");
			    } else {
			        throw new ExistingBookException("All available books reserved. The closest available book will be in " + minDays + " days");
			    }
	        } 
	        else {
	        	res.getBook().setReservationNumber(num);
	        }
	    }
	    
	    res.setStatus(checkStatus);

	    Reservation updatedReservation = reserRepo.save(res);
		
	    return reservationToDTO(updatedReservation);
	}
	
	@Transactional
	public ReservationDTO updateReservationData (ReservationDTO obj, Integer id) {
		Reservation res = findById(id);
		
		String newAuthorDto = "";
		
		if(res == null) {
			throw new NonExistingBookException("Update: No such reservation with id: "+id);
		} else {
			if(!res.getId().equals(obj.getId())) res.setId(obj.getId());
			if(!res.getUser().getUsername().equals(obj.getUsername())) {
				User otherUser = userRepo.findByUsername(obj.getUsername()).orElse(null);
				if(otherUser != null) {
					res.setUser(otherUser);
				}
			}
			if(!res.getBookEditionISBN().equals(obj.getBookReservationISBN())) res.setBookEditionISBN(obj.getBookReservationISBN());
			if(!res.getBook().getId().equals(obj.getBookid()) || !res.getBook().getTitle().equals(obj.getBookTitle())) {
				if(!res.getBook().getId().equals(obj.getBookid())) {
					Book otherBook = bookRepo.findById(obj.getBookid()).orElse(null);
					if(otherBook != null) {
						res.setBook(otherBook);
					}
				}
				else if (!res.getBook().getTitle().equals(obj.getBookTitle())) {
					Book anotherBook = bookRepo.findByTitle(obj.getBookTitle()).orElse(null);
					if(anotherBook != null) {
						res.setBook(anotherBook);
					}
				}
			}
			if(!res.getBook().getAuthor().equals(obj.getBookAuthor())) {
				newAuthorDto = obj.getBookAuthor();
			}
			if(!res.getReservationDate().equals(obj.getReservationDate())) res.setReservationDate(obj.getReservationDate());
			if(!res.getReturnDate().equals(obj.getReturnDate())) res.setReturnDate(obj.getReturnDate());
			if(!res.getStatus().equals(obj.getStatus())) res.setStatus(obj.getStatus());
			
			Reservation updatedRes = reserRepo.save(res);
			
			ReservationDTO updtadedResDto = reservationToDTO(updatedRes);
			
			updtadedResDto.setBookAuthor(newAuthorDto+" ("+updatedRes.getBook().getAuthor()+")");
			
			return updtadedResDto;
		}
	}
	
	@Transactional
	public boolean deleteReservation(Integer id) {
		Reservation res = reserRepo.findById(id).orElse(null);
		
		if(res == null) {
			throw new NonExistingBookException("Delete: No such reservation to delete with id: "+id);
		}
		else {
			reserRepo.delete(res);
		}
		
		return true;
	}
}

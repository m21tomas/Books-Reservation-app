package soft.project.demo.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import soft.project.demo.enums.ReservationStatus;

public class ReservationDTO {
	private Integer id;
	private String username;
	private String bookReservationISBN;
	private Integer bookid;
	private String bookTitle;
	private String bookAuthor;
	private LocalDateTime reservationDate;
	private LocalDate returnDate;
	@Enumerated(EnumType.STRING)
	private ReservationStatus status;
	
	public ReservationDTO () {}

	public ReservationDTO(Integer id, String username, String bookReservationISBN, Integer bookid, String bookTitle,
			String bookAuthor, LocalDateTime reservationDate, LocalDate returnDate, ReservationStatus status) {
		super();
		this.id = id;
		this.username = username;
		this.bookReservationISBN = bookReservationISBN;
		this.bookid = bookid;
		this.bookTitle = bookTitle;
		this.bookAuthor = bookAuthor;
		this.reservationDate = reservationDate;
		this.returnDate = returnDate;
		this.status = status;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getBookReservationISBN() {
		return bookReservationISBN;
	}

	public void setBookReservationISBN(String bookReservationISBN) {
		this.bookReservationISBN = bookReservationISBN;
	}

	public Integer getBookid() {
		return bookid;
	}

	public void setBookid(Integer bookid) {
		this.bookid = bookid;
	}

	public String getBookTitle() {
		return bookTitle;
	}

	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}

	public String getBookAuthor() {
		return bookAuthor;
	}

	public void setBookAuthor(String bookAuthor) {
		this.bookAuthor = bookAuthor;
	}

	public LocalDateTime getReservationDate() {
		return reservationDate;
	}

	public void setReservationDate(LocalDateTime reservationDate) {
		this.reservationDate = reservationDate;
	}

	public LocalDate getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(LocalDate returnDate) {
		this.returnDate = returnDate;
	}

	public ReservationStatus getStatus() {
		return status;
	}

	public void setStatus(ReservationStatus status) {
		this.status = status;
	}
	
}

package soft.project.demo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import soft.project.demo.enums.ReservationStatus;

@Entity
public class Reservation {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	@Column(name = "edition_isbn")
	private String bookEditionISBN;
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE},fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "reservation_date")
    private LocalDateTime reservationDate;

    @Column(name = "return_date")
    private LocalDate returnDate;
    
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;
    
    public Reservation() {
    	this.reservationDate = LocalDateTime.now();
    }

    public Reservation(int reservedDays) {
        this.reservationDate = LocalDateTime.now();
        this.returnDate = reservationDate.plusDays(reservedDays).toLocalDate();
    }

	public Integer getId() {
		return id;
	}
	
	public String getBookEditionISBN() {
		return bookEditionISBN;
	}

	public void setBookEditionISBN(String bookEditionISBN) {
		this.bookEditionISBN = bookEditionISBN;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	@Override
	public int hashCode() {
		return Objects.hash(book, bookEditionISBN, id, reservationDate, returnDate, status, user);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Reservation other = (Reservation) obj;
		return Objects.equals(book, other.book) && Objects.equals(bookEditionISBN, other.bookEditionISBN)
				&& Objects.equals(id, other.id) && Objects.equals(reservationDate, other.reservationDate)
				&& Objects.equals(returnDate, other.returnDate) && status == other.status
				&& Objects.equals(user, other.user);
	}
	
}

package soft.project.demo.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints= {@UniqueConstraint(columnNames="title")}, name = "Books")
public class Book {	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(nullable=false)
	private String title;
	@Column(nullable=false)
	private String author;
	@Column(nullable=false, length = 2000)
	private String summary;
	@Column(nullable=false)
	private String isbn;
	@Column(nullable=false)
	private Integer year;
	
	private String photo;
	@Column(nullable=false)
	private Integer pages;
	@Column(nullable=false)
	private Integer circulation;
	
	@Column(name = "reservation_number")
	private int reservationNumber;
	
	@ManyToOne (optional = false)
	//@JoinColumn(name = "category_id", nullable = true)
	private Category category;
	
	public Book() {}

	public Book(String title, String author, String summary, String isbn, Integer year, String photo, Integer pages,
			Category category, Integer circulation) {
		super();
		this.title = title;
		this.author = author;
		this.summary = summary;
		this.isbn = isbn;
		this.year = year;
		this.photo = photo;
		this.pages = pages;
		this.category = category;
		this.circulation = circulation;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public Integer getPages() {
		return pages;
	}

	public void setPages(Integer pages) {
		this.pages = pages;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Integer getCirculation() {
		return circulation;
	}

	public void setCirculation(Integer circulation) {
		this.circulation = circulation;
	}

	public int getReservationNumber() {
		return reservationNumber;
	}

	public void setReservationNumber(int reservationNumber) {
		this.reservationNumber = reservationNumber;
	}

	@Override
	public int hashCode() {
		return Objects.hash(author, summary, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Book other = (Book) obj;
		return Objects.equals(author, other.author) && Objects.equals(summary, other.summary)
				&& Objects.equals(title, other.title);
	}

	@Override
	public String toString() {
		return "Book [id=" + id + ", title=" + title + ", author=" + author + ", summary=" + summary + ", isbn=" + isbn
				+ ", photo=" + photo + ", pages=" + pages + ", category=" + category + ", circulation=" + circulation +"]";
	}

}

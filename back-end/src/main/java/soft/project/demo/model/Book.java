package soft.project.demo.model;

import org.springframework.lang.NonNull;

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
	@NonNull
	private String title;
	@NonNull
	private String author;
	@NonNull
	private String summary;
	@NonNull
	private Integer isbn;
	@NonNull
	private String photo;
	@NonNull
	private Integer pages;
	
	@ManyToOne(optional = false)
	private Category category;
	
	public Book() {}

	public Book(String title, String author, String summary, Integer isbn, String photo, Integer pages,
			Category category) {
		super();
		this.title = title;
		this.author = author;
		this.summary = summary;
		this.isbn = isbn;
		this.photo = photo;
		this.pages = pages;
		this.category = category;
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

	public Integer getIsbn() {
		return isbn;
	}

	public void setIsbn(Integer isbn) {
		this.isbn = isbn;
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

	@Override
	public String toString() {
		return "Book [id=" + id + ", title=" + title + ", author=" + author + ", summary=" + summary + ", isbn=" + isbn
				+ ", photo=" + photo + ", pages=" + pages + ", category=" + category + "]";
	}

}

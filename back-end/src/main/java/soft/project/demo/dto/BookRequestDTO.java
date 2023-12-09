package soft.project.demo.dto;

public class BookRequestDTO {
	
	private String title;
	private String author;
	private String summary;
	private String isbn;
	private Integer year;
	private Integer pages;
	private Integer circulation;
	private Integer reservations;
	private String category;
	
	public BookRequestDTO() {}
	
	public BookRequestDTO(String title, String author, String summary, String isbn, Integer year, Integer pages, Integer circulation,
			Integer reservations, String category) {
		super();
		this.title = title;
		this.author = author;
		this.summary = summary;
		this.isbn = isbn;
		this.year = year;
		this.pages = pages;
		this.circulation = circulation;
		this.reservations = reservations;
		this.category = category;
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

	public Integer getPages() {
		return pages;
	}

	public void setPages(Integer pages) {
		this.pages = pages;
	}

	public Integer getCirculation() {
		return circulation;
	}

	public void setCirculation(Integer circulation) {
		this.circulation = circulation;
	}
	
	public Integer getReservations() {
		return reservations;
	}

	public void setReservations(Integer reservations) {
		this.reservations = reservations;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
}

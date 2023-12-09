package soft.project.demo.dto;

public class BookResponseDTO {
	
	private Integer id;
	private String title;
	private String author;
	private String summary;
	private String isbn;
	private Integer year;
	private Integer pages;
	private byte[] image;
	private Integer circulation;
	private String category;
	private Integer reservations;
	
	public BookResponseDTO() {}

	public BookResponseDTO(Integer id, String title, String author, String summary, String isbn, Integer year, Integer pages,
			byte[] image, Integer circulation, String category, Integer reservations) {
		super();
		this.id = id;
		this.title = title;
		this.author = author;
		this.summary = summary;
		this.isbn = isbn;
		this.year = year;
		this.pages = pages;
		this.image = image;
		this.circulation = circulation;
		this.category = category;
		this.reservations = reservations;
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

	public Integer getPages() {
		return pages;
	}

	public void setPages(Integer pages) {
		this.pages = pages;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public Integer getCirculation() {
		return circulation;
	}

	public void setCirculation(Integer circulation) {
		this.circulation = circulation;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Integer getReservations() {
		return reservations;
	}

	public void setReservations(Integer reservations) {
		this.reservations = reservations;
	}
}

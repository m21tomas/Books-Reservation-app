package soft.project.demo.model;

import java.util.List;
import java.util.Set;

public class UserInfo {
	private Integer userId;
	private List<String> roles;
	private String username;
	private String password;
	private String email;
	private List<Reservation> reservations;
	private Set<Book> favoriteBooks;
	
	public UserInfo() {}
	
	public UserInfo(Integer userId, List<String> roles, String username, String password, String email) {
		super();
		this.userId = userId;
		this.roles = roles;
		this.username = username;
		this.password = password;
		this.email = email;
	}

	public UserInfo(Integer userId, List<String> roles, String username, String email) {
		super();
		this.userId = userId;
		this.roles = roles;
		this.username = username;
		this.email = email;
	}
	
	public UserInfo(Integer userId, List<String> roles, String username, String email, 
			        List<Reservation> reservations, Set<Book> favoriteBooks) {
		super();
		this.userId = userId;
		this.roles = roles;
		this.username = username;
		this.email = email;
		this.reservations = reservations;
		this.favoriteBooks = favoriteBooks;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Reservation> getReservations() {
		return reservations;
	}

	public void setReservations(List<Reservation> reservations) {
		this.reservations = reservations;
	}

	public Set<Book> getFavoriteBooks() {
		return favoriteBooks;
	}

	public void setFavoriteBooks(Set<Book> favoriteBooks) {
		this.favoriteBooks = favoriteBooks;
	}
	
}

package soft.project.demo.dto;

import java.util.List;

public class UserDTO {
	
	private List<String> authorities;
	private String email;
	private String username;
	private String password;
	
	public UserDTO() {

	}

	public UserDTO(List<String> authorities, String email, String username, String password) {
		super();
		this.authorities = authorities;
		this.email = email;
		this.username = username;
		this.password = password;
	}

	public List<String> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(List<String> authorities) {
		this.authorities = authorities;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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
}

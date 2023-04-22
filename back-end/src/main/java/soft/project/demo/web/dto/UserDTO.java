package soft.project.demo.web.dto;

import java.util.List;

public class UserDTO {
	
	private List<String> roles;
	private String email;
	private String username;
	private String password;
	
	public UserDTO() {

	}

	public UserDTO(List<String> roles, String email, String username, String password) {
		super();
		this.roles = roles;
		this.email = email;
		this.username = username;
		this.password = password;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
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

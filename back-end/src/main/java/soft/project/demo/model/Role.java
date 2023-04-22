package soft.project.demo.model;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Role {
	ADMIN("Administrator"), USER("User");
	
	private String role;

	Role(String role) {
		this.role = role;
	}
	
	public String getRole() {
		return role;
	}
}

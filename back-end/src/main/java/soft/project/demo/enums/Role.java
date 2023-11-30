package soft.project.demo.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Role {
	ADMIN("Administrator"), READER("Reader");
	
	private String role;

	Role(String role) {
		this.role = role;
	}
	
	public String getRole() {
		return role;
	}
}

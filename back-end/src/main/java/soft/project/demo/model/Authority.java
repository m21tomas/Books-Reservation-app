package soft.project.demo.model;



import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Authority implements GrantedAuthority {
	private static final long serialVersionUID = 6354204209245772733L;
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Enumerated(EnumType.STRING)
	private Role authority;
	@JsonIgnore
	@ManyToOne(optional = false)
	private User user;
	
	public Authority() {}
	
	public Authority(Role authority) {
		this.authority = authority;
	}

	@Override
	public String getAuthority() {
		return authority.getRole();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setAuthority(Role authority) {
		this.authority = authority;
	}
	
}

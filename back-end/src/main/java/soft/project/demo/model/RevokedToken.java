package soft.project.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "revoked_tokens")
public class RevokedToken {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "token")
    private String token;

    @Column(name = "revocation_date")
    private LocalDateTime revocationDate;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;
    
    public RevokedToken() {
    }

	public RevokedToken(String token, LocalDateTime revocationDate, LocalDateTime expirationDate) {
		super();
		this.token = token;
		this.revocationDate = revocationDate;
		this.expirationDate = expirationDate;
	}

	public Integer getId() {
		return id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public LocalDateTime getRevocationDate() {
		return revocationDate;
	}

	public void setRevocationDate(LocalDateTime revocationDate) {
		this.revocationDate = revocationDate;
	}

	public LocalDateTime getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(LocalDateTime expirationDate) {
		this.expirationDate = expirationDate;
	}

}

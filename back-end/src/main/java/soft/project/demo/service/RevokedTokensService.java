package soft.project.demo.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import soft.project.demo.exception.NonExistingUserException;
import soft.project.demo.model.RevokedToken;
import soft.project.demo.model.User;
import soft.project.demo.repository.RevokedTokenRepository;
import soft.project.demo.utility.JwtUtility;

@Service
public class RevokedTokensService {
	@Autowired
    private RevokedTokenRepository rTokensRepo;
	
	@Autowired
	private UserService userServ;
	
	@Autowired
	private JwtUtility jwtUtil;
	
	@Transactional(readOnly = true)
	public List<RevokedToken> getRevokedTokensList(){
		return rTokensRepo.findAll();
	}
	
	@Transactional(readOnly = true)
	public RevokedToken getRevokedTokenById(Integer id) {
		return rTokensRepo.findById(id).orElse(null);
	}
	
	@Transactional(readOnly = true)
	public RevokedToken getRevokedTokenByString(String token) {
		return rTokensRepo.findByToken(token);
	}
	
	@Transactional(readOnly = true)
	public Page<RevokedToken> getRevokedTokenPage(Pageable pageable){
		return rTokensRepo.findAll(pageable);
	}
	
	@Transactional
	public RevokedToken createRevokedToken(String token, User user) {
		User providedPrincipal = userServ.findByUsername(user.getUsername());
		
		if(providedPrincipal == null) {
			throw new NonExistingUserException("No such principal username by the provided user");
		}
		
		if(rTokensRepo.existsByToken(token)) {
			throw new IllegalArgumentException("The token is already revoked");
		}
		
		if(jwtUtil.validateToken(token, user)) {
			RevokedToken revokedToken = new RevokedToken();
	        revokedToken.setToken(token);
	        revokedToken.setRevocationDate(LocalDateTime.now());
	        Date tokenExpireDate = jwtUtil.getExpirationDateFromToken(token);
	        revokedToken.setExpirationDate(tokenExpireDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
	        return rTokensRepo.save(revokedToken);
		}
		else {
			throw new IllegalArgumentException("The token is invalid");
		}
	}
	
	@Transactional
	public Boolean changeRevokedToken(Integer id, String token, User user) {
		RevokedToken foundToken = rTokensRepo.findById(id).orElse(null);
		
		if(foundToken == null) {
			throw new IllegalArgumentException("No any revoked token found by the provided id");
		}
		
		User providedPrincipal = userServ.findByUsername(user.getUsername());
		
		if(providedPrincipal == null) {
			throw new NonExistingUserException("No such principal username by the provided user");
		}
		
		String foundTokenString = foundToken.getToken();
		
		if(jwtUtil.validateToken(token, user)) {
			if(foundToken.getToken().equals(token)) {
				return false;
			}
			foundToken.setToken(token);
			foundToken.setRevocationDate(LocalDateTime.now());
			Date tokenExpireDate = jwtUtil.getExpirationDateFromToken(token);
			foundToken.setExpirationDate(tokenExpireDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
			RevokedToken changedRevokedToken = rTokensRepo.save(foundToken);
			
			return !changedRevokedToken.getToken().equals(foundTokenString);
		}
		else {
			throw new IllegalArgumentException("The token is invalid");
		}
	}
	
	public Boolean deleteRevokedToken(Integer id) {
		RevokedToken foundToken = rTokensRepo.findById(id).orElse(null);
		
		if(foundToken == null) {
			throw new IllegalArgumentException("No any revoked token found by the provided id");
		}
		
		rTokensRepo.delete(foundToken);
		
		if(getRevokedTokenById(id) == null) {
			return true;
		} else {
			return false;
		}
	}
	
	public Boolean deleteExpiredRevokedTokens() {
		boolean deletion = false;
		
		List<RevokedToken> tokens = getRevokedTokensList();
		
		LocalDateTime currentDateTime = LocalDateTime.now();
		
		for(RevokedToken token : tokens) {
			if(token.getExpirationDate().isBefore(currentDateTime)) {
				deletion = deleteRevokedToken(token.getId());
			}
		}
		
		return deletion;
	}
}

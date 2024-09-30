package soft.project.demo.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import soft.project.demo.model.User;
import soft.project.demo.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	private final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
	
	@Autowired
	private UserRepository userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.info("Loading user by username: {}", username);
		
		Optional<User> user =userRepo.findByUsername(username);
		
		if (user.isPresent()) {
            return user.get();
        } else {
            logger.warn("User not found with username: {}", username);
            throw new UsernameNotFoundException("No user found by the provided username.");
        }
		//return user.orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
	}
	
}

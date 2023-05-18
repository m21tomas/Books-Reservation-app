package soft.project.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import soft.project.demo.dto.UserDTO;
import soft.project.demo.exception.EmptyInputException;
import soft.project.demo.exception.ExistingUserException;
import soft.project.demo.model.Authority;
import soft.project.demo.model.Role;
import soft.project.demo.model.User;
import soft.project.demo.model.UserInfo;
import soft.project.demo.repository.AuthorityRepository;
import soft.project.demo.repository.UserRepository;
import soft.project.demo.utility.CustomPasswordEncoder;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private AuthorityRepository authRepo;
	
	@Autowired
	private CustomPasswordEncoder passwordEncoder;
	
	@Autowired
	@Lazy
	private SessionRegistry sessionRegistry;
	
	/**
	 * Finds user with a specified username. Don't return User entity via REST.
	 * 
	 * @param username
	 * @return User entity (includes sensitive data)
	 */
	@Transactional(readOnly = true)
	public User findByUsername(String username) {

		return userRepo.findByUsername(username).orElse(null);
	}
	
	/**
	 * Create new user with specified parameters.
	 * 
	 * @param userData data for new user
	 * @return User entity (includes sensitive data)
	 */
	@Transactional
	public User createUser(UserDTO userData) {
		List<String> empty = new ArrayList<>();
		if(userData.getEmail().isEmpty()) empty.add("email");
		if(userData.getRoles().isEmpty()) empty.add("roles");
		if(userData.getPassword().isEmpty()) empty.add("password");
		if(userData.getUsername().isEmpty()) empty.add("username");
		
		if(empty.size() > 0)
		{
			String res1 = "";
			res1= (empty.size()>1) ? "Input fields " : "Input field ";
			for(int i=0; i<empty.size(); i++) {
				res1 = (empty.size()-i>1) ? res1.concat(empty.get(i))+" and " : res1.concat(empty.get(i));
			}
			res1= (empty.size()>1) ? res1.concat(" are empty.") : res1.concat(" is empty.");
			throw new EmptyInputException(res1);
		}
		
		User checkUser = userRepo.findByUsername(userData.getUsername()).orElse(null);
		
		if(checkUser != null) {
			throw new ExistingUserException("The username already taken.");
		}
		
		User newUser = new User();
        
		newUser.setEmail(userData.getEmail());
		List<Authority> authority = new ArrayList<>();
		for(String str : userData.getRoles()) {
			Authority auth = new Authority();
			auth.setAuthority(Role.valueOf(str));
			authority.add(auth);
		}	
		newUser.setAuthorities(authority);
		newUser.setUsername(userData.getUsername());
		newUser.setPassword(passwordEncoder.getPasswordEncoder().encode(userData.getPassword()));
		User returnUser = userRepo.save(newUser);
		for(Authority role : authority) {
			role.setUser(returnUser);
			authRepo.save(role);
		}
		return returnUser;
	}
	
	/**
	 * Returns a list of all users specified data
	 * @return List of users
	 */
	@Transactional(readOnly = true)
	public List<UserInfo> getAllUsers(){
		List<User> usersDb = userRepo.findAll();
		List<UserInfo> responseUsers = new ArrayList<>();
		
		for(User usr : usersDb) {
			UserInfo userDto = new UserInfo(usr.getId(), usr.getAuthorities(),  usr.getUsername(), usr.getEmail());
			responseUsers.add(userDto);
		}
		
		return responseUsers;
	}
	
	/**
	 * Returns a page of registered Users info with specified page number and page
	 * size
	 * 
	 * @return list of user details for ADMIN
	 */
	@Transactional(readOnly = true)
	public Page<UserInfo> getPageOfAllUsers(Pageable pageable) {
		Page<User> users = userRepo.findAll(pageable);
		Page<UserInfo> dtoPage = users.map(new Function<User, UserInfo>() {
			@Override
			public UserInfo apply(User user) {
				UserInfo dto = new UserInfo(user.getId(), user.getAuthorities(),  user.getUsername(), user.getEmail());
				return dto;
			}

		});
		return dtoPage;
	}
	
	/**
	 * 
	 * Deletes the user with a specified username. If an admin is being deleted
	 * and if there is no any more administrators in the database then it
	 * will create an admin again with hardcoded username and password 
	 * admin@admin.lt
	 * 
	 * @param username
	 */
	@Transactional
	public void deleteUser(String username) {

		User user = findByUsername(username);
		String userRole = null;
		for(GrantedAuthority auth : user.getAuthorities()) {
			if(auth.getAuthority().equals(Role.ADMIN.getRole())) {
				userRole = auth.getAuthority();
			}
		}

		if (userRole.equals("Administrator") && authRepo.findByAuthority(Role.ADMIN).size() == 1) {
			List<String> roles = new ArrayList<>();
			roles.add("ADMIN");
			createUser(new UserDTO(roles, "admin@admin.lt", "admin@admin.lt",
					passwordEncoder.getPasswordEncoder().encode("admin@admin.lt")));
		} 
		
		expireSession(user);

		userRepo.deleteByUsername(username);
	}
	
	/**
	 * 
	 * Expire session of logged in user if ADMIN deletes their account
	 * 
	 * @param user
	 */
	private void expireSession(User user) {

		List<Object> principals = sessionRegistry.getAllPrincipals();
		for (Object principal : principals) {
			UserDetails pUser = (UserDetails) principal;
			if (pUser.getUsername().equals(user.getUsername())) {
				for (SessionInformation activeSession : sessionRegistry.getAllSessions(principal, false)) {
					activeSession.expireNow();
				}
			}
		}
	}

}

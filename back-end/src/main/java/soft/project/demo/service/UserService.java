package soft.project.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import soft.project.demo.dto.UserDTO;
import soft.project.demo.enums.Role;
import soft.project.demo.exception.EmptyInputException;
import soft.project.demo.exception.ExistingUserException;
import soft.project.demo.exception.NonExistingUserException;
import soft.project.demo.model.Authority;
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
	public User createUser(UserDTO userData, int mode) {
		
		checkUserDataFields(userData);
		
		User checkUser = userRepo.findByUsername(userData.getUsername()).orElse(null);
		
		if(checkUser != null) {
			throw new ExistingUserException("The username already taken.");
		}
		
		User newUser = new User();
        
		newUser.setEmail(userData.getEmail()); 
//		newUser.setAuthorities(authority);
		newUser.setUsername(userData.getUsername());
		newUser.setPassword(passwordEncoder.getPasswordEncoder().encode(userData.getPassword()));
		
		User returnUser = userRepo.save(newUser);
		
		List<Authority> authorityList = new ArrayList<>();
		
		if (mode == 1) {
			Authority auth = new Authority();
	        auth.setUser(returnUser);
	        auth.setAuthority(Role.READER);
	        authorityList.add(auth);
		} else {
		    for (String authItem : userData.getAuthorities()) {
		        Authority auth = new Authority();
		        auth.setUser(returnUser);
		        auth.setAuthority(Role.valueOf(authItem));
		        authorityList.add(auth);
		    }
	    }

	    authRepo.saveAll(authorityList);
		
		return returnUser;
	}
	
	private static void checkUserDataFields (UserDTO userData) {
		List<String> empty = new ArrayList<>();
		for (String authItem : userData.getAuthorities()) {
			if(authItem.isEmpty()) {
				empty.add("Authority field \"roles\"");
				break;
			}
		}
		if(userData.getEmail().isEmpty()) empty.add("email");
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
	}
	
	private UserInfo mapUserToDTO(User user) {
		UserInfo userDTO = new UserInfo();
	    userDTO.setUserId(user.getId());
	    userDTO.setUsername(user.getUsername());
	    userDTO.setPassword(user.getPassword());
	    userDTO.setEmail(user.getEmail());
	    userDTO.setReservations(user.getReservations());
	    userDTO.setFavoriteBooks(user.getFavoriteBooks());
	    
	    List<String> userRoles = new ArrayList<>();
	    
	    userRoles = user.getAuthorities().stream()
	    				.map(auth -> auth.getAuthority())
	    				.collect(Collectors.toList());
	    
	    userDTO.setRoles(userRoles);
	    
	    return userDTO;
	}


	@Transactional(readOnly = true)
	public UserInfo findUserById(Integer id, String pName) {
		User foundUser = userRepo.findById(id).orElse(null);
		User checkPrincipal = findByUsername(pName);
		UserInfo foundUserDto = null;
		boolean admin = false;
		boolean sameUsername = false;
		if(foundUser == null) {
			throw new NonExistingUserException("No user with id: "+id.toString());
		}
		else {
			if(checkPrincipal != null) {
				for(GrantedAuthority auth : checkPrincipal.getAuthorities()) {
					if(auth.getAuthority().equals(Role.ADMIN.getRole())) {
						admin = true;
						break;
					}
				}
			}
			else {
				throw new NonExistingUserException("Principal is null");
			}
			if(!admin) {
				if(foundUser.getUsername().equals(pName)) {
					sameUsername = true;
				}
			}
			if(admin || sameUsername) {
				
				foundUserDto = mapUserToDTO(foundUser);
			}
		}
		return foundUserDto ;
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
			responseUsers.add(mapUserToDTO(usr));
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
				mapUserToDTO(user);
				return mapUserToDTO(user);
			}

		});
		return dtoPage;
	}
	
	@Transactional
	public boolean updateUser (Integer id, UserDTO userData, String pName) {
		
		User userToChange = userRepo.findById(id).orElse(null);
		
		User checkPrincipal = findByUsername(pName);

		boolean reader = true;
		boolean sameUsername = false;
		
		if(checkPrincipal != null) {
			for(GrantedAuthority auth : checkPrincipal.getAuthorities()) {
				if(auth.getAuthority().equals(Role.ADMIN.getRole())) {
					reader = false;
					break;
				}
			}
		}
		else {
			throw new NonExistingUserException("Principal is null");
		}
		if(reader) {
			if(userToChange.getUsername().equals(pName)) {
				sameUsername = true;
			}
		}
		
		if(userToChange == null) {
			throw new ExistingUserException("No such user for its data to be updated.");
		}
		
		checkUserDataFields(userData);
		
		if(!reader || sameUsername) {
			userToChange.setEmail(userData.getEmail());	
			userToChange.setUsername(userData.getUsername());
			userToChange.setPassword(passwordEncoder.getPasswordEncoder().encode(userData.getPassword()));
			User returnUser = userRepo.save(userToChange);
			
			List<Authority> authorityList = new ArrayList<>();
			
			authRepo.deleteByUserId(userToChange.getId());
			
			if(!reader) {		
				for (String authItem : userData.getAuthorities()) {
					Authority auth = new Authority();
					auth.setUser(returnUser);
					auth.setAuthority(Role.valueOf(authItem));
					authorityList.add(auth);
				}
			}
			else if(sameUsername) {
				Authority auth = new Authority();
				auth.setUser(returnUser);
				auth.setAuthority(Role.READER);
				authorityList.add(auth);
			}
	
			authRepo.saveAll(authorityList);
			
			return true;
		}
		else {
			throw new ExistingUserException("As a simple user you cannot change another user data");
		}
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
			userRole = auth.getAuthority();
		}

		if (userRole.equals("Administrator") && authRepo.findByAuthority(Role.ADMIN).size() == 1) {
			
			List<String> adminAuthorities = new ArrayList<>();
			
			adminAuthorities.add("ADMIN");
			
			UserDTO firstAdmin = new UserDTO(adminAuthorities, "admin@admin.lt", "admin@admin.lt","admin@admin.lt");
			
			createUser(firstAdmin, 0);
		} 

		userRepo.deleteByUsername(username);
		
		SecurityContextHolder.clearContext();	
	}
	
	/**
	 * 
	 * Expire session of logged in user if ADMIN deletes their account
	 * 
	 * @param user
	 */
	/*
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
	*/
}

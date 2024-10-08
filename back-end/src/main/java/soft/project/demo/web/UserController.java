package soft.project.demo.web;



import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import soft.project.demo.dto.CreateUserDto;
import soft.project.demo.dto.UserDTO;
import soft.project.demo.exception.ValidationException;
import soft.project.demo.model.User;
import soft.project.demo.model.UserInfo;
import soft.project.demo.service.UserService;

@RestController
@RequestMapping(path = "/api/users")
@Tag(name = "User Controller")
public class UserController {

	private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserService userService;
	
	
	/**
	 * Create new user from login screen
	 */
	@PostMapping("/createReader")
	@Operation(summary = "Create reader")
	public ResponseEntity<String> createUser(@RequestBody UserDTO userInfo){
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if(authentication instanceof AnonymousAuthenticationToken) {			
			User newUser= userService.createUser(userInfo, 1);
			
			if(newUser != null) {
				LOG.info("New user [{}] created", userInfo.getUsername());
				return new ResponseEntity<String>("New user created", HttpStatus.CREATED);
			}
		}
		LOG.error("New user [{}] NOT created", userInfo.getUsername());
		return new ResponseEntity<String>("User not created", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid CreateUserDto createUserDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
//        	return ResponseEntity
//                    .badRequest()
//                    .body(new MessageResponse(bindingResult.getAllErrors().toString()));
        	throw new ValidationException(bindingResult);
        }
        
        List<String> authority = new ArrayList<>();
        authority.add(createUserDto.getRole().toString());
		UserDTO testUSerDto = new UserDTO(authority, createUserDto.getEmail(), "testUser22", createUserDto.getPassword());
		// userService.createUser(testUSerDto, 0);
		if(testUSerDto.getUsername().equals("testUser22")) {
			LOG.info("New user [{}] created", "testUser22");
			return new ResponseEntity<String>("New test user created", HttpStatus.CREATED);
		}
		else {
			LOG.warn("New user [{}] IS NOT created", "testUser22");
			return new ResponseEntity<String>("User not created", HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
	
	/**
	 * Admin creates new user 
	 */
	@Secured({"Administrator"})
	@PostMapping("/createUser")
	@Operation(summary = "Create reader or admin")
	public ResponseEntity<String> createUserByAdmin(@RequestBody UserDTO userInfo){
		
		String principalName = SecurityContextHolder.getContext().getAuthentication().getName();
			
		User newUser= userService.createUser(userInfo, 0);
			
		if(newUser != null) {
			LOG.info("[{}]: New user [{}] created", principalName, userInfo.getUsername());
			return new ResponseEntity<String>("New user created", HttpStatus.CREATED);
		}

		LOG.error("[{}]: New user [{}] NOT created", principalName, userInfo.getUsername());
		return new ResponseEntity<String>("User not created", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	//@Secured({"ROLE_Administrator", "ROLE_Reader"})
	@PreAuthorize("hasAuthority('Administrator') || hasAuthority('Reader')")
	@GetMapping("/{id}")
	@Operation(summary = "Get User Information")
	public ResponseEntity<?> getUserData(@PathVariable Integer id){
		String principalUsername = SecurityContextHolder.getContext().getAuthentication().getName();
		
		UserInfo subject = userService.findUserById(id, principalUsername);
		if(subject != null) {
		
			LOG.info("[{}]: Retrieving user id: {} information", principalUsername, id);
		
			return new ResponseEntity<UserInfo>(subject, HttpStatus.OK);
		}
		else {
			LOG.warn("[{}]: Unable to retrieve user id: {} information", principalUsername, id);
			
			String str = "You cannot retrieve user with id: " + id + " information";
			
			return new ResponseEntity<String>(str, HttpStatus.CONFLICT);
		}
	}
	
	/**
	 *  Admin gets all users information
	 *  @return List of all users
	 */
	@Secured({"Administrator"})
	@GetMapping("/allUsers")
	@Operation(summary = "Get All Users")
	public ResponseEntity<List<UserInfo>> getAllUsers (){
		String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
		
		LOG.info("[{}]: Retrieving all users information", currentUsername);
		
		return new ResponseEntity<List<UserInfo>>(userService.getAllUsers(), HttpStatus.OK);
	}
	
	
	/**
	 * User updates its account data
	 */
	@Secured({"Administrator", "Reader"})
	@PutMapping("/updateAccount/{id}")
	@Operation(summary = "Update user")
	public ResponseEntity<String> updateUserData (@PathVariable Integer id, @RequestBody UserDTO userData){
		String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
		
		if(userService.updateUser(id, userData, currentUsername)) {
			LOG.info("[{}]: User data updated", currentUsername);
			return new ResponseEntity<String>("User data updated", HttpStatus.OK);
		}
		else {
			LOG.error("[{}]: User data updated", currentUsername);
			return new ResponseEntity<String>("User data not updated", HttpStatus.BAD_REQUEST);
		}
	}
	
	/**
	 * Returns a page of all users. Method only accessible to ADMIN users
	 * @return page of all users
	 */
	@Secured({ "Administrator" })
	@GetMapping(path = "/admin/allusers")
	@Operation(summary = "Get a page of all users")
	public Page<UserInfo> getAllUsers(@RequestParam("page") int page, @RequestParam("size") int size) {
		
		String adminNickname = SecurityContextHolder.getContext().getAuthentication().getName();

		Sort.Order order = new Sort.Order(Sort.Direction.DESC, "id");

		Pageable pageable = PageRequest.of(page, size, Sort.by(order));

		LOG.info("[{}]: "+"Sending a page of all users", adminNickname);
		
		return userService.getPageOfAllUsers(pageable);
	}
	
	/**
	 * Deletes user with specified username. Method only accessible to ADMIN users
	 * @param username
	 */
	@Secured({ "Administrator" })
	@DeleteMapping(path = "/admin/delete/{username}")
	@Operation(summary = "Delete user")
	public ResponseEntity<String> deleteUser(@PathVariable final String username) {
		
		String adminNickname = SecurityContextHolder.getContext().getAuthentication().getName();	
		
		if (userService.findByUsername(username) != null) {
			LOG.info("[{}]: deleting {}", adminNickname , username);
			userService.deleteUser(username);
			if (userService.findByUsername(username) == null) {
				LOG.info("[{}]: {} deleted ", adminNickname , username);
				return new ResponseEntity<String>(username+" deleted", HttpStatus.OK);
			}
			else {
				LOG.error("[{}]: {} not deleted ", adminNickname , username);
				return new ResponseEntity<String>(username+" not deleted", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		LOG.warn("[{}]: no user {} to be deleted", adminNickname , username);

		return new ResponseEntity<String>("No user " + username + " to be deleted", HttpStatus.NOT_FOUND);
	}
}

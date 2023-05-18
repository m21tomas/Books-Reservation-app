package soft.project.demo.model;

import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import soft.project.demo.dto.UserDTO;
import soft.project.demo.repository.UserRepository;
import soft.project.demo.service.UserService;


@Component
public class FirstAdmin {
	
	private static final Logger LOG = LoggerFactory.getLogger(FirstAdmin.class);

	@Autowired
	UserRepository userDao;

	@Autowired
	UserService userService;

	/**
	 * Add first users (ADMIN, MANAGER, USER) to the User repository for testing
	 * purposes
	 * 
	 * @throws Exception
	 * 
	 */
	@PostConstruct
	public void addFirstUser() throws Exception {

		if (userDao.findUsersByAuthority(Role.ADMIN).size() == 0) {
			List<String> adminRole = new ArrayList<>();
			List<String> userRole = new ArrayList<>();
			List<String> managersRoles = new ArrayList<>();
			//public UserDTO(List<String> roles, String email, String username, String password)
			adminRole.add("ADMIN");
			UserDTO firstAdmin = new UserDTO(adminRole, "admin@admin.lt", "admin@admin.lt","admin@admin.lt");

			userRole.add("USER");
			UserDTO firstUser = new UserDTO(userRole, "user@user.lt", "user@user.lt", "user@user.lt");
			
			managersRoles.add("ADMIN");
			managersRoles.add("USER");
			UserDTO firstManager = new UserDTO(managersRoles, "manager@manager.lt", "manager@manager.lt", "manager@manager.lt");

			userService.createUser(firstAdmin);
			userService.createUser(firstUser);
			userService.createUser(firstManager);
		}
		else LOG.info("There already exist users which Role is - {}", Role.ADMIN.toString());
	}
}

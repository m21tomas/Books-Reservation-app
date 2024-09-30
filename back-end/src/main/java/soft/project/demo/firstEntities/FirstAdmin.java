package soft.project.demo.firstEntities;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import soft.project.demo.dto.UserDTO;
import soft.project.demo.enums.Role;
import soft.project.demo.repository.UserRepository;
import soft.project.demo.service.UserService;


@Component
public class FirstAdmin {

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
	//@SuppressWarnings("unused")
	@PostConstruct
	public void addFirstUser() {

		if (userDao.findUsersByAuthority(Role.ADMIN).size() == 0) {

			//public UserDTO(List<String> authorities, String email, String username, String password)
			List<String> adminAuthorities = new ArrayList<>();
			adminAuthorities.add("ADMIN");
			UserDTO firstAdmin = new UserDTO(adminAuthorities, "admin@admin.lt", "admin@admin.lt","admin@admin.lt");

			List<String> readerAuthorities = new ArrayList<>();
			readerAuthorities.add("READER");
			UserDTO firstUser = new UserDTO(readerAuthorities, "reader@reader.lt", "reader@reader.lt", "reader@reader.lt");
			
			List<String> managerAuthorities = new ArrayList<>();
			managerAuthorities.add("ADMIN");
			managerAuthorities.add("READER");
			UserDTO firstManager = new UserDTO(managerAuthorities, "manager@manager.lt", "manager@manager.lt", "manager@manager.lt");

			userService.createUser(firstAdmin, 0);
			userService.createUser(firstUser, 0);
			userService.createUser(firstManager, 0);
		}
		//else LOG.info("There already exist users which Role is - {}", Role.ADMIN.toString());
	}
}

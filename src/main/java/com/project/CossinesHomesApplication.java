package com.project;

import com.project.entity.concretes.user.UserRole;
import com.project.entity.enums.Role;
import com.project.payload.request.user.UserRequest;
import com.project.repository.user.UserRoleRepository;
import com.project.service.user.UserRoleService;
import com.project.service.user.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class CossinesHomesApplication  implements CommandLineRunner {

	private final UserRoleService userRoleService;
	private final UserRoleRepository userRoleRepository;
	private final UserService userService;

	public CossinesHomesApplication(UserRoleService userRoleService, UserRoleRepository userRoleRepository, UserService userService) {
		this.userRoleService = userRoleService;
		this.userRoleRepository = userRoleRepository;
		this.userService = userService;
	}
	public static void main(String[] args) {
		SpringApplication.run(CossinesHomesApplication.class, args);
	}

	@Override //uygulama her çalıştığında bu kodlar otomatik olarak çalışacak
	public void run(String... args) throws Exception {

		//Role tablosu dolduruluyor
		if(userRoleService.getAllUserRole().isEmpty()) {

			UserRole admin = new UserRole();
			admin.setRoleName("Admin");
			admin.setRole(Role.ADMIN);
			userRoleRepository.save(admin);

			UserRole manager = new UserRole();
			manager.setRole(Role.MANAGER);
			manager.setRoleName("Manager");
			userRoleRepository.save(manager);

			UserRole customer = new UserRole();
			customer.setRole(Role.CUSTOMER);
			customer.setRoleName("Customer");
			userRoleRepository.save(customer);
		}


		//built_in admin oluşturulacak
		if(userService.countAllAdmins() == 0) {
			UserRequest adminRequest = new UserRequest();
			adminRequest.setUsername("Admin");
			adminRequest.setEmail("admin@admin.com");
			adminRequest.setPasswordHash("12345678");
			adminRequest.setFirstName("Ahmet");
			adminRequest.setLastName("Şimşek");
			adminRequest.setPhone("123-331-1111");
			userService.saveUser(adminRequest, "Admin");
		}
	}
}

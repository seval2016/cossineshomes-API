package com.project;

import com.project.entity.concretes.business.Advert;
import com.project.entity.concretes.business.AdvertType;
import com.project.entity.concretes.business.Images;
import com.project.entity.concretes.user.UserRole;
import com.project.entity.enums.AdvertStatus;
import com.project.entity.enums.RoleType;
import com.project.payload.request.business.CityRequest;
import com.project.payload.request.business.CountryRequest;
import com.project.payload.request.business.DistrictRequest;
import com.project.payload.request.user.UserRequest;
import com.project.payload.request.user.UserSaveRequest;
import com.project.repository.business.ImagesRepository;
import com.project.repository.user.UserRoleRepository;
import com.project.service.business.*;
import com.project.service.helper.MethodHelper;
import com.project.service.user.UserRoleService;
import com.project.service.user.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class CossinesHomesApplication  implements CommandLineRunner {

	private final PasswordEncoder passwordEncoder;
	private final UserRoleService userRoleService;
	private final UserService userService;
	private final UserRoleRepository userRoleRepository;
	private  final CityService cityService;
	private final CategoryPropertyKeyService categoryPropertyKeyService;
	private final AdvertTypesService advertTypesService;
	private final CategoryService categoryService;
	private final CountryService countryService;
	private final DistrictService districtService;
	private final AdvertService advertService;
	private final ImagesRepository imagesRepository;
	private final MethodHelper methodHelper;

	public CossinesHomesApplication(AdvertService advertService,
									AdvertTypesService advertTypesService,
									UserRoleService userRoleService,
									UserService userService,
									UserRoleRepository userRoleRepository,
									CategoryService categoryService, PasswordEncoder passwordEncoder,
									CityService cityService, CategoryPropertyKeyService categoryPropertyKeyService,
									CountryService countryService, DistrictService districtService, ImageService imageService,
									ImagesRepository imagesRepository, MethodHelper methodHelper) {
		this.userRoleService = userRoleService;
		this.userService = userService;
		this.userRoleRepository = userRoleRepository;
		this.categoryService=categoryService;
		this.passwordEncoder=passwordEncoder;
		this.cityService = cityService;
		this.categoryPropertyKeyService = categoryPropertyKeyService;
		this.countryService = countryService;
		this.advertTypesService=advertTypesService;
		this.districtService = districtService;
		this.advertService=advertService;

		this.imagesRepository = imagesRepository;
		this.methodHelper = methodHelper;
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
			admin.setRole(RoleType.ADMIN);
			userRoleRepository.save(admin);

			UserRole manager = new UserRole();
			manager.setRole(RoleType.MANAGER);
			manager.setRoleName("Manager");
			userRoleRepository.save(manager);

			UserRole customer = new UserRole();
			customer.setRole(RoleType.CUSTOMER);
			customer.setRoleName("Customer");
			userRoleRepository.save(customer);
		}


		//built_in admin oluşturulacak
		if(userService.countAllAdmins() == 0) {
			UserSaveRequest adminRequest = new UserSaveRequest();
			adminRequest.setEmail("admin@gmail.com");
			adminRequest.setPasswordHash(passwordEncoder.encode("12345678"));
			adminRequest.setFirstName("Ahmet");
			adminRequest.setLastName("Şimşek");
			adminRequest.setPhone("123-331-1111");
			adminRequest.setBuiltIn(true);

			userService.saveUserWithoutRequest(adminRequest);
		}

		categoryService.generateCategory();

		if (countryService.countAllCountries() == 0) {
			CountryRequest countryRequest = new CountryRequest();
			countryRequest.setName("Türkiye");

			countryService.setBuiltInForCountry();
		}

		if (cityService.countAllCities() == 0) {
			CityRequest cityRequest = new CityRequest();
			cityRequest.setName("Istanbul");
			cityRequest.setCountry_id(1);

			cityService.setBuiltInForCity();
		}

		if (districtService.countAllDistricts() == 0) {
			DistrictRequest districtRequest = new DistrictRequest();
			districtRequest.setName("Üsküdar");
			districtRequest.setDistrict_id(1);

			districtService.setBuiltInForDistrict();
		}

		if (advertTypesService.getAllAdvertTypes().size() == 0) {
			AdvertType advertType = new AdvertType();
			advertType.setBuiltIn(true);
			advertType.setTitle("rent");
			//	advertType.setAdvertList();
			advertTypesService.saveAdvertTypeRunner(advertType);

			AdvertType advertType2 = new AdvertType();
			advertType2.setBuiltIn(true);
			advertType2.setTitle("sell");
			//	advertType2.setAdvertList();
			advertTypesService.saveAdvertTypeRunner(advertType2);
	}
		if (advertService.getAllAdverts().size() == 0) {
			Object[] arr1 = {"Şehir Merkezinde Modern Daire", "3+1 odalı, geniş balkonlu ve yerden ısıtmalı modern daire. Merkezi konum, kapalı otopark ve güvenlik mevcut", "Ankara", 2500000.00, 1L, 2L};
			Object[] arr2 = {"Deniz Manzaralı Ev", "Denize sıfır, 4+1 odalı, özel havuzlu villa. Eşsiz manzara, geniş bahçe ve lüks iç mekan.", "İzmir", 10000000.00, 1L, 3L};
			Object[] arr3 = {"Doğa İçinde Ferah Ev", "2+1 odalı, bahçeli köy evi. Şehrin gürültüsünden uzak, huzurlu yaşam için ideal. Ahşap detaylarla sıcak bir atmosfer.", "Bursa", 12000000.00, 1L, 4L};
			Object[] arr4 = {"Lüks Şehir Evi", "Antalya'nın prestijli bölgesinde 5+1 odalı lüks şehir evi. Akıllı ev sistemi, geniş bahçe ve muhteşem şehir manzarası.", "Antalya", 45000000.00, 1L, 5L};

			List<Object[]> array = new ArrayList<>(new ArrayList<>(Arrays.asList(arr1, arr2, arr3, arr4)));

			for (Object[] o : array) {
				Advert advert = new Advert();
				advert.setTitle((String) o[0]);
				advert.setDescription((String) o[1]);
				advert.setLocation((String) o[2]);
				advert.setPrice((Double) o[3]);
				advert.setCountry(countryService.getCountryById(3L));
				advert.setDistrict(districtService.getDistrictByIdForAdvert((Long) o[4]));
				advert.setCity(cityService.getCityById((Long) o[4]));
				advert.setCategory(categoryService.getCategoryById(1L));
				advert.setAdvertType(advertTypesService.findByIdAdvertType(1L));
				advert.setCreateAt(LocalDateTime.now());
				advert.setBuiltIn(true);
				advert.setUser(methodHelper.findUserWithId(1L));
				advert.setStatus(AdvertStatus.ACTIVATED.getValue());
				advert.setActive(true);

				advertService.saveRunner(advert);

				String[] imageNames = {"mustakil.jpg", "yatak.jpg", "banyo.jpg", "mutfak.jpg"}; // Bu diziyi fotoğraf isimleriyle doldur

				for (String imageName : imageNames) {
					Path path = Paths.get("src/main/resources/static/images/" + imageName);
					byte[] imageData = Files.readAllBytes(path);

					Images image = new Images();
					image.setName(imageName);
					image.setData(imageData);
					image.setAdvert(advert);

					imagesRepository.save(image);
				}

			}

		}

	}}


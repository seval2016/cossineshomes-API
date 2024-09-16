package com.project;

import com.project.entity.concretes.business.Advert;
import com.project.entity.concretes.business.AdvertType;
import com.project.entity.concretes.business.Images;
import com.project.entity.concretes.user.UserRole;
import com.project.entity.enums.RoleType;
import com.project.payload.request.business.CityRequest;
import com.project.payload.request.business.CountryRequest;
import com.project.payload.request.business.DistrictRequest;
import com.project.payload.request.user.UserSaveRequest;
import com.project.repository.business.ImagesRepository;
import com.project.repository.user.UserRoleRepository;
import com.project.service.business.*;
import com.project.service.helper.AdvertHelper;
import com.project.service.helper.CategoryPropertyKeyHelper;
import com.project.service.helper.MethodHelper;
import com.project.service.user.UserRoleService;
import com.project.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class CossinesHomesApplication implements CommandLineRunner {

	private final PasswordEncoder passwordEncoder;
	private final UserRoleService userRoleService;
	private final UserService userService;
	private final UserRoleRepository userRoleRepository;
	private final CityService cityService;
	private final CategoryPropertyKeyService categoryPropertyKeyService;
	private final AdvertTypesService advertTypesService;
	private final CategoryService categoryService;
	private final CountryService countryService;
	private final DistrictService districtService;
	private final AdvertService advertService;
	private final ImagesRepository imagesRepository;
	private final MethodHelper methodHelper;
	private final AdvertHelper advertHelper;
	private final CategoryPropertyKeyHelper categoryPropertyKeyHelper;

	@Autowired
	public CossinesHomesApplication(PasswordEncoder passwordEncoder,
									UserRoleService userRoleService,
									UserService userService,
									UserRoleRepository userRoleRepository,
									CityService cityService,
									CategoryPropertyKeyService categoryPropertyKeyService,
									AdvertTypesService advertTypesService,
									CategoryService categoryService,
									CountryService countryService,
									DistrictService districtService,
									AdvertService advertService,
									ImagesRepository imagesRepository,
									MethodHelper methodHelper,
									AdvertHelper advertHelper,
									CategoryPropertyKeyHelper categoryPropertyKeyHelper) {
		this.passwordEncoder = passwordEncoder;
		this.userRoleService = userRoleService;
		this.userService = userService;
		this.userRoleRepository = userRoleRepository;
		this.cityService = cityService;
		this.categoryPropertyKeyService = categoryPropertyKeyService;
		this.advertTypesService = advertTypesService;
		this.categoryService = categoryService;
		this.countryService = countryService;
		this.districtService = districtService;
		this.advertService = advertService;
		this.imagesRepository = imagesRepository;
		this.methodHelper = methodHelper;
		this.advertHelper = advertHelper;
		this.categoryPropertyKeyHelper = categoryPropertyKeyHelper;
	}

	public static void main(String[] args) {
		SpringApplication.run(CossinesHomesApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		// Role tablosu dolduruluyor
		if (userRoleService.getAllUserRole().isEmpty()) {
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

		// Built-in admin oluşturuluyor
		if (userService.countAllAdmins() == 0) {
			UserSaveRequest adminRequest = new UserSaveRequest();
			adminRequest.setEmail("admin@gmail.com");
			adminRequest.setPasswordHash(passwordEncoder.encode("12345678"));
			adminRequest.setFirstName("Ahmet");
			adminRequest.setLastName("Şimşek");
			adminRequest.setPhone("123-331-1111");
			adminRequest.setBuiltIn(true);
			userService.saveUserWithoutRequest(adminRequest);
		}

		// Varsayılan kategoriler oluşturuluyor
		categoryService.generateCategory();
		categoryPropertyKeyService.generateCategoryPropertyKeys();

		// Varsayılan ülkeler ekleniyor
		if (countryService.countAllCountries() == 0) {
			addDefaultCountries();
			countryService.setBuiltInForCountry();
		}

		// Varsayılan şehirler ekleniyor
		if (cityService.countAllCities() == 0) {
			addDefaultCity();
			cityService.setBuiltInForCity();
		}

		// Varsayılan ilçeler ekleniyor
		if (districtService.countAllDistricts() == 0) {
			addDefaultDistricts(); // Default districts added
			districtService.setBuiltInForDistrict();
		}

		// Varsayılan ilan türleri ekleniyor
		if (advertTypesService.getAllAdvertTypes().isEmpty()) {
			AdvertType advertType = new AdvertType();
			advertType.setBuiltIn(true);
			advertType.setTitle("rent");
			advertTypesService.saveAdvertTypeRunner(advertType);

			AdvertType advertType2 = new AdvertType();
			advertType2.setBuiltIn(true);
			advertType2.setTitle("sell");
			advertTypesService.saveAdvertTypeRunner(advertType2);
		}

		// Varsayılan ilanlar ekleniyor
		if (advertHelper.getAllAdverts().isEmpty()) {
			addDefaultAdverts();
		}
	}

	// Varsayılan ülkeleri ekleyen metot
	private void addDefaultCountries() {
		CountryRequest defaultCountry = new CountryRequest();
		defaultCountry.setName("Türkiye");
	}
	private void addDefaultCity(){
		CityRequest defaultCity=new CityRequest();
		defaultCity.setName("İstanbul");
		defaultCity.setCountry_id(1L);
	}
	private void addDefaultDistricts(){
		DistrictRequest defaultDistrict=new DistrictRequest();
		defaultDistrict.setName("Üsküdar");
	}
	private void addDefaultAdverts(){
		Object[] arr1 = {"Şehir Merkezinde Modern Daire", "3+1 odalı, geniş balkonlu ve yerden ısıtmalı modern daire. Merkezi konum, kapalı otopark ve güvenlik mevcut", "Ankara", 2500000.00, 1L, 2L};
		Object[] arr2 = {"Deniz Manzaralı Ev", "Denize sıfır, 4+1 odalı, özel havuzlu villa. Eşsiz manzara, geniş bahçe ve lüks iç mekan.", "İzmir", 10000000.00, 1L, 3L};
		Object[] arr3 = {"Doğa İçinde Ferah Ev", "2+1 odalı, bahçeli köy evi. Şehrin gürültüsünden uzak, huzurlu yaşam için ideal. Ahşap detaylarla sıcak bir atmosfer.", "Bursa", 12000000.00, 1L, 4L};
		Object[] arr4 = {"Lüks Şehir Evi", "Antalya'nın prestijli bölgesinde 5+1 odalı lüks şehir evi. Akıllı ev sistemi, geniş bahçe ve muhteşem şehir manzarası.", "Antalya", 45000000.00, 1L, 5L};

		List<Object[]> advertList = Arrays.asList(arr1, arr2, arr3, arr4);

		for (Object[] advertData : advertList) {
			Advert advert = new Advert();
			advert.setTitle((String) advertData[0]);
			advert.setDescription((String) advertData[1]);
			advert.setLocation((String) advertData[2]);
			advert.setPrice((BigDecimal) advertData[3]);
			advert.setCountry(countryService.getCountryById(1L));
			advert.setDistrict(districtService.getDistrictByIdForAdvert((Long) advertData[5]));
			advert.setCity(cityService.getCityById((Long) advertData[5]));
			advert.setCategory(categoryService.getCategoryById(1L));
			advert.setAdvertType(advertTypesService.findByIdAdvertType((Long) advertData[4]));
			advert.setBuiltIn(true);
			advert.setUser(methodHelper.findUserWithId(1L));
			advert.setStatus(1); // Activated status
			advert.setIsActive(true);
			advert.setCreateAt(LocalDateTime.now());

			advertHelper.saveRunner(advert);

			// Define and save images for each advert
			String[] imageNames = {"mustakil.jpg", "yali.jpg", "villa.jpg"};

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


	// Varsayılan ilanları ekleyen metot

}
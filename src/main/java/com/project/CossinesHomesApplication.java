package com.project;

import com.project.entity.concretes.business.*;
import com.project.entity.concretes.user.UserRole;
import com.project.entity.enums.RoleType;
import com.project.payload.request.business.CityRequest;
import com.project.payload.request.business.CountryRequest;
import com.project.payload.request.business.DistrictRequest;
import com.project.payload.request.user.UserSaveRequest;
import com.project.repository.business.DistrictRepository;
import com.project.repository.business.ImagesRepository;
import com.project.repository.user.UserRoleRepository;
import com.project.service.business.*;
import com.project.service.helper.AdvertHelper;
import com.project.service.helper.CategoryHelper;
import com.project.service.helper.CategoryPropertyKeyHelper;
import com.project.service.helper.MethodHelper;
import com.project.service.user.UserRoleService;
import com.project.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
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
	private final CategoryHelper categoryHelper;
	private final CategoryPropertyKeyHelper categoryPropertyKeyHelper;
	private final DistrictRepository districtRepository;

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
									DistrictRepository districtRepository,
									MethodHelper methodHelper,
									AdvertHelper advertHelper,
									CategoryHelper categoryHelper,
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
		this.districtRepository = districtRepository;
		this.methodHelper = methodHelper;
		this.advertHelper = advertHelper;
		this.categoryHelper = categoryHelper;
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
		categoryHelper.generateCategory();
		categoryHelper.generateCategoryPropertyKeys();

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
		Country turkey = new Country();
		turkey.setName("Türkiye");
		turkey.setBuiltIn(true);
		countryService.saveCountry(turkey);

		Country germany = new Country();
		germany.setName("Almanya");
		germany.setBuiltIn(false);
		countryService.saveCountry(germany);
	}

	private void addDefaultCity() {
		CityRequest defaultCity = new CityRequest();
		defaultCity.setName("İstanbul");
		defaultCity.setCountry_id(1L); // Mevcut bir ülkenin id'sini kullanın
		cityService.saveCity(defaultCity);
	}

	private void addDefaultDistricts() {
		if (districtRepository.findAll().isEmpty()) {
			City city = cityService.getCityByName("İstanbul");
			if (city != null) {
				District district = District.builder()
						.name("Kadıköy")
						.city(city)
						.builtIn(false)
						.build();
				districtRepository.save(district);
			} else {
				System.err.println("İstanbul şehri bulunamadı!");
			}
		}
	}

	private void addDefaultAdverts() throws IOException {
		List<Object[]> advertList = Arrays.asList(
				new Object[]{"Şehir Merkezinde Modern Daire", "3+1 odalı, geniş balkonlu ve yerden ısıtmalı modern daire. Merkezi konum, kapalı otopark ve güvenlik mevcut", "Ankara", new BigDecimal("2500000.00"), 1L, 2L},
				new Object[]{"Deniz Manzaralı Ev", "Denize sıfır, 4+1 odalı, özel havuzlu villa. Eşsiz manzara, geniş bahçe ve lüks iç mekan.", "İzmir", new BigDecimal("10000000.00"), 1L, 3L},
				new Object[]{"Doğa İçinde Ferah Ev", "2+1 odalı, bahçeli köy evi. Şehrin gürültüsünden uzak, huzurlu yaşam için ideal. Ahşap detaylarla sıcak bir atmosfer.", "Bursa", new BigDecimal("12000000.00"), 1L, 4L},
				new Object[]{"Lüks Şehir Evi", "Antalya'nın prestijli bölgesinde 5+1 odalı lüks şehir evi. Akıllı ev sistemi, geniş bahçe ve muhteşem şehir manzarası.", "Antalya", new BigDecimal("45000000.00"), 1L, 5L}
		);

		for (Object[] advertData : advertList) {
			Advert advert = new Advert();
			advert.setTitle((String) advertData[0]);
			advert.setDescription((String) advertData[1]);
			advert.setLocation((String) advertData[2]);
			advert.setPrice((BigDecimal) advertData[3]);
			advert.setCountry(countryService.getCountryById(1L));

			// District ve City için doğru ID'leri kullanın
			Long districtId = (Long) advertData[1];
			Long cityId = (Long) advertData[1];

			advert.setDistrict(districtService.getDistrictByIdForAdvert(districtId));
			advert.setCity(cityService.getCityById(cityId));

			advert.setCategory(categoryService.getCategoryById(1L));
			advert.setAdvertType(advertTypesService.findByIdAdvertType((Long) advertData[4]));
			advert.setBuiltIn(true);
			advert.setUser(methodHelper.findUserWithId(1L));
			advert.setStatus(1); // Activated status
			advert.setIsActive(true);
			advert.setCreateAt(LocalDateTime.now());

			// Slug oluşturma
			advert.generateSlug();

			try {
				advertHelper.saveRunner(advert);
			} catch (Exception e) {
				System.err.println("Advert kaydedilirken hata: " + e.getMessage());
				continue; // Hata alındıysa bir sonraki ilana geç
			}

			// Resim ekleme
			addImagesToAdvert(advert);
		}
	}

	// Resimleri ekleyen ayrı bir metot
	private void addImagesToAdvert(Advert advert) {
		String[] imageNames = {"mustakil.jpg", "yali.jpg", "villa.jpg"};
		for (String imageName : imageNames) {
			try {
				Path path = Paths.get("src/main/resources/static/images/" + imageName);
				byte[] imageData = Files.readAllBytes(path);

				Image image = new Image();
				image.setName(imageName);
				image.setData(imageData);
				image.setAdvert(advert);

				imagesRepository.save(image);
			} catch (IOException e) {
				System.err.println("Resim yüklenirken hata: " + e.getMessage());
			}
		}
	}
}
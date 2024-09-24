package com.project.service.helper;

import com.project.entity.concretes.business.*;
import com.project.entity.concretes.business.Image;
import com.project.entity.concretes.user.User;
import com.project.entity.concretes.user.UserRole;
import com.project.entity.enums.AdvertStatus;
import com.project.entity.enums.RoleType;
import com.project.exception.BadRequestException;
import com.project.exception.ConflictException;

import com.project.exception.ResourceNotFoundException;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.request.user.AuthenticatedUsersRequest;
import com.project.payload.request.user.CustomerRequest;
import com.project.payload.response.business.image.ImageResponse;
import com.project.repository.business.AdvertRepository;
import com.project.repository.business.FavoriteRepository;
import com.project.repository.user.UserRepository;
import com.project.service.user.UserRoleService;

import lombok.RequiredArgsConstructor;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.springframework.http.HttpHeaders;


import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class MethodHelper {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleService userRoleService;
    private final AdvertRepository advertRepository;


    /**
     * Kullanıcının var olup olmadığını kontrol eder. Bulunmazsa bir istisna fırlatır.
     */
    public User isUserExist(Long userId){
        return userRepository.findById(userId).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_MESSAGE, userId)));
    }

    /**
     * Kullanıcının builtIn alanını kontrol eder. Eğer true ise işlem yapmayı engeller.
     */
    public void checkBuiltIn(User user){
        if(Boolean.TRUE.equals(user.getBuiltIn())) {
            throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
        }
    }

    /**
     * Kullanıcının belirtilen rollerden birine sahip olup olmadığını kontrol eder.
     */
    public void controlRoles(User user, RoleType... roleTypes) {

        Set<RoleType> roles = new HashSet<>();
        Collections.addAll(roles, roleTypes);
        Set<UserRole> rolesUserRole = roles.stream().map(userRoleService::getUserRole).collect(Collectors.toSet());

        for (UserRole role : user.getUserRole()) {
            if (!(rolesUserRole.contains(role))) {
                throw new BadRequestException(ErrorMessages.NOT_HAVE_AUTHORITY);
            }
        }
    }

    /**
     * Kullanıcının kullanıcı adının var olup olmadığını kontrol eder.
     */
    public User isUserExistByUsername(String username){
        User user = userRepository.findByUsernameEquals(username);

        if(user == null){
            throw new ResourceNotFoundException(ErrorMessages.NOT_FOUND_USER_MESSAGE);
        }
        return user;
    }

    /**
     * Verilen ID'ye sahip bir kullanıcının veritabanında olup olmadığını kontrol eder.
     *
     * @param id Kontrol edilecek kullanıcı ID'si.
     * @return Kullanıcı var ise true, yok ise false döner.
     */
    public boolean isUserExistById(Long id) {
        return userRepository.existsById(id);
    }

    /**
     * Verilen ID'ye sahip kullanıcıyı bulur.
     * Bulunmazsa ResourceNotFoundException fırlatır.
     *
     * @param id Bulunacak kullanıcı ID'si.
     * @return Bulunan kullanıcı nesnesi.
     * @throws ResourceNotFoundException Eğer kullanıcı bulunamazsa.
     */
    public User findUserWithId(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.USER_IS_NOT_FOUND, id)));
    }

    /**
     * E-mail ile kullanıcıyı bulur. E-mail null veya boş olamaz.
     */
    public User findByUserByEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email can not be null or empty");
        }

        return userRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException(String.format(ErrorMessages.EMAIL_CANNOT_BE_NULL_OR_EMPTY, email)));
    }

    /**
     * HttpServletRequest ile e-mail alıp kullanıcıyı döner.
     */
    public User getUserByHttpRequest(HttpServletRequest request) {
        return findByUserByEmail(getEmailByRequest(request));
    }

    /**
     * HttpServletRequest'ten e-mail alır.
     */
    public String getEmailByRequest(HttpServletRequest request) {

        return (String) request.getAttribute("email");
    }

    /**
     * E-posta veya telefon numarasının başka bir kullanıcı tarafından kullanılıp kullanılmadığını kontrol eder.
     */
    public void checkDuplicate(String email, String phone) {
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException(String.format(ErrorMessages.THIS_EMAIL_IS_ALREADY_TAKEN, email));
        }
        if (userRepository.existsByPhone(phone)) {
            throw new ConflictException(String.format(ErrorMessages.THIS_PHONE_NUMBER_IS_ALREADY_TAKEN, phone));
        }
    }

    /**
     * Kullanıcının e-posta ve telefon bilgilerini değiştirmeden önce çakışmalar olup olmadığını kontrol eder.
     */
    public void checkUniqueProperties(User user, AuthenticatedUsersRequest request) {

        boolean changed = false;
        String changedEmail = "";
        String changedPhone = "";

        if (!user.getEmail().equalsIgnoreCase(request.getEmail())) {
            changed = true;
            changedEmail = request.getEmail();
        }

        if (!user.getPhone().equalsIgnoreCase(request.getPhone())) {
            changed = true;
            changedPhone = request.getEmail();
        }

        if (changed) {
            checkDuplicate(changedEmail, changedPhone);
        }


    }

    /**
     * Kullanıcının e-posta ve şifresini kontrol eder.
     */
    public void checkEmailAndPassword(User user, CustomerRequest request) {

        if (!user.getEmail().equals(request.getEmail())){
            throw new BadRequestException(String.format(ErrorMessages.EMAIL_IS_INCORRECT, request.getEmail()));
        }

        if (!passwordEncoder.matches(request.getPassword(),user.getPasswordHash())){
            throw new BadRequestException(ErrorMessages.PASSWORD_IS_NOT_CORRECT);
        }
    }

    /**
     * Kullanıcının belirtilen rollerden birine sahip olup olmadığını kontrol eder.
     */
    public void checkRoles(User user, RoleType... roleTypes) {

        Set<RoleType> roles = new HashSet<>();
        Collections.addAll(roles, roleTypes);

        for (UserRole userRole : user.getUserRole()) {
            if (roles.contains(userRole.getRole())) return;
        }
        throw new ResourceNotFoundException(ErrorMessages.ROLE_NOT_FOUND);
    }

    /**
     * Rol tipini string olarak alıp UserRole tipine çevirir.
     */
    public Set<UserRole> roleStringToUserRole(Set<String> request) {

        return request.stream().map(item -> userRoleService.getUserRole(RoleType.valueOf(item))).collect(Collectors.toSet());
    }

    /**
     * Şifrelerin eşleşip eşleşmediğini kontrol eder.
     */
    public void UpdatePasswordControl(String password, String reWritePassword) {
        if (!Objects.equals(password, reWritePassword)) {
            throw new BadRequestException(ErrorMessages.PASSWORDS_DID_NOT_MATCH);
        }
    }

    /**
     *Popülerlik puanı hesaplar. (3 * ilan tur talebi sayısı + ilan görüntülenme sayısı)
     */
    public int calculatePopularityPoint(int advertTourRequestListSize, int advertViewCount) {
        return (3 * advertTourRequestListSize) + advertViewCount;
    }

    /**
     * Fiyat kontrolü yapar. Geçersizse true döner.
     */
    public boolean priceControl(Double startPrice, Double endPrice) {
        if (startPrice < 0 || endPrice < startPrice || endPrice < 0) {
            return true;
        } else return false;
    }

    /**
     * İki listeyi tek bir haritaya dönüştürür.
     */
    public Map<Object, Object> mapTwoListToOneMap(List<Object> list1, List<Object> list2) {
        Map<Object, Object> resultMap = new LinkedHashMap<>();

        for (int i = 0; i < Math.min(list1.size(), list2.size()); i++) {
            resultMap.put(list1.get(i), list2.get(i));
        }

        return resultMap;
    }

    /**
     * Kullanıcıyı alıp rollerini kontrol eder ve döner.
     */
    public User getUserAndCheckRoles(HttpServletRequest request, String name) {
        User user = getUserByHttpRequest(request);
        checkRoles(user, RoleType.valueOf(name));
        return user;
    }

    /**
     * HttpServletRequest'ten kullanıcının ID'sini alır.
     */
    public static Long getUserIdFromRequest(HttpServletRequest httpServletRequest, UserRepository userRepository) {

        String email = (String) httpServletRequest.getAttribute("email");


        Optional<User> userOptional = userRepository.findByEmail(email);


        return userOptional.map(User::getId).orElse(null);
    }

    /**
     * Kullanıcının favori ilanını ekler.
     */
    public static void addFavorite(User user, Advert advert, FavoriteRepository favoritesRepository) {
        // Favori ilanın var olup olmadığını kontrol et
        boolean isFavorite = favoritesRepository.existsByUserIdAndAdvertId(user.getId(), advert.getId());

        // Eğer ilgili favori zaten yoksa, favori ekle
        if (!isFavorite) {
            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setAdvert(advert);
            favoritesRepository.save(favorite);
        }
    }

    /**
     * ID ile ilan var mı diye kontrol eder.
     */
    public Advert isAdvertExistById(Long id){
        return advertRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_ADVERT_WITH_ID_MESSAGE,id)));
    }

    /**
     *  İlan için dosyalardan image listesi oluşturur.
     */
    public List<Image> getImagesForAdvert(MultipartFile[] files, List<Image> currentImages) {
        // images işlem lojiklerini buraya yazın
        List<Image> imageList = new ArrayList<>();
        for (MultipartFile file : files) {
            Image image = new Image();
            // image.setData(file.getBytes()); // Dosya verisini ayarla
            imageList.add(image);
        }
        return imageList;
    }

    /**
     * Excel dosyası için bir satır oluşturur.
     */
    public void isRelatedToAdvertsOrTourRequest(User user) {

        if (user.getTourRequests().size() > 0 || user.getAdvert().size() > 0) {
            throw new BadRequestException(ErrorMessages.THE_USER_HAS_RELATED_RECORDS_WITH_ADVERTS_OR_TOUR_REQUESTS);
        }

    }

    /*--------------------------For Report---------------------------------------*/

    /**
     * Excel dosyası için bir satır oluşturur.
     *
     * @param sheet  Excel sayfası.
     * @param rowNum Oluşturulacak satır numarası.
     * @param style  Hücre stili (opsiyonel).
     * @param values Satırda yer alacak değerler.
     */
    private void createRow(Sheet sheet, int rowNum, CellStyle style, Object... values) {
        Row row = sheet.createRow(rowNum);
        for (int i = 0; i < values.length; i++) {

            Cell cell= row.createCell(i);
            cell.setCellValue(values[i].toString());
            if (style != null) {
                cell.setCellStyle(style);
            }

        }
    }

    /**
     * Excel başlık stili oluşturur.
     *
     * @param workbook Excel çalışma kitabı.
     * @return Başlık stili.
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }


    /**
     * Verilen listeye göre Excel dosyası oluşturur ve HTTP yanıtı olarak döner.
     *
     * @param list Excel dosyasına dönüştürülecek nesne listesi.
     * @param <T>  Liste içindeki nesne tipi.
     * @return Excel dosyasını içeren HTTP yanıtı.
     * @throws BadRequestException Eğer Excel dosyası oluşturulamazsa.
     */
    public <T> ResponseEntity<byte[]> excelResponse(List<T> list) {

        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("AdvertReport");
            int rowNum = 0;

            CellStyle headerStyle = createHeaderStyle(workbook);


            if (!list.isEmpty() && list.get(0) instanceof User) {
                createRow(sheet, rowNum++, headerStyle,"ID", "Name", "Last Name","Email","Phone");
                for (User fetchedUser : (List<User>) list) {

                    createRow(sheet, rowNum++,null, fetchedUser.getId(), fetchedUser.getFirstName(), fetchedUser.getLastName(),fetchedUser.getEmail(),fetchedUser.getPhone());
                }
            } else if (!list.isEmpty() && list.get(0) instanceof TourRequest) {
                createRow(sheet, rowNum++, headerStyle,"ID", "Name", "Last Name","Title");

                for (TourRequest tourRequest : (List<TourRequest>) list) {
                    createRow(sheet, rowNum++,null, tourRequest.getId(), tourRequest.getOwnerUser().getFirstName(), tourRequest.getOwnerUser().getLastName(), tourRequest.getAdvert().getTitle());
                }
            } else if (!list.isEmpty() && list.get(0) instanceof Advert) {
                createRow(sheet, rowNum++, headerStyle,"ID", "AdvertTitle", "Status","AdvertTypeTitle","CategoryTitle");

                for (Advert advert : (List<Advert>) list) {
                    createRow(sheet, rowNum++,null, advert.getId(), advert.getTitle(), advert.getStatus(), advert.getAdvertType().getTitle(), advert.getCategory().getTitle());
                }

            }
            else{
                throw new BadRequestException(ErrorMessages.EXCEL_COULD_NOT_BE_CREATED);
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            HttpHeaders headers =returnHeader();
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);

        } catch (IOException e) {
            throw new BadRequestException("ERROR");
        }
    }

    /**
     * Verilen sayfaya göre Excel dosyası oluşturur ve HTTP yanıtı olarak döner.
     *
     * @param list Excel dosyasına dönüştürülecek sayfalı nesne listesi.
     * @param <T>  Liste içindeki nesne tipi.
     * @return Excel dosyasını içeren HTTP yanıtı.
     * @throws BadRequestException Eğer Excel dosyası oluşturulamazsa veya tip Advert değilse.
     */
    public <T> ResponseEntity<byte[]> excelResponse(Page<T> list) {

        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("AdvertReport");
            int rowNum = 0;
            List<T> page = list.getContent();
            CellStyle headerStyle = createHeaderStyle(workbook);

            if (page.isEmpty() || !(page.get(0) instanceof Advert)){
                throw new BadRequestException(ErrorMessages.EXCEL_COULD_NOT_BE_CREATED_TYPE_IS_NOT_ADVERT);
            }
            createRow(sheet, rowNum++, headerStyle,"ID", "AdvertTitle", "Status","AdvertTypeTitle","CategoryTitle");

            for (Advert advert : (Page<Advert>) page) {
                createRow(sheet,rowNum++,null, advert.getId(),advert.getTitle(),advert.getStatus(),advert.getAdvertType().getTitle(),advert.getCategory().getTitle());
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            HttpHeaders headers =returnHeader();

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);


        } catch (BadRequestException | IOException err) {
            throw new BadRequestException("ERR");
        }

    }

    /**
     * Excel dosyası indirilirken gerekli HTTP başlıklarını oluşturur.
     *
     * @return Excel dosyası indirme için gerekli HTTP başlıkları.
     */
    private HttpHeaders returnHeader(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("report.xlsx")
                .build());
        headers.setCacheControl(CacheControl.noCache().mustRevalidate());
        return headers;
    }

    /*-------------------------Advert----------------------------------*/

    public int updateAdvertStatus(int caseNumber, Advert advert) {
        AdvertStatus status;
        switch (caseNumber) {
            case 0:
                status = AdvertStatus.PENDING;
                advert.setIsActive(false);
                System.out.println("Advert status set to PENDING. Advert is now inactive.");
                break;
            case 1:
                status = AdvertStatus.PENDING;
                advert.setIsActive(true);
                System.out.println("Advert status set to ACTIVATED. Advert is now active.");
                break;
            case 2:
                status = AdvertStatus.REJECTED;
                advert.setIsActive(false);
                System.out.println("Advert status set to REJECTED. Advert is inactive.");
                break;
            default:
                System.out.println("Invalid case number.");
                return AdvertStatus.PENDING.getValue();
        }
        return caseNumber;
    }

    public ImageResponse getFeaturedImage(List<Image> images) {
        if (images == null || images.isEmpty()) {
            return null;
        }
        // Featured image seçme mantığı: 'featured' flag'ine göre veya ilk resmi seç
        return images.stream()
                .filter(Image::getFeatured)
                .findFirst()
                .map(img -> ImageResponse.builder()
                        .id(img.getId())
                        .name(img.getName())
                        .type(img.getType())
                        .featured(img.getFeatured())
                        .build())
                .orElseGet(() -> {
                    Image firstImage = images.get(0);
                    return ImageResponse.builder()
                            .id(firstImage.getId())
                            .name(firstImage.getName())
                            .type(firstImage.getType())
                            .featured(firstImage.getFeatured())
                            .build();
                });
    }
}
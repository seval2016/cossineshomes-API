package com.project.service.helper;

import com.project.entity.concretes.business.*;
import com.project.entity.concretes.user.User;
import com.project.entity.concretes.user.UserRole;
import com.project.entity.enums.AdvertStatus;
import com.project.entity.enums.RoleType;
import com.project.exception.BadRequestException;
import com.project.exception.ConflictException;

import com.project.exception.ResourceNotFoundException;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.request.business.AdvertRequest;
import com.project.payload.request.user.AuthenticatedUsersRequest;
import com.project.payload.request.user.CustomerRequest;
import com.project.payload.response.business.CategoryResponse;
import com.project.payload.response.business.CityAdvertResponse;
import com.project.repository.business.AdvertRepository;
import com.project.repository.business.FavoriteRepository;
import com.project.repository.user.UserRepository;
import com.project.service.business.*;
import com.project.service.user.UserRoleService;

import lombok.RequiredArgsConstructor;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
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
    private final CategoryService categoryService;



    //!!! isUserExist
    public User isUserExist(Long userId){
        return userRepository.findById(userId).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_MESSAGE, userId)));
    }

    //!!! builtIn kontrolu
    public void checkBuiltIn(User user){
        if(Boolean.TRUE.equals(user.isBuiltIn())) {
            throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
        }
    }

    //!!! Rol kontrolu yapan metod
    public void checkRole(User user, RoleType role){
       if(!user.getUserRole()
               .stream()
               .anyMatch(userRole -> userRole.getRole().equals(role))){

            throw new ResourceNotFoundException(
                    String.format(ErrorMessages.NOT_FOUND_USER_WITH_ROLE_MESSAGE, user.getId(), role));
        }
    }

    //!!! username ile kontrol
    public User isUserExistByUsername(String username){
        User user = userRepository.findByUsernameEquals(username);

        if(user.getId() == null){
            throw new ResourceNotFoundException(ErrorMessages.NOT_FOUND_USER_MESSAGE);
        }

        return user;
    }
    public User findByUserByEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new ResourceNotFoundException("Email can not be null or empty");
        }

        return userRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_EMAIL, email)));
    }
        public User getUserByHttpRequest(HttpServletRequest request) {
        return findByUserByEmail(getEmailByRequest(request));

    }

    public String getEmailByRequest(HttpServletRequest request) {

        return (String) request.getAttribute("email");
    }

    public void checkDuplicate(String email, String phone) {

        if (userRepository.existsByEmail(email)) {
            throw new ConflictException(String.format(ErrorMessages.THIS_EMAIL_IS_ALREADY_TAKEN, email));
        }
        if (userRepository.existsByPhone(phone)) {
            throw new ConflictException(String.format(ErrorMessages.THIS_PHONE_NUMBER_IS_ALREADY_TAKEN, phone));

        }

    }


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

    public void checkEmailAndPassword(User user, CustomerRequest request) {

        if (!user.getEmail().equals(request.getEmail())){
            throw new BadRequestException(String.format(ErrorMessages.EMAIL_IS_INCORRECT, request.getEmail()));
        }

        if (!passwordEncoder.matches(request.getPassword(),user.getPasswordHash())){
            throw new BadRequestException(ErrorMessages.PASSWORD_IS_NOT_CORRECT);
        }
    }

    public User findUserWithId(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.USER_IS_NOT_FOUND, id)));
    }
    public void checkRoles(User user, RoleType... roleTypes) {

        Set<RoleType> roles = new HashSet<>();
        Collections.addAll(roles, roleTypes);

        for (UserRole userRole : user.getUserRole()) {
            if (roles.contains(userRole.getRole())) return;
        }
        throw new ResourceNotFoundException(ErrorMessages.ROLE_NOT_FOUND);
    }

    public Set<UserRole> roleStringToUserRole(Set<String> request) {

        return request.stream().map(item -> userRoleService.getUserRole(RoleType.valueOf(item))).collect(Collectors.toSet());
    }

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

    public void UpdatePasswordControl(String password, String reWritePassword) {
        if (!Objects.equals(password, reWritePassword)) {
            throw new BadRequestException(ErrorMessages.PASSWORDS_DID_NOT_MATCH);
        }
    }

    public int calculatePopularityPoint(int advertTourRequestListSize, int advertViewCount) {
        return (3 * advertTourRequestListSize) + advertViewCount;
    }

    public boolean priceControl(Double startPrice, Double endPrice) {
        if (startPrice < 0 || endPrice < startPrice || endPrice < 0) {
            return true;
        } else return false;
    }

    public Map<Object, Object> mapTwoListToOneMap(List<Object> list1, List<Object> list2) {
        Map<Object, Object> resultMap = new LinkedHashMap<>();

        for (int i = 0; i < Math.min(list1.size(), list2.size()); i++) {
            resultMap.put(list1.get(i), list2.get(i));
        }

        return resultMap;
    }

    public User getUserAndCheckRoles(HttpServletRequest request, String name) {
        User user = getUserByHttpRequest(request);
        checkRoles(user, RoleType.valueOf(name));
        return user;
    }

    public static Long getUserIdFromRequest(HttpServletRequest httpServletRequest, UserRepository userRepository) {

        String email = (String) httpServletRequest.getAttribute("email");


        Optional<User> userOptional = userRepository.findByEmail(email); // TODO Optional yerine throw new olabilir


        return userOptional.map(User::getId).orElse(null);
    }

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

    public int updateAdvertStatus(int caseNumber, Advert advert) {
        AdvertStatus status;
        switch (caseNumber) {
            case 0:
                status = AdvertStatus.PENDING;
                advert.setActive(false);
                System.out.println("Advert status set to PENDING. Advert is now inactive.");
                break;
            case 1:
                status = AdvertStatus.PENDING;
                advert.setActive(true);
                System.out.println("Advert status set to ACTIVATED. Advert is now active.");
                break;
            case 2:
                status = AdvertStatus.REJECTED;
                advert.setActive(false);
                System.out.println("Advert status set to REJECTED. Advert is inactive.");
                break;
            default:
                System.out.println("Invalid case number.");
                return AdvertStatus.PENDING.getValue();
        }
        return caseNumber;
    }

    public Advert isAdvertExistById(Long id){
        return advertRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_ADVERT_WITH_ID_MESSAGE,id)));
    }

    public List<Images> getImagesForAdvert(MultipartFile[] files, List<Images> currentImages) {
        // images işlem lojiklerini buraya yazın
        List<Images> imageList = new ArrayList<>();
        for (MultipartFile file : files) {
            Images image = new Images();
            // image.setData(file.getBytes()); // Dosya verisini ayarla
            imageList.add(image);
        }
        return imageList;
    }

    public List<CityAdvertResponse> getAdvertsGroupedByCities() {
        return advertRepository.findAdvertsGroupedByCities();
    }

    /*--------------------------For Report---------------------------------------*/

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
                    createRow(sheet, rowNum++,null, tourRequest.getId(), tourRequest.getOwnerUser().getFirstName(),tourRequest.getOwnerUser().getLastName(), tourRequest.getAdvert().getTitle());
                }
            } else if (!list.isEmpty() && list.get(0) instanceof Advert) {
                //TODO hem advert hemde advertType title var ikisinide gerek var mi?
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

    private HttpHeaders returnHeader(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("report.xlsx")
                .build());
        headers.setCacheControl(CacheControl.noCache().mustRevalidate());
        return headers;
    }

    /*-----------------------------------------------------------*/
}
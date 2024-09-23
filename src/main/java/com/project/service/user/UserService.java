package com.project.service.user;

import com.project.entity.concretes.business.Advert;
import com.project.entity.concretes.user.User;
import com.project.entity.concretes.user.UserRole;
import com.project.entity.enums.Log;
import com.project.entity.enums.RoleType;
import com.project.exception.BadRequestException;
import com.project.exception.MailServiceException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.UserMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.business.ForgotPasswordRequest;
import com.project.payload.request.business.UpdatePasswordRequest;
import com.project.payload.request.user.*;
import com.project.payload.response.UserResponse;
import com.project.payload.response.abstracts.BaseUserResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.payload.response.user.RegisterResponse;
import com.project.repository.user.UserRepository;
import com.project.service.business.LogService;
import com.project.service.email.EmailService;
import com.project.service.helper.MethodHelper;
import com.project.service.helper.PageableHelper;
import com.project.service.validator.UniquePropertyValidator;
import com.project.utils.MailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final MethodHelper methodHelper;
    private final UserRepository userRepository;
    private final UniquePropertyValidator uniquePropertyValidator;
    private final UserMapper userMapper;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;
    private final PageableHelper pageableHelper;
    private final LogService logService;

    /**
     * Bu metod, verilen kullanıcı ID'sine göre kullanıcıyı bulur
     * ve kullanıcı bilgilerini BaseUserResponse objesine dönüştürerek döner.
     *
     * @param userId Kullanıcının ID'si.
     * @return BaseUserResponse Kullanıcı bilgilerini içeren yanıt.
     */
    public ResponseMessage<BaseUserResponse> getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_MESSAGE, userId)));

        BaseUserResponse baseUserResponse;
        if (user.getUserRole().stream().anyMatch(role -> role.getRole() == RoleType.CUSTOMER)) {
            baseUserResponse = userMapper.mapUserToCustomerResponse(user);
        } else {
            baseUserResponse = userMapper.mapUserToUserResponse(user);
        }

        return ResponseMessage.<BaseUserResponse>builder()
                .message(SuccessMessages.USER_FOUND)
                .httpStatus(HttpStatus.OK)
                .object(baseUserResponse)
                .build();
    }

    /**
     * Bu metod, sayfa numarası, boyutu, sıralama ve rol türüne göre
     * kullanıcıları sayfalandırarak getirir.
     *
     * @param page Sayfa numarası.
     * @param size Sayfa boyutu.
     * @param sort Sıralama kriteri.
     * @param type Kullanıcı tipi.
     * @param userRole Kullanıcı rolü.
     * @return Page<UserResponse> Kullanıcı bilgilerini içeren sayfa.
     */
    public Page<UserResponse> getUsersByPage(int page, int size, String sort, String type, String userRole) {
        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);
        return userRepository.findByUserByRole(userRole, pageable)
                .map(userMapper::mapUserToUserResponse);
    }

    /**
     * Bu metod, verilen ID'ye sahip kullanıcıyı siler. Kullanıcının
     * mevcut durumu ve yetkilerine bağlı olarak silme işlemi gerçekleştirilir.
     *
     * @param id Silinecek kullanıcının ID'si.
     * @param request HTTP isteği, kimlik doğrulama için kullanılır.
     * @return String Başarılı silme mesajı.
     */
    public String deleteUserById(Long id, HttpServletRequest request) {
        User user = methodHelper.isUserExist(id);
        String userName = (String) request.getAttribute("username");
        User user2 = userRepository.findByUsernameEquals(userName);

        if (Boolean.TRUE.equals(user.getBuiltIn())) {
            throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
        } else if (user2.getUserRole().stream().anyMatch(role -> role.getRole() == RoleType.MANAGER)) {
            if (!user.getUserRole().stream().anyMatch(role -> role.getRole() == RoleType.CUSTOMER)) {
                throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
            }
        }

        userRepository.deleteById(id);
        return SuccessMessages.USER_DELETED;
    }

    /**
     * Bu metod, verilen kullanıcı bilgileri ile kullanıcıyı günceller.
     * Kullanıcı güncelleme öncesi gerekli kontroller yapılır.
     *
     * @param userRequest Güncellenmiş kullanıcı bilgilerini içeren istek.
     * @param userId Güncellenmesi gereken kullanıcının ID'si.
     * @return ResponseMessage<BaseUserResponse> Güncellenen kullanıcı bilgileri.
     */
    public ResponseMessage<BaseUserResponse> updateUser(UserRequest userRequest, Long userId) {
        User user = methodHelper.isUserExist(userId);
        methodHelper.checkBuiltIn(user);
        uniquePropertyValidator.checkUniqueProperties(user, userRequest);

        User updatedUser = userMapper.mapUserRequestToUpdatedUser(userRequest, userId);
        updatedUser.setPasswordHash(passwordEncoder.encode(userRequest.getPasswordHash()));
        updatedUser.setUserRole(user.getUserRole());
        User savedUser = userRepository.save(updatedUser);

        return ResponseMessage.<BaseUserResponse>builder()
                .message(SuccessMessages.USER_UPDATE_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .object(userMapper.mapUserToUserResponse(savedUser))
                .build();
    }

    /**
     * Bu metod, kimlik doğrulama yapmış kullanıcıyı günceller.
     * Şifre hariç diğer bilgileri günceller.
     *
     * @param userRequestWithoutPassword Kullanıcının güncellenmiş bilgilerini içeren istek.
     * @param request HTTP isteği, kimlik doğrulama için kullanılır.
     * @return ResponseEntity<String> Başarılı güncelleme mesajı.
     */
    public ResponseEntity<String> updateAuthenticatedUser(UserRequestWithoutPassword userRequestWithoutPassword, HttpServletRequest request) {
        String userName = (String) request.getAttribute("username");
        User user = userRepository.findByUsernameEquals(userName);

        methodHelper.checkBuiltIn(user);
        uniquePropertyValidator.checkUniqueProperties(user, userRequestWithoutPassword);

        //user.setUsername(userRequestWithoutPassword);
        user.setFirstName(userRequestWithoutPassword.getFirstName());
        user.setLastName(userRequestWithoutPassword.getLastName());
        user.setEmail(userRequestWithoutPassword.getEmail());
        user.setPhone(userRequestWithoutPassword.getPhone());

        userRepository.save(user);

        return ResponseEntity.ok(SuccessMessages.USER_UPDATED);
    }

    /**
     * Bu metod, verilen isimle eşleşen kullanıcıları getirir.
     *
     * @param name Kullanıcı adının bir kısmını içeren isim.
     * @return List<UserResponse> İlgili kullanıcıları içeren liste.
     */
    public List<UserResponse> getUserByName(String name) {
        return userRepository.getUserByFirstNameContaining(name)
                .stream()
                .map(userMapper::mapUserToUserResponse)
                .collect(Collectors.toList());
    }

    /**
     * Bu metod, sisteme kayıtlı tüm admin kullanıcılarının sayısını döner.
     *
     * @return long Admin kullanıcı sayısı.
     */
    public long countAllAdmins() {
        return userRepository.countAdmin(RoleType.ADMIN);
    }


    /**
     * Bu metod, verilen kullanıcı adını kullanarak müşteri bilgilerini getirir.
     *
     * @param customerUsername Müşterinin kullanıcı adı.
     * @return User Müşteri bilgilerini içeren kullanıcı objesi.
     */
    public User getCustomerByUsername(String customerUsername) {
        return userRepository.findByUsername(customerUsername);
    }

    /**
     * Bu metod, verilen kullanıcı ID'sine göre kullanıcı bilgilerini getirir.
     *
     * @param userId Kullanıcının ID'si.
     * @return User Kullanıcı bilgilerini içeren kullanıcı objesi.
     */
    public User getUserByUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException(ErrorMessages.NOT_FOUND_USER_MESSAGE));
    }

    /**
     * Bu metod, verilen müşteri ID'lerine göre müşterileri getirir.
     *
     * @param customerIds Müşteri ID'lerinin dizisi.
     * @return List<User> Müşteri bilgilerini içeren kullanıcı listesi.
     */
    public List<User> getCustomerById(Long[] customerIds) {
        return userRepository.findByIdsEquals(customerIds);
    }

    /**
     * Bu metod, kimlik doğrulama yapmış kullanıcının şifresini günceller.
     *
     * @param passwordUpdateRequest Güncellenmiş şifre bilgilerini içeren istek.
     * @param request HTTP isteği, kimlik doğrulama için kullanılır.
     */
    public void updateAuthenticatedUserPassword(UpdatePasswordRequest passwordUpdateRequest, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        User user = userRepository.findByUsernameEquals(username);
        user.setPasswordHash(passwordEncoder.encode(passwordUpdateRequest.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * Bu metod, verilen rol tipine göre kullanıcıları getirir.
     *
     * @param roleType Kullanıcı rolü.
     * @return List<User> Belirtilen rol tipine sahip kullanıcılar.
     */
    public List<User> getUsersByRoleType(RoleType roleType) {
        return userRepository.findByUserRole_Role(roleType);
    }

    /**
     * Bu metod, kullanıcı adını kullanarak bir kullanıcıyı bulur ve
     * UserResponse objesine dönüştürür.
     *
     * @param username Kullanıcı adı.
     * @return UserResponse Kullanıcının bilgilerini döner.
     */
    public UserResponse findByUsername(String username) {
        User user = userRepository.findByUsername(username);
        return userMapper.mapUserToUserResponse(user);
    }

    /**
     * Bu metod, kimlik doğrulama yapmış kullanıcının şifresini günceller.
     *
     * @param updatePasswordRequest Güncellenmiş şifre bilgilerini içeren istek.
     * @param request HTTP isteği, kimlik doğrulama için kullanılır.
     */
    public void updatePassword(UpdatePasswordRequest updatePasswordRequest, HttpServletRequest request) {
        String userName = (String) request.getAttribute("username");
        User user = userRepository.findByUsername(userName);

        if (Boolean.TRUE.equals(user.getBuiltIn())) {
            throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
        }

        if (!passwordEncoder.matches(updatePasswordRequest.getOldPassword(), user.getPasswordHash())) {
            throw new BadRequestException(ErrorMessages.PASSWORD_NOT_MATCHED);
        }

        user.setPasswordHash(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * Bu metod, yeni bir kullanıcı kaydı yapar ve döner.
     *
     * @param request Kullanıcı bilgilerini içeren istek.
     * @return ResponseEntity<UserResponse> Yeni kullanıcının bilgilerini içeren yanıt.
     */
    public ResponseEntity<UserResponse> saveUserWithoutRequest(UserSaveRequest request) {
        methodHelper.checkDuplicate(request.getEmail(), request.getPhone());
        User savedUser = userMapper.userRequestToUser(request);
        savedUser.setPasswordHash(passwordEncoder.encode(request.getPasswordHash()));
        List<UserRole> userRolesSaved = new ArrayList<>();
        userRolesSaved.add(userRoleService.getUserRole(RoleType.ADMIN));
        savedUser.setUserRole(userRolesSaved);
        userRepository.save(savedUser);

        return ResponseEntity.ok(userMapper.mapUserToUserResponse(savedUser));
    }

    //F08
    public ResponseEntity<String> deleteAuthenticatedUser(CustomerRequest customerRequest,HttpServletRequest request) {
        User user = methodHelper.getUserByHttpRequest(request);

        if (Boolean.TRUE.equals(user.getBuiltIn())) {
            throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
        }
        methodHelper.checkRoles(user, RoleType.CUSTOMER);
        methodHelper.isRelatedToAdvertsOrTourRequest(user);

        if(customerRequest.getEmail()==null){
            throw new BadRequestException("EMAIL IS NULL");
        }
        methodHelper.checkEmailAndPassword(user, customerRequest);
        userRepository.delete(user);

        for (Advert advert : user.getAdvert()) {
            logService.createLogEvent(user, advert, Log.DELETED);
        }

        return  ResponseEntity.ok(SuccessMessages.CUSTOMER_DELETED);

    }
}

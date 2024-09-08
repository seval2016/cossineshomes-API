package com.project.service.helper;

import com.project.entity.concretes.business.Advert;
import com.project.entity.concretes.business.CategoryPropertyValue;
import com.project.entity.concretes.business.Favorite;
import com.project.entity.concretes.user.User;
import com.project.entity.concretes.user.UserRole;
import com.project.entity.enums.RoleType;
import com.project.exception.BadRequestException;
import com.project.exception.ConflictException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.request.user.AuthenticatedUsersRequest;
import com.project.payload.request.user.CustomerRequest;
import com.project.repository.business.FavoriteRepository;
import com.project.repository.user.UserRepository;
import com.project.service.business.CategoryPropertyValueService;
import com.project.service.user.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MethodHelper {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleService userRoleService;

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

}
package com.project.controller.user;

import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.business.UpdatePasswordRequest;
import com.project.payload.request.user.CustomerRequest;
import com.project.payload.request.user.UserRequest;
import com.project.payload.request.user.UserRequestWithoutPassword;
import com.project.payload.response.ResponseMessage;
import com.project.payload.response.UserResponse;
import com.project.payload.response.abstracts.BaseUserResponse;
import com.project.service.AuthenticationService;
import com.project.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    /**
     * F05 - Authenticate olmuş kullanıcının bilgilerini döndürmek için kullanılır.
     * http://localhost:8080/users/auth + GET
     * @param request HTTP isteği.
     * @return Authenticate olmuş kullanıcının bilgilerini içeren ResponseEntity.
     */
    @GetMapping("/auth")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CUSTOMER')")
    public ResponseEntity<BaseUserResponse> getAuthenticatedUser(HttpServletRequest request) {
        return ResponseEntity.ok(authenticationService.getCurrentAuthenticatedUser(request));
    }

    /**
     * F06 - Authenticate olmuş kullanıcı bilgilerini güncellemek için kullanılır.
     * http://localhost:8080/users/auth + PUT + JSON
     * @param userRequestWithoutPassword Güncelleme bilgilerini içeren UserRequestWithoutPassword nesnesi.
     * @param request HTTP isteği.
     * @return Güncelleme işleminin sonucunu içeren ResponseEntity.
     */
    @PutMapping("/auth")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CUSTOMER')")
    public ResponseEntity<String> updateAuthenticatedUser(@RequestBody @Valid UserRequestWithoutPassword userRequestWithoutPassword, HttpServletRequest request) {
        userService.updateAuthenticatedUser(userRequestWithoutPassword, request);
        return ResponseEntity.ok(SuccessMessages.USER_UPDATED);
    }

    /**
     * F07 - Authenticate olmuş kullanıcının şifresini güncellemek için kullanılır.
     * http://localhost:8080/users/auth + PATCH + JSON
     * @param passwordUpdateRequest Şifre güncelleme bilgilerini içeren UpdatePasswordRequest nesnesi.
     * @param request HTTP isteği.
     * @return Şifre güncelleme işleminin sonucunu içeren ResponseEntity.
     */
    @PatchMapping("/auth") //
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CUSTOMER')")
    public ResponseEntity<String> updateAuthenticatedUserPassword(@RequestBody @Valid UpdatePasswordRequest passwordUpdateRequest, HttpServletRequest request) {
        userService.updateAuthenticatedUserPassword(passwordUpdateRequest, request);
        return ResponseEntity.ok(SuccessMessages.PASSWORD_CHANGED_RESPONSE_MESSAGE);
    }

    /**
     * F08 - Authenticate olmuş kullanıcıyı silmek için kullanılır.
     * http://localhost:8080/users/auth + PATCH + JSON
     * @param request HTTP isteği.
     * @param customerRequest Silme işlemi için gereken bilgileri içeren JSON payload.
     * @return Silme işleminin sonucunu içeren ResponseEntity.
     */
    @PatchMapping("/auth/customer") //
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<String> deleteAuthenticatedUser(@RequestBody @Valid CustomerRequest customerRequest,HttpServletRequest request) {
        return userService.deleteAuthenticatedUser(customerRequest,request);
    }

    /**
     * F09 - Belirli bir rol için sayfalanmış kullanıcıları getirmek için kullanılır.
     * http://localhost:8080/users/getAllUserByPage/Admin
     * @param userRole Kullanıcı rolü.
     * @param page     Sayfa numarası.
     * @param size     Sayfa boyutu.
     * @param sort     Sıralama kriteri.
     * @param type     Sıralama tipi (asc/desc).
     * @return Sayfalanmış kullanıcı listesini içeren ResponseEntity.
     */
    @GetMapping("/getAllUserByPage/{userRole}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<Page<UserResponse>> getUserByPage(
            @PathVariable String userRole,
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "firstName") String sort,
            @RequestParam(value = "type",defaultValue = "desc") String type
    ){
        return ResponseEntity.ok(userService.getUsersByPage(page,size,sort,type,userRole));
    }

    /**
     * F10 - ID'ye göre bir kullanıcıyı getirmek için kullanılır.
     * http://localhost:8080/users/1 + GET
     * @param userId Kullanıcı ID'si.
     * @return Belirtilen ID'ye sahip kullanıcıyı içeren ResponseMessage.
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseMessage<BaseUserResponse> getUserById(@PathVariable Long userId){
        return userService.getUserById(userId);
    }

    /**
     * F11 - Admin tarafından, belirli bir kullanıcıyı güncellemek için kullanılır.
     * http://localhost:8080/users/update/1 + PUT + JSON
     * @param userRequest Güncelleme bilgilerini içeren UserRequest nesnesi.
     * @param userId     Güncellenecek kullanıcının ID'si.
     * @return Güncellenmiş kullanıcı bilgilerini içeren ResponseMessage.
     */
    @PutMapping("/update/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseMessage<BaseUserResponse> updateAdminManagerForAdmin( @RequestBody @Valid UserRequest userRequest, @PathVariable Long userId){
        return userService.updateUser(userRequest,userId);
    }

    /**
     * F12 - Belirli bir ID'ye sahip kullanıcıyı silmek için kullanılır.
     * http://localhost:8080/users/delete/3
     * @param id Kullanıcı ID'si.
     * @param Request HTTP isteği.
     * @return Silme işleminin sonucunu içeren ResponseEntity.
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id, HttpServletRequest Request){
        return ResponseEntity.ok(userService.deleteUserById(id, Request));
    }

    /**
     * Kullanıcı adını içerir şekilde kullanıcıları getirmek için kullanılır.
     * http://localhost:8080/users?name=user1  + GET
     * @param userName Kullanıcı adı.
     * @return Kullanıcı adını içeren kullanıcı listesini döndürür.
     */
    @GetMapping("/")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CUSTOMER')")
    public List<UserResponse> getUserByName(@RequestParam (name = "name") String userName){
        return userService.getUserByName(userName);
    }
}

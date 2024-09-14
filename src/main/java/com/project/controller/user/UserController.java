package com.project.controller.user;

import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.business.UpdatePasswordRequest;
import com.project.payload.request.user.RegisterRequest;
import com.project.payload.request.user.UserRequest;
import com.project.payload.request.user.UserRequestWithoutPassword;
import com.project.payload.response.UserResponse;
import com.project.payload.response.abstracts.BaseUserResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.payload.response.user.RegisterResponse;
import com.project.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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

    //!!! Yeni bir kullanıcı kaydetmek için kullanılır.
    @PostMapping("/register/{userRole}") // http://localhost:8080/users/register/Admin + POST + JSON
    public ResponseMessage<RegisterResponse> registerUser(@Valid @RequestBody RegisterRequest RegisterRequest){
        // Kullanıcıyı rolüne göre kaydeder.
        return userService.registerUser(RegisterRequest);
    } //F02


    //!!! Belirli bir rol için sayfalanmış kullanıcıları getirmek için kullanılır.
    @GetMapping("/getAllUserByPage/{userRole}") // http://localhost:8080/users/getAllUserByPage/Admin
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<Page<UserResponse>> getUserByPage(
            @PathVariable String userRole,
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "firstName") String sort,
            @RequestParam(value = "type",defaultValue = "desc") String type
    ){
        // Belirtilen rol için sayfalanmış kullanıcı listesini döndürür.
        Page<UserResponse> adminsOrManager = userService.getUsersByPage(page,size,sort,type,userRole);
        return new ResponseEntity<>(adminsOrManager, HttpStatus.OK);
    } //F09

    //!!! ID'ye göre bir kullanıcıyı getirmek için kullanılır.
    @GetMapping("/{userId}") // http://localhost:8080/users/1 + GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseMessage<BaseUserResponse> getUserById(@PathVariable Long userId){
        // Belirtilen ID'ye sahip kullanıcıyı döndürür.
        return userService.getUserById(userId);
    } //F10

    // !!! Admin tarafından, belirli bir kullanıcıyı güncellemek için kullanılır.
    @PutMapping("/update/{userId}")  // http://localhost:8080/users/update/1 + PUT + JSON
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseMessage<BaseUserResponse> updateAdminManagerForAdmin( @RequestBody @Valid UserRequest userRequest, @PathVariable Long userId){
        // Kullanıcıyı günceller ve güncellenmiş kullanıcı bilgilerini döndürür.
        return userService.updateUser(userRequest,userId);
    } //F11

    // !!! Belirli bir ID'ye sahip kullanıcıyı silmek için kullanılır.
    @DeleteMapping("/delete/{id}") // http://localhost:8080/users/delete/3
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id, HttpServletRequest Request){
        // Kullanıcıyı ID'ye göre siler ve sonucu döndürür.
        return ResponseEntity.ok(userService.deleteUserById(id, Request));
    } //F12

    //!!! Kullanıcı adını içerir şekilde kullanıcıları getirmek için kullanılır.
    @GetMapping("/") // http://localhost:8080/users?name=user1  + GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CUSTOMER')")
    public List<UserResponse> getUserByName(@RequestParam (name = "name") String userName){
        // Kullanıcı adını içerir şekilde kullanıcı listesini döndürür.
        return userService.getUserByName(userName);
    }

    //!!! Authenticate olmuş kullanıcının bilgilerini döndürmek için kullanılır.
    @GetMapping("/auth") // http://localhost:8080/users/auth + GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CUSTOMER')")
    public ResponseEntity<BaseUserResponse> getAuthenticatedUser(HttpServletRequest request) {
        // Authenticate olmuş kullanıcının bilgilerini döndürür.
        return ResponseEntity.ok(userService.getAuthenticatedUser(request));
    } //f05

    // !!! Authenticate olmuş kullanıcı bilgilerini güncellemek için kullanılır.
    @PutMapping("/auth") // http://localhost:8080/users/auth + PUT + JSON
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CUSTOMER')")
    public ResponseEntity<String> updateAuthenticatedUser(@RequestBody @Valid UserRequestWithoutPassword userRequestWithoutPassword, HttpServletRequest request) {
        // Authenticate olmuş kullanıcı bilgilerini günceller.
        userService.updateAuthenticatedUser(userRequestWithoutPassword, request);
        return ResponseEntity.ok(SuccessMessages.USER_UPDATED);
    }//F06

    //!!! Authenticate olmuş kullanıcının şifresini güncellemek için kullanılır.
    @PatchMapping("/auth") // http://localhost:8080/users/auth + PATCH + JSON
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CUSTOMER')")
    public ResponseEntity<String> updateAuthenticatedUserPassword(@RequestBody @Valid UpdatePasswordRequest passwordUpdateRequest, HttpServletRequest request) {
        // Authenticate olmuş kullanıcının şifresini günceller.
        userService.updateAuthenticatedUserPassword(passwordUpdateRequest, request);
        return ResponseEntity.ok(SuccessMessages.PASSWORD_CHANGED_RESPONSE_MESSAGE);
    } //F07

}

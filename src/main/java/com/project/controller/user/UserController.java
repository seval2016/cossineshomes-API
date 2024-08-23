package com.project.controller.user;

import com.project.payload.request.user.UserRequest;
import com.project.payload.request.user.UserRequestWithoutPassword;
import com.project.payload.response.UserResponse;
import com.project.payload.response.abstracts.BaseUserResponse;
import com.project.payload.response.business.ResponseMessage;
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
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //!!! Save --> Customer disindakiler icin
    @PostMapping("/save/{userRole}") // http://localhost:8080/users/save/Admin + POST + JSON
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ResponseMessage<UserResponse>> saveUser(@Valid @RequestBody UserRequest userRequest,
                                                                  @PathVariable String userRole){
        return ResponseEntity.ok(userService.saveUser(userRequest,userRole));
    }

    //!!! getall --> Admin,Manager
    @GetMapping("/getAllUserByPage/{userRole}") // http://localhost:8080/users/getAllUserByPage/Admin
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getUserByPage(
            @PathVariable String userRole,
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "name") String sort,
            @RequestParam(value = "type",defaultValue = "desc") String type
    ){
        Page<UserResponse> adminsOrManager = userService.getUsersByPage(page,size,sort,type,userRole);
        return new ResponseEntity<>(adminsOrManager, HttpStatus.OK);
    }

    //!!! getUserById
    @GetMapping("/getUserById/{userId}") // http://localhost:8080/users/getUserById/1 + GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseMessage<BaseUserResponse> getUserById(@PathVariable Long userId){
        return userService.getUserById(userId);
    }

    // !!!  deleteUser()
    // !!! Admin hepsini silebilsin
    // !!! Mudur sadece customer silebilsin
    @DeleteMapping("/delete/{id}") // http://localhost:8080/user/delete/3
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id, HttpServletRequest httpServletRequest){

        return ResponseEntity.ok(userService.deleteUserById(id, httpServletRequest));
    }


    // Update
    // !!! Admin -->Manager'ı guncellerken kullanilacağı method
    // !!! Customer icin ekstra fieldlar gerekebileceği icin, baska endpoint gerekiyor
    @PutMapping("/update/{userId}")  // http://localhost:8080/users/update/1 + PUT + JSON
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<BaseUserResponse> updateAdminManagerForAdmin( @RequestBody @Valid UserRequest userRequest, @PathVariable Long userId){
        return userService.updateUser(userRequest,userId);
    }

    // Update
    // !!! Kullanicinin kendisini update edeceği zaman bu method tetiklenir.
    @PatchMapping("/updateUser") // http://localhost:8080/users/updateUser + PATCH + JSON
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CUSTOMER')")
    public ResponseEntity<String> updateUser(@RequestBody @Valid UserRequestWithoutPassword userRequestWithoutPassword, HttpServletRequest request){ //HttpServletRequest request ile bu methodu tetikleyen kullanıcıya ulaşıyoruz.
        return userService.updateUserForUsers(userRequestWithoutPassword, request);
    }

    //!!! getByName
    @GetMapping("/getUserByName") // http://localhost:8080/users/getUserByName?name=user1  + GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CUSTOMER')")
    public List<UserResponse> getUserByName(@RequestParam (name = "name") String userName){
        return userService.getUserByName(userName);
    }
}

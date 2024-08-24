package com.project.service.helper;

import com.project.entity.concretes.user.User;
import com.project.entity.enums.Role;
import com.project.exception.BadRequestException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.messages.ErrorMessages;
import com.project.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MethodHelper {

    private final UserRepository userRepository;

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
    public void checkRole(User user, Role role){
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

}
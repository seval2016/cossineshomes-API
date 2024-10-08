package com.project.service.validator;

import com.project.entity.concretes.user.User;
import com.project.exception.ConflictException;
import com.project.payload.messages.ErrorMessages;
import com.project.repository.user.UserRepository;
import com.project.payload.request.abstracts.AbstractUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniquePropertyValidator {

    private final UserRepository userRepository;

    public void checkUniqueProperties(User user, AbstractUserRequest abstractUserRequest){
        String updatedUsername="";
        String updatedPhone="";
        String updatedEmail="";
        boolean isChanged= false;


        if( ! user.getPhone().equalsIgnoreCase(abstractUserRequest.getPhone())){
            updatedPhone = abstractUserRequest.getPhone();
            isChanged = true;
        }

        if(! user.getEmail().equalsIgnoreCase(abstractUserRequest.getEmail())){
            updatedEmail = abstractUserRequest.getEmail();
            isChanged = true;
        }

        if(isChanged){
            checkDuplicate(updatedUsername,updatedPhone, updatedEmail);
        }
    }

    public void checkDuplicate(String username, String phone, String email){

        if(userRepository.existsByUsername(username)){
            throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_MESSAGE_USERNAME, username));
        }

        if(userRepository.existsByPhone(phone)){
            throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_MESSAGE_PHONE, phone));
        }
        if(userRepository.existsByEmail(email)){
            throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_MESSAGE_EMAIL, email));
        }

    }
}

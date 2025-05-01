package com.ifellow.bookstore.validator;

import com.ifellow.bookstore.dto.request.RegistrationUserDto;
import org.springframework.stereotype.Component;

@Component
public class PasswordMatchesValidator {

    public boolean isValid(RegistrationUserDto registrationUserDto) {
        return registrationUserDto.getPassword().equals(registrationUserDto.getConfirmPassword());
    }

}

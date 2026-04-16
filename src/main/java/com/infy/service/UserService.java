package com.infy.service;

import com.infy.dto.UserRegistrationDto;
import com.infy.entity.User;

public interface UserService {
    User register(UserRegistrationDto dto);
}

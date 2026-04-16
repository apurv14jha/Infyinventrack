package com.infy.service.impl;

import com.infy.dto.UserRegistrationDto;
import com.infy.entity.User;
import com.infy.enums.InfyInvenTrackConstants;
import com.infy.exception.ConflictException;
import com.infy.repository.UserRepository;
import com.infy.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User register(UserRegistrationDto dto) {
        if (userRepository.existsByUserNameIgnoreCase(dto.getUserName())) {
            throw new ConflictException(String.format(InfyInvenTrackConstants.INVALID_ATTRIBUTE.value(), "userName"));
        }
        if (userRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new ConflictException(String.format(InfyInvenTrackConstants.INVALID_ATTRIBUTE.value(), "email"));
        }
        User user = new User();
        user.setUserName(dto.getUserName());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        return userRepository.save(user);
    }
}

package com.infy.util;

import com.infy.entity.User;
import com.infy.enums.InfyInvenTrackConstants;
import com.infy.enums.UserRole;
import com.infy.exception.UnauthorizedException;
import com.infy.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RoleValidator {

    private final UserRepository userRepository;

    public RoleValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateRole(Integer userId, UserRole requestedRole, Set<UserRole> allowedRoles) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UnauthorizedException(InfyInvenTrackConstants.UNAUTHORIZED.value()));

        if (!allowedRoles.contains(requestedRole) || !user.getRole().equals(requestedRole)) {
            throw new UnauthorizedException(InfyInvenTrackConstants.UNAUTHORIZED.value());
        }
    }
}

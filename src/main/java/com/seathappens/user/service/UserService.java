package com.seathappens.user.service;

import com.seathappens.common.exception.BusinessException;
import com.seathappens.common.exception.ErrorCode;
import com.seathappens.common.exception.ResourceNotFoundException;
import com.seathappens.security.service.TokenStoreService;
import com.seathappens.user.entity.User;
import com.seathappens.user.entity.UserStatus;
import com.seathappens.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TokenStoreService tokenStoreService;

    @Transactional
    public void deactivateUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));

        if (UserStatus.INACTIVE.equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.USER_ALREADY_INACTIVE);
        }

        user.setStatus(UserStatus.INACTIVE);
        tokenStoreService.revokeTokensByUserId(user.getId());
    }

}

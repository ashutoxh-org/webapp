package com.csye6225.cloud.service;

import com.csye6225.cloud.dto.CreateUserRequestDTO;
import com.csye6225.cloud.dto.EmailVerificationResponseDTO;
import com.csye6225.cloud.dto.UpdateUserRequestDTO;
import com.csye6225.cloud.dto.UserResponseDTO;
import com.csye6225.cloud.exception.CustomException;
import com.csye6225.cloud.exception.EmailVerificationException;
import com.csye6225.cloud.model.User;
import com.csye6225.cloud.repository.UserRepository;
import com.csye6225.cloud.util.Util;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

/**
 * The type User service.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PublisherService publisherService;

    /**
     * Get requesting user's details
     *
     * @return UserResponseDTO user
     */
    public UserResponseDTO getUser() {
        LOGGER.info("Get user called");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        LOGGER.debug("Attempting to get user {}", email);
        Optional<User> user = userRepository.findByEmail(email);
        if(validateUserVerification(user.get())) {
            LOGGER.debug("User {} fetched", user.get().getEmail());
            return getUserResponseFromUser(user.get());
        }
        return null;
    }

    /**
     * Create a user
     *
     * @param createUserRequestDTO obj
     * @return UserResponseDTO user response dto
     */
    public UserResponseDTO createUser(CreateUserRequestDTO createUserRequestDTO) {
        LOGGER.info("Create user called");
        LOGGER.debug("Attempting to create user {}", createUserRequestDTO.getEmail());
        User user = getUserFromCreateRequest(createUserRequestDTO);
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            LOGGER.warn("User {} already exists", existingUser.get().getEmail());
            throw new CustomException("User " + existingUser.get().getEmail() + " already exists");
        }
        user = userRepository.save(user);
        publisherService.prepareToPublish(user.getEmail());
        LOGGER.debug("User {} created", user.getEmail());
        return getUserResponseFromUser(user);
    }


    /**
     * Update user.
     *
     * @param updateUserRequestDTO the update user request dto
     */
    public void updateUser(UpdateUserRequestDTO updateUserRequestDTO) {
        LOGGER.info("Update user called");
        User updatedUser = getUserFromUpdateRequest(updateUserRequestDTO);
        LOGGER.debug("Attempting to update user {}", updatedUser.getEmail());
        Optional<User> user = userRepository.findByEmail(updatedUser.getEmail());
        if(validateUserVerification(user.get())) {
            user.get().setPassword(updatedUser.getPassword());
            user.get().setFirstName(updatedUser.getFirstName());
            user.get().setLastName(updatedUser.getLastName());
            userRepository.save(user.get());
            LOGGER.debug("User {} updated", user.get().getEmail());
        }
    }

    /**
     * Verify user user response dto.
     *
     * @param token the token
     * @return the user response dto
     */
    public EmailVerificationResponseDTO verifyUser(String token) {
        LOGGER.debug("Attempting to verify user with token {}", token);
        String email = new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8).split(":")[0];
        Optional<User> user = userRepository.findByEmailAndTokenToVerify(email, token);
        if(user.isPresent() && LocalDateTime.now().isAfter(user.get().getTokenExpiryTime())){
            LOGGER.error("User {} failed to verify before the stipulated time", user.get().getEmail());
            throw new EmailVerificationException("User " + user.get().getEmail() + " failed to verify before the stipulated time");
        }
        if(user.isPresent() && !user.get().isVerified()) {
            user.get().setVerified(true);
            userRepository.save(user.get());
            LOGGER.debug("User {} verified", user.get().getEmail());
            return new EmailVerificationResponseDTO(true, "User has been verified successfully");
        }
        if(user.isPresent()){
            LOGGER.debug("User {} already verified", user.get().getEmail());
            throw new CustomException("User already verified");
        }
        LOGGER.debug("User token {} verification failed", token);
        throw new CustomException("Invalid URl");
    }

    /**
     * Get UserResponseDTO obj from User obj
     *
     * @param user obj
     * @return UserResponseDTO
     */
    private UserResponseDTO getUserResponseFromUser(User user) {
        return UserResponseDTO.builder()
                .accountCreated(user.getAccountCreated())
                .accountUpdated(user.getAccountUpdated())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .id(user.getId())
                .build();
    }

    /**
     * Get User obj from CreateUserRequestDTO
     *
     * @param createUserRequestDTO obj
     * @return User
     */
    private User getUserFromCreateRequest(CreateUserRequestDTO createUserRequestDTO) {
        return User.builder()
                .firstName(createUserRequestDTO.getFirstName())
                .lastName(createUserRequestDTO.getLastName())
                .password(passwordEncoder.encode(createUserRequestDTO.getPassword()))
                .email(createUserRequestDTO.getEmail())
                .build();
    }

    /**
     * Get User obj from UpdateUserRequestDTO
     *
     * @param updateUserRequestDTO obj
     * @return User
     */
    private User getUserFromUpdateRequest(UpdateUserRequestDTO updateUserRequestDTO) {
        String email = Util.getUserEmail();
        return User.builder()
                .firstName(updateUserRequestDTO.getFirstName())
                .lastName(updateUserRequestDTO.getLastName())
                .password(passwordEncoder.encode(updateUserRequestDTO.getPassword()))
                .email(email)
                .build();
    }

    private boolean validateUserVerification(User user){
        if(user.isVerified()){
            return true;
        }
        if(null != user.getTokenExpiryTime() && LocalDateTime.now().isAfter(user.getTokenExpiryTime())){
            LOGGER.error("User {} failed to verify before the stipulated time", user.getEmail());
            throw new EmailVerificationException("User " + user.getEmail() + " failed to verify before the stipulated time");
        } else {
            LOGGER.error("Verify through the link sent by email first");
            throw new EmailVerificationException("Verify through the link sent by email first");
        }
    }

}

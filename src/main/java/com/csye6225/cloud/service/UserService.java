package com.csye6225.cloud.service;

import com.csye6225.cloud.dto.CreateUserRequestDTO;
import com.csye6225.cloud.dto.UpdateUserRequestDTO;
import com.csye6225.cloud.dto.UserResponseDTO;
import com.csye6225.cloud.exception.CustomException;
import com.csye6225.cloud.model.User;
import com.csye6225.cloud.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * The type User service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Get requesting user's details
     *
     * @return UserResponseDTO user
     */
    public UserResponseDTO getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<User> user = userRepository.findByEmail(email);
        return getUserResponseFromUser(user.get());
    }

    /**
     * Create a user
     *
     * @param createUserRequestDTO obj
     * @return UserResponseDTO user response dto
     */
    public UserResponseDTO createUser(CreateUserRequestDTO createUserRequestDTO) {
        User user = getUserFromCreateRequest(createUserRequestDTO);
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            log.error("User already exists {}", existingUser);
            throw new CustomException("User already exists");
        }
        user = userRepository.save(user);
        return getUserResponseFromUser(user);
    }


    public void updateUser(UpdateUserRequestDTO updateUserRequestDTO) {
        User updatedUser = getUserFromUpdateRequest(updateUserRequestDTO);
        User user = userRepository.findByEmail(updatedUser.getEmail()).get();
        user.setPassword(updatedUser.getPassword());
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        userRepository.save(user);
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return User.builder()
                .firstName(updateUserRequestDTO.getFirstName())
                .lastName(updateUserRequestDTO.getLastName())
                .password(passwordEncoder.encode(updateUserRequestDTO.getPassword()))
                .email(email)
                .build();
    }

}

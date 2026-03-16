package com.example.userdemo.service;

import com.example.userdemo.dto.CreateUserRequest;
import com.example.userdemo.dto.UpdateUserRequest;
import com.example.userdemo.dto.UserDTO;
import com.example.userdemo.entity.User;
import com.example.userdemo.entity.UserStatus;
import com.example.userdemo.exception.ResourceNotFoundException;
import com.example.userdemo.exception.UserAlreadyExistsException;
import com.example.userdemo.mapper.UserMapper;
import com.example.userdemo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Page<UserDTO> getUsersPaginated(Pageable pageable) {
        log.info("Fetching users with pagination: {}", pageable);
        return userRepository.findAll(pageable)
                .map(userMapper::toDTO);
    }
    
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        log.info("Fetching user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id".concat(
                            Long.toString(id)
                        )
                    )
                );
        return userMapper.toDTO(user);
    }
    
    @Transactional
    public UserDTO createUser(CreateUserRequest request) {
        log.info("Creating new user with email: {}", request.getEmail());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(
                    "User already exists with email: "
                        .concat(request.getEmail())
            );
        }
        
        User user = userMapper.toEntity(request);
        user = userRepository.save(user);
        log.info("User created successfully with id: {}", user.getId());
        
        return userMapper.toDTO(user);
    }
    
    @Transactional
    public UserDTO updateUser(Long id, UpdateUserRequest request) {
        log.info("Updating user with id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "User not found with id: "
                                        .concat(Long.toString(id))
                        )
                );
        
        // Check if email is being changed and if it already exists
        String eMail = request.getEmail();
        if (eMail != null && !eMail.equals(user.getEmail())) {
            if (userRepository.existsByEmailAndIdNot(eMail, id)) {
                throw new UserAlreadyExistsException(
                        "Email already exists: "
                        .concat(eMail)
                );
            }
            user.setEmail(eMail);
        }
        
        // Update fields if provided
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        
        user = userRepository.save(user);
        log.info("User updated successfully with id: {}", user.getId());
        
        return userMapper.toDTO(user);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                "User not found with id: "
                    .concat(
                        Long.toString(id)
                    )
            );
        }
        
        userRepository.deleteById(id);
        log.info("User deleted successfully with id: {}", id);
    }
    
    @Transactional
    public UserDTO updateUserStatus(Long id, UserStatus status) {
        log.info("Updating status for user id: {} to {}", id, status);
        
        User user = userRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "User not found with id: ".concat(
                                    Long.toString(id)
                                )
                        )
                );
        
        user.setStatus(status.toString());
        user = userRepository.save(user);
        
        return userMapper.toDTO(user);
    }
    
    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        log.info("Fetching user with email: {}", email);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "User not found with email: "
                                    .concat(email)
                        )
                );
        
        return userMapper.toDTO(user);
    }

}
package com.swajyot.log.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swajyot.log.model.User;
import com.swajyot.log.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
    
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }
    
    public List<User> getUsersByRole(User.Role role) {
        return userRepository.findByRole(role);
    }
    
    public List<User> getActiveUsers() {
        return userRepository.findByActive(true);
    }
    
    @Transactional
    public User createUser(User user) {
        // In a real application, you would hash the password here
        return userRepository.save(user);
    }
    
    @Transactional
    public User updateUser(Long id, User updatedUser) {
        User existingUser = getUserById(id);
        
        existingUser.setName(updatedUser.getName());
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            // In a real application, you would hash the password here
            existingUser.setPassword(updatedUser.getPassword());
        }
        existingUser.setRole(updatedUser.getRole());
        existingUser.setActive(updatedUser.isActive());
        
        return userRepository.save(existingUser);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    @Transactional
    public User toggleUserActive(Long id) {
        User user = getUserById(id);
        user.setActive(!user.isActive());
        return userRepository.save(user);
    }
    
    // Basic authentication method - in a real app, you'd use Spring Security
    public User authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));
                
        // In a real application, you would verify the hashed password here
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid username or password");
        }
        
        if (!user.isActive()) {
            throw new RuntimeException("User account is deactivated");
        }
        
        return user;
    }
}

// Implementation of the UserService interface for managing User entities.
package com.hcoders.portal.service.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.hcoders.portal.model.User;
import com.hcoders.portal.repository.UserRepository;
import com.hcoders.portal.service.ResultService;
import com.hcoders.portal.service.TestService;
import com.hcoders.portal.service.UserService;
import jakarta.transaction.Transactional;

@Service  // Marks this class as a Spring service component.
@Qualifier("userServiceImpl") // Specifies the bean name for this service implementation.
@Transactional  // Ensures that methods execute within a transaction.
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository; // Repository for User persistence operations.

    @Autowired
    @Qualifier("testServiceImpl")
    private TestService testService; // Service for managing Test entities.

    @Autowired
    @Qualifier("resultServiceImpl")
    private ResultService resultService; // Service for managing Result entities.

    // Retrieves all users from the repository.
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // Retrieves a user by its unique ID.
    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // Retrieves a user by their username.
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Saves a new user and assigns the saved user to an admin.
    @Override
    public User save(User user, Long adminId) {
        User savedUser = userRepository.save(user); // Save the user entity.
        userRepository.assignUser(adminId, savedUser.getId()); // Link the saved user with an admin.
        return savedUser;
    }

    // Saves a new user entity without admin assignment.
    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    // Updates an existing user entity.
    @Override
    public User update(User user) {
        return userRepository.save(user);
    }

    // Deletes a user by its unique ID.
    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    // Finds a user by email, ignoring case differences.
    @Override
    public User findByEmailIgnoreCase(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    // Finds a user by username, ignoring case differences.
    @Override
    public User findByUsernameIgnoreCase(String username) {
        return userRepository.findByUsernameIgnoreCase(username);
    }

    // Retrieves all user IDs associated with the specified admin.
    @Override
    public List<Long> findAllIdsOfOwnUsers(long adminId) {
        return userRepository.findAllIdsOfOwnUsers(adminId);
    }

    // Retrieves all users that belong to a specific admin.
    @Override
    public List<User> findAllUsersByAdminId(long adminId) {
        // Get list of user IDs associated with the admin.
        List<Long> idsOfUsersBelongToAdmin = userRepository.findAllIdsOfOwnUsers(adminId);
        List<User> users = new ArrayList<User>();
        // For each user ID, retrieve the User entity if available.
        for (Long id : idsOfUsersBelongToAdmin) {
            Optional<User> user = userRepository.findById(id);
            if (user.isPresent())
                users.add(user.get());
        }
        return users;
    }

    // Retrieves all active (enabled) users that belong to a specific admin.
    @Override
    public List<User> findAllActiveUsersByAdminId(long adminId) {
        // Get list of user IDs associated with the admin.
        List<Long> idsOfUsersBelongToAdmin = userRepository.findAllIdsOfOwnUsers(adminId);
        List<User> users = new ArrayList<User>();
        // For each user ID, check if the user is enabled and add them to the list.
        for (Long id : idsOfUsersBelongToAdmin) {
            Optional<User> user = userRepository.findById(id);
            if (user.isPresent() && user.get().isEnabled())
                users.add(user.get());
        }
        return users;
    }

    // Retrieves all candidates for a test who are not already given access.
    // This method filters out users who can already access the test.
    @Override
    public List<User> findAllCandidatesForATestByAdminId(long testId, long adminId) {
        // Retrieve all active users associated with the admin.
        List<User> users = findAllActiveUsersByAdminId(adminId);
        // Filter users who already have access to the test.
        List<User> usersSeeTest = users.stream()
                .filter(user -> testService.isAccessableByUser(testId, user.getId()))
                .collect(Collectors.toList());
        // Remove users who already have access.
        users.removeAll(usersSeeTest);
        return users;
    }

    // Finds the admin's ID associated with a given user.
    @Override
    public Long findAdminOf(Long userId) {
        return userRepository.findAdminIdOf(userId);
    }

    // Assigns a user to a specific admin.
    @Override
    public void assignUser(long adminId, long userId) {
        userRepository.assignUser(adminId, userId);
    }

    // Deletes a user by their username.
    @Override
    public void deleteByUsername(String username) {
        userRepository.deleteByUsername(username);
    }

    // Deletes a user by their email.
    @Override
    public void deleteByEmail(String email) {
        userRepository.deleteByEmail(email);
    }
}

// Service interface for managing User entities in the application.
package com.hcoders.portal.service;

import java.util.List;
import java.util.Optional;
import com.hcoders.portal.model.User;

public interface UserService {

    // Retrieves all users from the system.
    List<User> findAll();

    // Retrieves a user by its unique ID.
    Optional<User> findById(Long id);

    // Retrieves a user by their username.
    User findByUsername(String username);

    // Saves a new user entity.
    User save(User user);

    // Saves a new user entity and assigns the user to an admin.
    User save(User user, Long adminId);

    // Updates an existing user entity.
    User update(User user);

    // Deletes a user by their unique ID.
    void deleteById(Long id);

    // Finds a user by email, ignoring case differences.
    User findByEmailIgnoreCase(String email);

    // Finds a user by username, ignoring case differences.
    User findByUsernameIgnoreCase(String username);

    // Retrieves the list of user IDs that belong to a specific admin.
    List<Long> findAllIdsOfOwnUsers(long adminId);

    // Retrieves all users associated with a particular admin.
    List<User> findAllUsersByAdminId(long adminId);

    // Retrieves all active users (enabled) associated with a particular admin.
    List<User> findAllActiveUsersByAdminId(long adminId);

    // Assigns a user to an admin.
    void assignUser(long adminId, long userId);

    // Finds the admin associated with a given user.
    Long findAdminOf(Long userId);

    // Retrieves all candidates for a specific test for a given admin.
    List<User> findAllCandidatesForATestByAdminId(long testId, long adminId);

    // Deletes a user by their username.
    void deleteByUsername(String username);
    
    // Deletes a user by their email.
    void deleteByEmail(String email);
}

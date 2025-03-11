package com.hcoders.portal.repository;

// Importing necessary classes for working with collections
import java.util.List;

// Importing Spring Data JPA classes to enable repository functionality
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
// The Repository annotation marks this interface as a Spring Data Repository bean.
import org.springframework.stereotype.Repository;
// Transactional ensures that the methods run within a transactional context.
import org.springframework.transaction.annotation.Transactional;

// Importing the User entity which this repository will manage.
import com.hcoders.portal.model.User;

/**
 * Repository interface for managing User entities.
 * 
 * This interface extends JpaRepository, which provides built-in CRUD operations and query derivation.
 * Additionally, custom queries are defined to handle associations between admins and their users.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Assigns a user to an admin.
     * <p>
     * This method executes a native SQL insert into the "admin_user" join table, which maps an admin to a user.
     * The parameters adminId and userId are inserted into the table.
     * </p>
     *
     * @param adminId the ID of the admin.
     * @param userId the ID of the user to be assigned.
     */
    @Modifying
    @Query(value = "insert into admin_user (admin_Id, user_Id) values (?1, ?2)", nativeQuery = true)
    @Transactional
    void assignUser(long adminId, long userId);

    /**
     * Retrieves the list of user IDs that are assigned to a specific admin.
     * <p>
     * This native query selects the "user_id" from the "admin_user" table where the "admin_id" matches the provided parameter.
     * </p>
     *
     * @param adminId the ID of the admin.
     * @return a List of user IDs assigned to the given admin.
     */
    @Query(value = "select user_id from admin_user where admin_id=?1", nativeQuery = true)
    @Transactional
    List<Long> findAllIdsOfOwnUsers(long adminId);

    /**
     * Finds the admin ID associated with a given user.
     * <p>
     * This native query retrieves the "admin_id" from the "admin_user" table where the "user_id" matches the provided parameter.
     * </p>
     *
     * @param userId the ID of the user.
     * @return the admin ID associated with the user.
     */
    @Query(value = "select admin_id from admin_user where user_id=?1", nativeQuery = true)
    @Transactional
    Long findAdminIdOf(Long userId);

    /**
     * Finds a User entity by username.
     * <p>
     * This method leverages Spring Data JPA's query derivation to generate the appropriate query.
     * </p>
     *
     * @param username the username to search for.
     * @return the User entity with the specified username.
     */
    User findByUsername(String username);

    /**
     * Finds a User entity by email (ignoring case).
     * <p>
     * Spring Data JPA generates a query to find a user where the email matches, ignoring case sensitivity.
     * </p>
     *
     * @param email the email address to search for.
     * @return the User entity with the matching email, or null if not found.
     */
    User findByEmailIgnoreCase(String email);

    /**
     * Finds a User entity by username (ignoring case).
     * <p>
     * This method is similar to findByUsername, but performs a case-insensitive search.
     * </p>
     *
     * @param username the username to search for.
     * @return the User entity with the matching username, or null if not found.
     */
    User findByUsernameIgnoreCase(String username);

    /**
     * Deletes a User entity by username.
     * <p>
     * Spring Data JPA automatically generates the necessary delete query based on the method name.
     * </p>
     *
     * @param username the username of the user to be deleted.
     */
    void deleteByUsername(String username);
    
    /**
     * Deletes a User entity by email.
     * <p>
     * Spring Data JPA will generate the delete query to remove the user with the specified email.
     * </p>
     *
     * @param email the email address of the user to be deleted.
     */
    void deleteByEmail(String email);
}

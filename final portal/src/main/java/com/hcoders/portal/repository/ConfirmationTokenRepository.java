package com.hcoders.portal.repository;

// Importing the List interface for returning collections.
import java.util.List;

// Importing Spring Data JPA classes to enable repository functionality.
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
// Repository annotation marks this interface as a Spring Data Repository.
import org.springframework.stereotype.Repository;
// Transactional ensures that modifying queries run within a transactional context.
import org.springframework.transaction.annotation.Transactional;

// Importing the ConfirmationToken entity which this repository manages.
import com.hcoders.portal.model.ConfirmationToken;

/**
 * Repository interface for managing ConfirmationToken entities.
 *
 * This interface extends JpaRepository, which provides basic CRUD operations
 * and additional functionality out-of-the-box.
 */
@Repository // Marks this interface as a Spring Data Repository bean.
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {

    /**
     * Finds a ConfirmationToken entity based on the confirmation token string.
     *
     * @param confirmationToken the unique token string to search for.
     * @return the ConfirmationToken entity that matches the provided token string.
     */
    ConfirmationToken findByConfirmationToken(String confirmationToken);

    /**
     * Retrieves the IDs of users who have an unused confirmation token.
     *
     * This native SQL query joins three tables: admin_user, user, and confirmation_token.
     * It checks for tokens that have been sent (ct.sent=1) and returns the user IDs
     * for users assigned to a specific admin (using the admin_id parameter).
     *
     * @param adminId the ID of the admin for whom the user tokens are being queried.
     * @return a list of user IDs that have received but not yet used confirmation tokens.
     */
    @Query(
        value = "SELECT u.id FROM admin_user au, user u, confirmation_token ct " +
                "WHERE au.admin_id=?1 AND au.user_id=u.id " +
                "AND u.id = ct.user_id AND ct.sent=1;",
        nativeQuery = true
    )
    List<Long> findUsersIdsWithUnusedToken(Long adminId);

    /**
     * Removes the association between a ConfirmationToken and its User.
     *
     * This is useful when you want to dissociate the token from the user without deleting the token record.
     * The @Modifying annotation indicates that this query performs an update operation.
     * The @Transactional annotation ensures that the operation runs within a transaction.
     *
     * @param userId the ID of the user whose ConfirmationToken association should be removed.
     */
    @Modifying
    @Query("update ConfirmationToken ct set ct.user = null where ct.user.id = ?1")
    @Transactional
    void removeUserOfConfirmationToken(Long userId);

    /**
     * Deletes confirmation token(s) associated with a given user ID.
     *
     * This method uses Spring Data JPA's query derivation mechanism. By specifying "deleteByUser_Id",
     * Spring Data automatically generates the necessary query to remove all ConfirmationToken records
     * that are linked to the user with the provided ID.
     *
     * @param userId the ID of the user whose confirmation token records should be deleted.
     */
    void deleteByUser_Id(Long userId);
}

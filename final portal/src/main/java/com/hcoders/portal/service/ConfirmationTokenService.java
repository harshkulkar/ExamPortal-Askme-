package com.hcoders.portal.service;

import java.util.List;

import com.hcoders.portal.model.ConfirmationToken;
import com.hcoders.portal.model.User;

/**
 * Service interface for managing confirmation tokens.
 */
public interface ConfirmationTokenService {
    
    /**
     * Retrieves the list of user IDs that have unused confirmation tokens for a given admin.
     *
     * @param adminId the administrator's user ID
     * @return a list of user IDs with unused tokens
     */
    List<Long> findUsersIdsWithUnusedToken(Long adminId);

    /**
     * Retrieves the list of User objects that have unused confirmation tokens for a given admin.
     *
     * @param adminId the administrator's user ID
     * @return a list of Users with unused tokens
     */
    List<User> findUsersWithUnusedToken(Long adminId);

    /**
     * Persists the provided confirmation token.
     *
     * @param confirmationToken the confirmation token to save
     * @return the saved ConfirmationToken entity
     */
    ConfirmationToken save(ConfirmationToken confirmationToken);

    /**
     * Removes the association of a user from their confirmation token.
     *
     * @param userId the ID of the user to dissociate
     */
    void removeUserOfConfirmationToken(Long userId);

    /**
     * Deletes the confirmation token associated with the given user ID.
     *
     * @param userId the user ID whose token should be deleted
     */
    void deleteByUserId(Long userId);
}

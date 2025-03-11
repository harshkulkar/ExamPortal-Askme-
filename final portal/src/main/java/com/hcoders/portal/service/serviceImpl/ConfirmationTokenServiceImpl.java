package com.hcoders.portal.service.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.hcoders.portal.model.ConfirmationToken;
import com.hcoders.portal.model.User;
import com.hcoders.portal.repository.ConfirmationTokenRepository;
import com.hcoders.portal.repository.UserRepository;
import com.hcoders.portal.service.ConfirmationTokenService;

import jakarta.transaction.Transactional;

/**
 * Implementation of the ConfirmationTokenService interface.
 */
@Service
@Qualifier("confirmationTokenServiceImpl")
@Transactional
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {

    // Injecting the repository that handles confirmation token persistence.
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    // Injecting the repository for user-related database operations.
    @Autowired
    private UserRepository userRepository;

    /**
     * Retrieves a list of user IDs with unused confirmation tokens for the specified admin.
     *
     * @param adminId the administrator's user ID
     * @return a list of user IDs with unused tokens
     */
    @Override
    public List<Long> findUsersIdsWithUnusedToken(Long adminId) {
        return confirmationTokenRepository.findUsersIdsWithUnusedToken(adminId);
    }

    /**
     * Retrieves the list of User objects that have unused confirmation tokens for the specified admin.
     *
     * This method first retrieves the user IDs with unused tokens, then fetches each User
     * from the database. If a user is not found for a given ID, it is skipped.
     *
     * @param adminId the administrator's user ID
     * @return a list of Users with unused tokens
     */
    @Override
    public List<User> findUsersWithUnusedToken(Long adminId) {
        List<Long> usersIdsWithUnusedToken = findUsersIdsWithUnusedToken(adminId);
        List<User> usersWithUnusedToken = new ArrayList<User>();
        
        // Iterate through the list of user IDs and fetch the corresponding User object.
        for (Long userId : usersIdsWithUnusedToken) {
            Optional<User> user = userRepository.findById(userId);
            // If the user exists, add to the result list.
            if (user.isPresent()) {
                usersWithUnusedToken.add(user.get());
            }
        }
        return usersWithUnusedToken;
    }

    /**
     * Saves the given confirmation token entity in the database.
     *
     * @param confirmationToken the confirmation token to be saved
     * @return the saved ConfirmationToken entity
     */
    @Override
    public ConfirmationToken save(ConfirmationToken confirmationToken) {
        return confirmationTokenRepository.save(confirmationToken);
    }

    /**
     * Removes the user association from the confirmation token.
     *
     * @param userId the user ID whose token association should be removed
     */
    @Override
    public void removeUserOfConfirmationToken(Long userId) {
        confirmationTokenRepository.removeUserOfConfirmationToken(userId);
    }

    /**
     * Deletes the confirmation token associated with the specified user ID.
     *
     * @param userId the user ID whose token should be deleted
     */
    @Override
    public void deleteByUserId(Long userId) {
        confirmationTokenRepository.deleteByUser_Id(userId);
    }
}

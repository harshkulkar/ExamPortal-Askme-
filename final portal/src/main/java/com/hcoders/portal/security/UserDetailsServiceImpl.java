package com.hcoders.portal.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.hcoders.portal.model.User;
import com.hcoders.portal.repository.UserRepository;

/**
 * Custom implementation of Spring Security's UserDetailsService.
 * Loads user-specific data for authentication purposes.
 */
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository; // Repository to access User data.

    /**
     * Locates the user based on the username.
     * In the actual implementation, the username is expected to be unique.
     *
     * @param username the username identifying the user whose data is required.
     * @return A fully populated UserDetails object.
     * @throws UsernameNotFoundException if the user could not be found or the user has no GrantedAuthority.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Convert username to lowercase for consistency.
        User user = userRepository.findByUsername(username.toLowerCase());
        
        // Throw exception if user is not found.
        if (user == null) {
            throw new UsernameNotFoundException("Could not find User");
        }

        // Wrap the user into UserDetailsImpl and return it.
        return new UserDetailsImpl(user);
    }
}

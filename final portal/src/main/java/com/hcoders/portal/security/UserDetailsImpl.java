package com.hcoders.portal.security;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.hcoders.portal.model.User;

import jakarta.transaction.Transactional;

/**
 * Custom implementation of Spring Security's UserDetails interface.
 * This class wraps a User model instance to provide security-specific user details.
 */
public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L; // Serialization version for compatibility

    // Underlying User entity that contains the application-specific user details.
    private User user;

    /**
     * Constructor that accepts a User object.
     *
     * @param user The user entity to be wrapped.
     */
    public UserDetailsImpl(User user) {
        this.user = user;
    }

    /**
     * Returns the authorities granted to the user.
     * Here, the user's role is converted into a SimpleGrantedAuthority.
     *
     * @return A collection of granted authorities.
     */
    @Override
    @Transactional
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Create an authority using the user's role.
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
        return Arrays.asList(authority);
    }

    /**
     * Returns the unique identifier of the user.
     *
     * @return The user ID.
     */
    public Long getId() {
        return user.getId();
    }

    /**
     * Returns the password used to authenticate the user.
     *
     * @return The user's password.
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Returns the username used to authenticate the user.
     *
     * @return The user's username.
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * Indicates whether the user's account has expired.
     *
     * @return true if the account is valid (non-expired), false if expired.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; // All accounts are considered non-expired in this implementation.
    }

    /**
     * Indicates whether the user is locked or unlocked.
     *
     * @return true if the user is not locked, false otherwise.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true; // All accounts are considered non-locked in this implementation.
    }

    /**
     * Indicates whether the user's credentials (password) has expired.
     *
     * @return true if the credentials are valid (non-expired), false if expired.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Credentials are considered non-expired in this implementation.
    }

    /**
     * Indicates whether the user is enabled or disabled.
     *
     * @return true if the user is enabled, false otherwise.
     */
    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    /**
     * Returns the underlying User entity.
     *
     * @return The user object.
     */
    public User getUser() {
        return user;
    }
}

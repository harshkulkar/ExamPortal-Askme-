package com.hcoders.portal.service;

import java.util.List;

import com.hcoders.portal.model.ConfirmationToken;
import com.hcoders.portal.model.User;

public interface ConfirmationTokenService {
	List<Long> findUsersIdsWithUnusedToken(Long adminId);

	List<User> findUsersWithUnusedToken(Long adminId);

	ConfirmationToken save(ConfirmationToken confirmationToken);

	void removeUserOfConfirmationToken(Long userId);

	void deleteByUserId(Long userId);
}

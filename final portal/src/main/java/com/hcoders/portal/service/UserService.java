package com.hcoders.portal.service;

import java.util.List;
import java.util.Optional;

import com.hcoders.portal.model.User;

public interface UserService {

	List<User> findAll();

	Optional<User> findById(Long id);

	User findByUsername(String username);

	User save(User user);

	User save(User user, Long adminId);

	User update(User user);

	void deleteById(Long id);

	User findByEmailIgnoreCase(String email);

	User findByUsernameIgnoreCase(String username);

	List<Long> findAllIdsOfOwnUsers(long adminId);

	List<User> findAllUsersByAdminId(long adminId);

	public List<User> findAllActiveUsersByAdminId(long adminId);

	void assignUser(long adminId, long userId);

	Long findAdminOf(Long userId);

	public List<User> findAllCandidatesForATestByAdminId(long testId, long adminId);

	void deleteByUsername(String username);
	
	void deleteByEmail(String email);

}
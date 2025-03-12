package com.hcoders.portal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.hcoders.portal.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	@Modifying
	@Query(value = "insert into admin_user (admin_Id, user_Id) values (?1, ?2) ", nativeQuery = true)
	@Transactional
	void assignUser(long adminId, long userId);

	@Query(value = "select user_id from admin_user where admin_id=?1 ", nativeQuery = true)
	@Transactional
	List<Long> findAllIdsOfOwnUsers(long adminId);

	@Query(value = "select admin_id from admin_user where user_id=?1 ", nativeQuery = true)
	@Transactional
	Long findAdminIdOf(Long userId);

	User findByUsername(String username);

	User findByEmailIgnoreCase(String email);

	User findByUsernameIgnoreCase(String username);

	void deleteByUsername(String username);
	
	void deleteByEmail(String email);
}

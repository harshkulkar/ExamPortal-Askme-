package com.hcoders.portal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.hcoders.portal.model.ConfirmationToken;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
	ConfirmationToken findByConfirmationToken(String confirmationToken);

	@Query(value = "SELECT u.id FROM admin_user au, user u, confirmation_token ct where au.admin_id=?1 AND au.user_id=u.id AND u.id = ct.user_id AND ct.sent=1;", nativeQuery = true)
	List<Long> findUsersIdsWithUnusedToken(Long adminId);

	@Modifying
	@Query("update ConfirmationToken ct set ct.user = null where ct.user.id = ?1")
	@Transactional
	void removeUserOfConfirmationToken(Long userId);

	void deleteByUser_Id(Long userId);
}

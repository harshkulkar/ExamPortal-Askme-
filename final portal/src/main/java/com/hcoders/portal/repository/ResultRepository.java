package com.hcoders.portal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hcoders.portal.model.Result;

public interface ResultRepository extends JpaRepository<Result, Long> {

	@Query(value = "SELECT user.username, result.test_name, result.create_date, result.total_mark, result.grade, result.passed"
			+ " FROM result,admin_user, user"
	    	+ " WHERE result.examinee_id= admin_user.user_id"
		    + " AND admin_user.admin_id = admin_user.admin_id=?1"
		    + " AND admin_user.user_id = user.id", nativeQuery = true)
	List<Object[]> findAllByAdminId(Long adminId);

	Result findByTestId(Long testId);

	List<Result> findByExamineeId(Long examineeId);

	void deleteByExamineeId(Long examineeId);
}

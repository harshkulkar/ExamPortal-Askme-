package com.hcoders.portal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.hcoders.portal.model.Test;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {

	@Query("SELECT t FROM Test t WHERE t.testCreator.id = :id")
	List<Test> findAllByCreaterId(Long id);

	@Modifying
	@Query(value = "insert into test_access (test_id, user_id, access) values (?1, ?2, ?3)  ON DUPLICATE KEY UPDATE  test_id=?1, user_id=?2, access=?3", nativeQuery = true)
	@Transactional
	void setTestAccess(Long testId, Long userId, int access);

	@Query(value = "SELECT  CASE WHEN (count(access) > 0) THEN 'true' else 'false' END FROM test_access  where test_id=?1 AND user_id=?2 AND access = 1 ", nativeQuery = true)
	@Transactional
	Boolean isAccessableByUser(Long testId, Long userId);

	@Modifying
	@Query("update Test test set test.totalMark = ?1 where test.id = ?2")
	@Transactional
	void setTotalMarkForTest(double totalMark, Long testId);
}

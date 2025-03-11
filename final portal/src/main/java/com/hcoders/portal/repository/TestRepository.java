package com.hcoders.portal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.hcoders.portal.model.Test;

/**
 * Repository interface for managing Test entities.
 * <p>
 * This interface extends JpaRepository, providing built-in CRUD operations for Test entities.
 * It also includes custom query methods for specific operations related to test access and marks.
 * </p>
 */
@Repository // Indicates that this interface is a Spring Data Repository.
public interface TestRepository extends JpaRepository<Test, Long> {

    /**
     * Retrieves all Test entities created by a specific user.
     * <p>
     * The JPQL query fetches tests where the testCreator's ID matches the provided id.
     * </p>
     *
     * @param id the ID of the user (test creator)
     * @return a List of Test entities created by the specified user.
     */
    @Query("SELECT t FROM Test t WHERE t.testCreator.id = :id")
    List<Test> findAllByCreaterId(Long id);

    /**
     * Sets or updates test access for a user.
     * <p>
     * This method uses a native SQL query to insert a new row into the test_access table
     * or update the existing one if the combination of test_id and user_id already exists.
     * The query uses "ON DUPLICATE KEY UPDATE" to handle both insertion and update in a single query.
     * </p>
     *
     * @param testId the ID of the test.
     * @param userId the ID of the user.
     * @param access the access level (e.g., 0 for no access, 1 for access granted).
     */
    @Modifying
    @Query(value = "insert into test_access (test_id, user_id, access) values (?1, ?2, ?3)  ON DUPLICATE KEY UPDATE  test_id=?1, user_id=?2, access=?3", nativeQuery = true)
    @Transactional
    void setTestAccess(Long testId, Long userId, int access);

    /**
     * Checks whether a test is accessible by a user.
     * <p>
     * This native query counts the number of rows in the test_access table that match the given test_id,
     * user_id and where access is set to 1. If the count is greater than 0, it returns 'true'; otherwise, 'false'.
     * </p>
     *
     * @param testId the ID of the test.
     * @param userId the ID of the user.
     * @return Boolean indicating whether the test is accessible by the user.
     */
    @Query(value = "SELECT  CASE WHEN (count(access) > 0) THEN 'true' else 'false' END FROM test_access  where test_id=?1 AND user_id=?2 AND access = 1 ", nativeQuery = true)
    @Transactional
    Boolean isAccessableByUser(Long testId, Long userId);

    /**
     * Updates the total mark for a specific test.
     * <p>
     * This JPQL update query sets the totalMark field of the Test entity with the provided value,
     * where the test's ID matches the given testId.
     * </p>
     *
     * @param totalMark the new total mark to be set for the test.
     * @param testId    the ID of the test to update.
     */
    @Modifying
    @Query("update Test test set test.totalMark = ?1 where test.id = ?2")
    @Transactional
    void setTotalMarkForTest(double totalMark, Long testId);
}

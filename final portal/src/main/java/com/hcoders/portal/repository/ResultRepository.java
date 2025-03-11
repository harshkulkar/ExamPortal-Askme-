package com.hcoders.portal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hcoders.portal.model.Result;

public interface ResultRepository extends JpaRepository<Result, Long> {

    /**
     * Custom native query to retrieve summarized result data for an admin.
     * <p>
     * The query joins three tables: result, admin_user, and user.
     * It selects the following fields:
     * <ul>
     *   <li>user.username</li>
     *   <li>result.test_name</li>
     *   <li>result.create_date</li>
     *   <li>result.total_mark</li>
     *   <li>result.grade</li>
     *   <li>result.passed</li>
     * </ul>
     * The query uses a parameter (?1) to filter results based on the admin's ID.
     * Note: The clause "AND admin_user.admin_id = admin_user.admin_id=?1" appears to be intended to filter
     * by the admin's ID. (Typically, it would be written as "AND admin_user.admin_id = ?1".)
     * </p>
     *
     * @param adminId the ID of the admin for which to retrieve result summaries.
     * @return a List of Object arrays, where each array contains the selected columns.
     */
    @Query(value = "SELECT user.username, result.test_name, result.create_date, result.total_mark, result.grade, result.passed"
            + " FROM result, admin_user, user"
            + " WHERE result.examinee_id = admin_user.user_id"
            + " AND admin_user.admin_id = admin_user.admin_id=?1"
            + " AND admin_user.user_id = user.id", nativeQuery = true)
    List<Object[]> findAllByAdminId(Long adminId);

    /**
     * Retrieves a Result entity based on the test ID.
     * <p>
     * This method uses Spring Data JPA's query derivation mechanism to generate a query based on the method name.
     * </p>
     *
     * @param testId the ID of the test for which the result is retrieved.
     * @return the Result entity associated with the given testId.
     */
    Result findByTestId(Long testId);

    /**
     * Retrieves all Result entities for a given examinee.
     * <p>
     * The method returns a list of results corresponding to the examinee's ID.
     * </p>
     *
     * @param examineeId the ID of the examinee (user) whose results should be retrieved.
     * @return a List of Result entities for the specified examinee.
     */
    List<Result> findByExamineeId(Long examineeId);

    /**
     * Deletes all Result entities associated with a given examinee.
     * <p>
     * This method leverages Spring Data JPA's query derivation to automatically generate a delete query.
     * </p>
     *
     * @param examineeId the ID of the examinee (user) whose results should be deleted.
     */
    void deleteByExamineeId(Long examineeId);
}

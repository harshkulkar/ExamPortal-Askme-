package com.hcoders.portal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hcoders.portal.model.VerificationCode;

/**
 * Repository interface for managing VerificationCode entities.
 * <p>
 * This interface extends JpaRepository, which provides standard CRUD operations
 * for the VerificationCode entity. It also supports query derivation based on method names.
 * </p>
 */
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    /**
     * Retrieves a list of VerificationCode entities matching the specified userId and testId.
     * <p>
     * Spring Data JPA automatically derives the query from the method name, searching for records
     * where the 'userId' field equals the provided userId and the 'testId' field equals the provided testId.
     * </p>
     *
     * @param userId the ID of the user for whom to retrieve verification codes.
     * @param testId the ID of the test for which to retrieve verification codes.
     * @return a List of VerificationCode entities that match the given userId and testId.
     */
    List<VerificationCode> findByUserIdAndTestId(Long userId, Long testId);

}

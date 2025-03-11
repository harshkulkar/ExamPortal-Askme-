// Service interface for managing Result entities
package com.hcoders.portal.service;

import java.util.List;
import java.util.Optional;

import com.hcoders.portal.model.Result;

public interface ResultService {
    // Retrieves a Result by its unique ID.
    Optional<Result> findById(Long id);

    // Retrieves a Result associated with a specific Test ID.
    Result findByTestId(Long testId);

    // Retrieves all Results for a given Examinee ID.
    List<Result> findByExamineeId(Long examineeId);

    // Saves or updates a Result entity in the database.
    Result save(Result result);

    // Retrieves a list of aggregated result data (as Object arrays) by Admin ID.
    List<Object[]> findAllByAdminId(Long admiId);

    // Deletes all Results associated with a given Examinee ID.
    void deleteByExamineeId(Long examineeId);
}

// Service interface for managing Test entities
package com.hcoders.portal.service;

import java.util.List;
import java.util.Optional;

import com.hcoders.portal.model.Test;

public interface TestService {
    
    // Retrieves a list of Tests created by a specific creator (identified by their ID)
    List<Test> findAllByCreaterId(Long id);

    // Saves a Test entity and returns the persisted instance
    Test save(Test test);

    // Adds a new Test entity to the repository
    void addTest(Test test);

    // Retrieves a Test by its unique ID
    Optional<Test> findById(Long id);

    // Checks whether a Test is accessible by a particular user
    Boolean isAccessableByUser(Long testId, Long userId);

    // Deletes a Test from the repository based on its unique ID
    void deleteById(Long id);

    // Sets the access level for a Test for a given user
    void setTestAccess(Long testId, Long userId, int access);

    // Updates the total marks for a Test
    void setTotalMarkForTest(double totalMark, Long testId);
}

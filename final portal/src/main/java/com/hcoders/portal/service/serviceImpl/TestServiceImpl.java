// Implementation of the TestService interface
package com.hcoders.portal.service.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.hcoders.portal.model.Test;
import com.hcoders.portal.repository.TestRepository;
import com.hcoders.portal.service.TestService;

@Service  // Indicates that this class is a Spring service component
@Qualifier("testServiceImpl") // Specifies the bean name for this implementation
public class TestServiceImpl implements TestService {

    @Autowired  // Injects the TestRepository dependency to perform database operations
    private TestRepository testRepository;

    // Retrieves all Test entities created by a specific creator using their ID
    @Override
    public List<Test> findAllByCreaterId(Long id) {
        return testRepository.findAllByCreaterId(id);
    }

    // Adds a new Test to the repository by saving it
    @Override
    public void addTest(Test test) {
        testRepository.save(test);
    }

    // Retrieves a Test by its unique ID from the repository
    @Override
    public Optional<Test> findById(Long id) {
        return testRepository.findById(id);
    }

    // Deletes a Test from the repository using its unique ID
    @Override
    public void deleteById(Long id) {
        testRepository.deleteById(id);
    }

    // Saves or updates a Test entity in the repository and returns the persisted entity
    @Override
    public Test save(Test test) {
        return testRepository.save(test);
    }

    // Checks if a Test is accessible by a given user (based on test and user IDs)
    @Override
    public Boolean isAccessableByUser(Long testId, Long userId) {
        return testRepository.isAccessableByUser(testId, userId);
    }

    // Sets the access level for a Test for a particular user
    @Override
    public void setTestAccess(Long testId, Long userId, int access) {
        testRepository.setTestAccess(testId, userId, access);
    }

    // Updates the total marks assigned to a Test in the repository
    @Override
    public void setTotalMarkForTest(double totalMark, Long testId) {
        testRepository.setTotalMarkForTest(totalMark, testId);
    }
}

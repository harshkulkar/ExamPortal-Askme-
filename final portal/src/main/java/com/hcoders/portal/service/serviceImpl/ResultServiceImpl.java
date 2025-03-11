// Implementation of the ResultService interface
package com.hcoders.portal.service.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.hcoders.portal.model.Result;
import com.hcoders.portal.repository.ResultRepository;
import com.hcoders.portal.service.ResultService;

import jakarta.transaction.Transactional;

@Service  // Indicates that this class is a service component in the Spring context
@Qualifier("resultServiceImpl") // Specifies the name for this implementation (helpful when multiple implementations exist)
@Transactional // Ensures that database operations within this service are executed within a transactional context
public class ResultServiceImpl implements ResultService {

    @Autowired  // Injects the ResultRepository dependency to interact with the database
    private ResultRepository resultRepository;

    // Retrieves a Result by its unique ID.
    @Override
    public Optional<Result> findById(Long id) {
        return resultRepository.findById(id);
    }

    // Saves or updates a Result entity in the database.
    @Override
    public Result save(Result result) {
        return resultRepository.save(result);
    }

    // Retrieves a Result associated with a specific Test ID.
    @Override
    public Result findByTestId(Long testId) {
        return resultRepository.findByTestId(testId);
    }

    // Retrieves all Results for a given Examinee ID.
    @Override
    public List<Result> findByExamineeId(Long examineeId) {
        return resultRepository.findByExamineeId(examineeId);
    }

    // Retrieves aggregated result data (as Object arrays) by Admin ID.
    @Override
    public List<Object[]> findAllByAdminId(Long admiId) {
        return resultRepository.findAllByAdminId(admiId);
    }

    // Deletes all Results associated with a given Examinee ID.
    @Override
    public void deleteByExamineeId(Long examineeId) {
        resultRepository.deleteByExamineeId(examineeId);
    }
}

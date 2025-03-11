// Service interface for managing Question entities
package com.hcoders.portal.service;

import java.util.List;
import java.util.Optional;

import com.hcoders.portal.model.Question;

public interface QuestionService {

    // Retrieves a Question by its ID
    Optional<Question> findById(Long questionId);

    // Retrieves all Questions associated with a specific Test ID
    List<Question> findByTestId(Long testId);

    // Saves a Question entity to the database
    Question save(Question question);

    // Deletes a Question by its ID
    void deleteById(Long questionId);
}

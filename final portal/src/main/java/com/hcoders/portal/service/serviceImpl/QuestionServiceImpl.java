// Implementation of the QuestionService interface
package com.hcoders.portal.service.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.hcoders.portal.model.Question;
import com.hcoders.portal.repository.QuestionRepository;
import com.hcoders.portal.service.QuestionService;

import jakarta.transaction.Transactional;

@Service  // Marks this class as a service component in the Spring context
@Qualifier("questionServiceImpl") // Used to specify this implementation if multiple exist
@Transactional // Ensures all database operations in this service are executed within a transaction
public class QuestionServiceImpl implements QuestionService {

    @Autowired  // Injects the QuestionRepository dependency
    private QuestionRepository questionRepository;

    // Retrieves all Questions associated with a specific Test ID
    @Override
    public List<Question> findByTestId(Long testId) {
        return questionRepository.findByTest_Id(testId);
    }

    // Saves a Question entity to the database
    @Override
    public Question save(Question question) {
        return questionRepository.save(question);
    }

    // Retrieves a Question by its ID
    @Override
    public Optional<Question> findById(Long questionId) {
        return questionRepository.findById(questionId);
    }

    // Deletes a Question by its ID
    @Override
    public void deleteById(Long questionId) {
        questionRepository.deleteById(questionId);
    }
}

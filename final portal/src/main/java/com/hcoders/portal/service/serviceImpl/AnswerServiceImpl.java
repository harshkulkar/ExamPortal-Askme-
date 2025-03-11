package com.hcoders.portal.service.serviceImpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.hcoders.portal.model.Answer;
import com.hcoders.portal.repository.AnswerRepository;
import com.hcoders.portal.service.AnswerService;

import jakarta.transaction.Transactional;

/**
 * Implementation of the AnswerService interface.
 * <p>
 * This service provides methods for saving, retrieving, and deleting Answer entities
 * using the underlying AnswerRepository.
 * </p>
 */
@Service
@Qualifier("answerServiceImpl")
@Transactional // Ensures that all methods run within a transaction.
public class AnswerServiceImpl implements AnswerService {

    // Injecting the AnswerRepository to interact with the database for Answer entities.
    @Autowired
    private AnswerRepository answerRepository;

    /**
     * Saves the given Answer entity.
     * <p>
     * This method delegates the save operation to the AnswerRepository.
     * </p>
     *
     * @param answer the Answer entity to be saved.
     * @return the saved Answer entity, potentially with generated fields (e.g., ID).
     */
    @Override
    public Answer save(Answer answer) {
        // Delegate saving the Answer entity to the repository.
        return answerRepository.save(answer);
    }

    /**
     * Saves the given Answer entity and flushes the persistence context immediately.
     * <p>
     * This ensures that the changes are immediately persisted and visible to subsequent queries.
     * </p>
     *
     * @param answer the Answer entity to be saved and flushed.
     * @return the saved Answer entity.
     */
    public Answer saveAndFlush(Answer answer) {
        // Delegate saving and flushing the Answer entity to the repository.
        return answerRepository.saveAndFlush(answer);
    }

    /**
     * Retrieves an Answer entity by its ID.
     * <p>
     * Returns an Optional containing the Answer if found, or an empty Optional if not.
     * </p>
     *
     * @param answerId the ID of the Answer to retrieve.
     * @return an Optional containing the Answer entity if it exists.
     */
    @Override
    public Optional<Answer> findById(long answerId) {
        // Delegate the findById operation to the repository.
        return answerRepository.findById(answerId);
    }

    /**
     * Deletes the Answer entity with the specified ID.
     * <p>
     * This method removes the Answer entity from the database.
     * </p>
     *
     * @param answerId the ID of the Answer to be deleted.
     */
    @Override
    public void deleteById(Long answerId) {
        // Delegate the delete operation to the repository.
        answerRepository.deleteById(answerId);
    }
}

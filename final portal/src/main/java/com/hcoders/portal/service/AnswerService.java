package com.hcoders.portal.service;

import java.util.Optional;

import com.hcoders.portal.model.Answer;

/**
 * Service interface for handling operations related to Answer entities.
 * <p>
 * This interface defines the contract for saving, retrieving, and deleting Answer objects.
 * Implementations of this interface should provide the logic to persist Answer entities
 * in the underlying data store.
 * </p>
 */
public interface AnswerService {

    /**
     * Saves the given Answer entity.
     * <p>
     * Persists the provided Answer object in the database.
     * </p>
     *
     * @param answer the Answer entity to be saved.
     * @return the saved Answer entity, which may include generated values such as an ID.
     */
    Answer save(Answer answer);

    /**
     * Saves the given Answer entity and flushes the persistence context immediately.
     * <p>
     * This method not only saves the Answer entity but also forces a flush,
     * ensuring that the changes are immediately visible to subsequent queries.
     * </p>
     *
     * @param answer the Answer entity to be saved and flushed.
     * @return the saved Answer entity.
     */
    Answer saveAndFlush(Answer answer);

    /**
     * Retrieves an Answer entity by its ID.
     * <p>
     * The method returns an Optional that may contain the Answer if it exists,
     * or be empty if no Answer is found with the given ID.
     * </p>
     *
     * @param answerId the ID of the Answer to retrieve.
     * @return an Optional containing the Answer if found, or empty otherwise.
     */
    Optional<Answer> findById(long answerId);

    /**
     * Deletes the Answer entity with the specified ID.
     * <p>
     * This operation removes the Answer from the underlying database.
     * </p>
     *
     * @param answerId the ID of the Answer to be deleted.
     */
    void deleteById(Long answerId);
}

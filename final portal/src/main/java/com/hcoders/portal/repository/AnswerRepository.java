package com.hcoders.portal.repository;

// Import the JpaRepository interface which provides CRUD operations and more.
import org.springframework.data.jpa.repository.JpaRepository;
// Import the Repository annotation to mark this interface as a Spring Data Repository.
import org.springframework.stereotype.Repository;

// Import the Answer entity class that this repository will manage.
import com.hcoders.portal.model.Answer;

/**
 * Repository interface for the Answer entity.
 * <p>
 * This interface extends JpaRepository, providing standard CRUD (Create, Read, Update, Delete)
 * operations and additional JPA-related methods for the Answer entity.
 * <br>
 * The generic parameters:
 * <ul>
 *   <li>Answer: the type of the entity to handle</li>
 *   <li>Long: the type of the entity's primary key</li>
 * </ul>
 * </p>
 */
@Repository // Indicates that this interface is a Spring Data Repository.
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    // No additional methods are defined here. Spring Data JPA automatically implements basic CRUD operations.
}

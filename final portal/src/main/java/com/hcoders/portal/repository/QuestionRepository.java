package com.hcoders.portal.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.hcoders.portal.model.Question;

/**
 * Repository interface for managing Question entities.
 *
 * This interface extends JpaRepository, which provides built-in methods for CRUD operations
 * and query derivation mechanisms.
 *
 * The generic parameters are:
 *  - Question: the type of the entity managed by this repository.
 *  - Long: the type of the entity's primary key.
 */
@Repository // Marks this interface as a Spring Data Repository.
public interface QuestionRepository extends JpaRepository<Question, Long> {

    /**
     * Retrieves a list of Question entities that belong to a specific test.
     *
     * Spring Data JPA automatically generates the query based on the method name.
     * In this case, it will find all questions where the associated test's ID matches the provided testId.
     *
     * @param testId the ID of the test for which to find questions.
     * @return a List of Question entities that belong to the test with the given testId.
     */
    List<Question> findByTest_Id(Long testId);
}

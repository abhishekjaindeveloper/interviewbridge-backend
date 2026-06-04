package com.interviewbridge.repository;

import com.interviewbridge.entity.PracticeQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing PracticeQuestion database operations.
 */
@Repository
public interface PracticeQuestionRepository extends JpaRepository<PracticeQuestion, UUID> {

    /**
     * Finds all questions associated with a practice session ordered by question number ascending.
     *
     * @param sessionId the practice session UUID
     * @return list of practice questions
     */
    List<PracticeQuestion> findByPracticeSessionIdOrderByQuestionNumberAsc(UUID sessionId);

    /**
     * Optimized query to retrieve a practice question along with its session and session owner user.
     *
     * @param id the practice question UUID
     * @return optional practice question containing initialized session and user
     */
    @Query("SELECT q FROM PracticeQuestion q JOIN FETCH q.practiceSession s JOIN FETCH s.user u JOIN FETCH s.technology t JOIN FETCH s.experience e WHERE q.id = :id")
    Optional<PracticeQuestion> findByIdWithSessionAndUser(@Param("id") UUID id);
}

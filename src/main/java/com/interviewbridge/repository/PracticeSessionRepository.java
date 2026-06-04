package com.interviewbridge.repository;

import com.interviewbridge.entity.PracticeSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing PracticeSession database operations.
 */
@Repository
public interface PracticeSessionRepository extends JpaRepository<PracticeSession, UUID> {

    /**
     * Finds all practice sessions belonging to a user by email, ordered by creation date descending.
     *
     * @param email the user email
     * @return list of practice sessions
     */
    List<PracticeSession> findByUserEmailOrderByCreatedAtDesc(String email);

    /**
     * Finds all practice sessions belonging to a user by email, optimized using JOIN FETCH.
     *
     * @param email the user email
     * @return list of practice sessions containing initialized user, technology, and experience
     */
    @Query("SELECT s FROM PracticeSession s JOIN FETCH s.user u JOIN FETCH s.technology t JOIN FETCH s.experience e WHERE u.email = :email ORDER BY s.createdAt DESC")
    List<PracticeSession> findByUserEmailWithUserAndTechnologyAndExperienceOrderByCreatedAtDesc(@Param("email") String email);

    /**
     * Finds a practice session by ID, optimized using JOIN FETCH.
     *
     * @param id the practice session UUID
     * @return optional practice session containing initialized user, technology, and experience
     */
    @Query("SELECT s FROM PracticeSession s JOIN FETCH s.user u JOIN FETCH s.technology t JOIN FETCH s.experience e WHERE s.id = :id")
    Optional<PracticeSession> findByIdWithUserAndTechnologyAndExperience(@Param("id") UUID id);
}

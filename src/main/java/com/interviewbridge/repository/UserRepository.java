package com.interviewbridge.repository;

import com.interviewbridge.Enum.ApprovalStatus;
import com.interviewbridge.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing User database operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Finds a user by their unique email address.
     *
     * @param email the user email to search
     * @return an Optional containing the User if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user already exists with the given email address.
     *
     * @param email the email to verify
     * @return true if the email is already registered
     */
    boolean existsByEmail(String email);

    /**
     * Finds all users with a specific registration approval status.
     *
     * @param approvalStatus the status to search
     * @return a list of Users matching the status
     */
    List<User> findByApprovalStatus(ApprovalStatus approvalStatus);
}

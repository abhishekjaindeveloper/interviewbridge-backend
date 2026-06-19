package com.interviewbridge.repository;

import com.interviewbridge.Enum.ApprovalStatus;
import com.interviewbridge.Enum.Role;
import com.interviewbridge.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
     * Finds a user by their unique phone number.
     *
     * @param phoneNumber the user phone number to search
     * @return an Optional containing the User if found
     */
    Optional<User> findByPhoneNumber(String phoneNumber);

    /**
     * Checks if a user already exists with the given phone number.
     *
     * @param phoneNumber the phone number to verify
     * @return true if the phone number is already registered
     */
    boolean existsByPhoneNumber(String phoneNumber);

    /**
     * Checks if a user already exists with the given role.
     *
     * @param role the role to verify
     * @return true if a user with the given role exists
     */
    boolean existsByRole(Role role);

    /**
     * Finds all users with a specific registration approval status.
     *
     * @param approvalStatus the status to search
     * @return a list of Users matching the status
     */
    List<User> findByApprovalStatus(ApprovalStatus approvalStatus);

    /**
     * Finds all users matching optional status, active flag, and search query.
     */
    @Query("SELECT u FROM User u WHERE " +
           "u.email <> :loggedInEmail " +
           "AND u.approvalStatus <> com.interviewbridge.Enum.ApprovalStatus.REJECTED " +
           "AND (:search IS NULL OR :search = '' OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:approvalStatus IS NULL OR u.approvalStatus = :approvalStatus) " +
           "AND (:isActive IS NULL OR u.isActive = :isActive)")
    Page<User> findAllFilteredAndSearched(
        @Param("search") String search,
        @Param("approvalStatus") ApprovalStatus approvalStatus,
        @Param("isActive") Boolean isActive,
        @Param("loggedInEmail") String loggedInEmail,
        Pageable pageable
    );

    long countByApprovalStatusNot(ApprovalStatus approvalStatus);
    long countByIsActiveAndApprovalStatus(Boolean isActive, ApprovalStatus approvalStatus);
    long countByApprovalStatus(ApprovalStatus approvalStatus);
}

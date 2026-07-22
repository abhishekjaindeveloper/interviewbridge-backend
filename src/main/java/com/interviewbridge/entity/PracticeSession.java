package com.interviewbridge.entity;

import com.interviewbridge.enums.SessionStatus;
import com.interviewbridge.common.BaseEntity;
import com.interviewbridge.constants.EntityConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing an interview practice session.
 */
@Entity
@Table(name = EntityConstants.PracticeSession.TABLE_NAME, indexes = {
        @Index(name = EntityConstants.PracticeSession.IDX_SESSION_USER_ID, columnList = EntityConstants.PracticeSession.COL_USER_ID),
        @Index(name = EntityConstants.PracticeSession.IDX_SESSION_TECH_ID, columnList = EntityConstants.PracticeSession.COL_TECHNOLOGY_ID),
        @Index(name = EntityConstants.PracticeSession.IDX_SESSION_EXP_ID, columnList = EntityConstants.PracticeSession.COL_EXPERIENCE_ID)
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class PracticeSession extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = EntityConstants.PracticeSession.COL_ID, updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    private UUID id;

    @Version
    @Column(name = EntityConstants.PracticeSession.COL_VERSION, nullable = false)
    @lombok.Builder.Default
    private Integer version = 0;

    @NotNull
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = EntityConstants.PracticeSession.COL_USER_ID, nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = EntityConstants.PracticeSession.COL_TECHNOLOGY_ID, nullable = false)
    private TechnologyMaster technology;

    @NotNull
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = EntityConstants.PracticeSession.COL_EXPERIENCE_ID, nullable = false)
    private ExperienceMaster experience;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = EntityConstants.PracticeSession.COL_SESSION_STATUS, nullable = false)
    private SessionStatus sessionStatus;

    @NotNull
    @Column(name = EntityConstants.PracticeSession.COL_TOTAL_QUESTIONS, nullable = false)
    private Integer totalQuestions;

    @NotNull
    @Column(name = EntityConstants.PracticeSession.COL_COMPLETED_QUESTIONS, nullable = false)
    @lombok.Builder.Default
    private Integer completedQuestions = 0;

    @NotNull
    @Column(name = EntityConstants.PracticeSession.COL_AVERAGE_SCORE, nullable = false)
    @lombok.Builder.Default
    private Double averageScore = 0.0;

    @NotNull
    @Column(name = EntityConstants.PracticeSession.COL_STARTED_AT, nullable = false)
    private LocalDateTime startedAt;

    @Column(name = EntityConstants.PracticeSession.COL_COMPLETED_AT)
    private LocalDateTime completedAt;
}


package com.interviewbridge.entity;

import com.interviewbridge.enums.EvaluationStatus;
import com.interviewbridge.enums.QuestionStatus;
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
 * Entity representing an individual question generated within a practice session.
 */
@Entity
@Table(name = EntityConstants.PracticeQuestion.TABLE_NAME, indexes = {
        @Index(name = EntityConstants.PracticeQuestion.IDX_QUESTION_SESSION_ID, columnList = EntityConstants.PracticeQuestion.COL_SESSION_ID)
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class PracticeQuestion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = EntityConstants.PracticeQuestion.COL_ID, updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    private UUID id;

    @NotNull
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = EntityConstants.PracticeQuestion.COL_SESSION_ID, nullable = false)
    private PracticeSession practiceSession;

    @NotNull
    @Column(name = EntityConstants.PracticeQuestion.COL_QUESTION_NUMBER, nullable = false)
    private Integer questionNumber;

    @NotNull
    @Column(name = EntityConstants.PracticeQuestion.COL_QUESTION, nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(name = EntityConstants.PracticeQuestion.COL_REFERENCE_ANSWER, columnDefinition = "TEXT")
    private String referenceAnswer;

    @Column(name = EntityConstants.PracticeQuestion.COL_USER_ANSWER, columnDefinition = "TEXT")
    private String userAnswer;

    @Column(name = EntityConstants.PracticeQuestion.COL_TRANSLATED_ANSWER, columnDefinition = "TEXT")
    private String translatedAnswer;

    @Column(name = EntityConstants.PracticeQuestion.COL_IMPROVED_ANSWER, columnDefinition = "TEXT")
    private String improvedAnswer;

    @Column(name = EntityConstants.PracticeQuestion.COL_EXPLANATION, columnDefinition = "TEXT")
    private String explanation;

    @Column(name = EntityConstants.PracticeQuestion.COL_SCORE)
    private Integer score;

    @Column(name = EntityConstants.PracticeQuestion.COL_WHAT_WAS_CORRECT, columnDefinition = "TEXT")
    private String whatWasCorrect;

    @Column(name = EntityConstants.PracticeQuestion.COL_WHAT_WAS_MISSING, columnDefinition = "TEXT")
    private String whatWasMissing;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = EntityConstants.PracticeQuestion.COL_QUESTION_STATUS, nullable = false)
    private QuestionStatus questionStatus;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = EntityConstants.PracticeQuestion.COL_EVALUATION_STATUS, nullable = false)
    @lombok.Builder.Default
    private EvaluationStatus evaluationStatus = EvaluationStatus.PENDING;

    @Column(name = EntityConstants.PracticeQuestion.COL_EVALUATED_AT)
    private LocalDateTime evaluatedAt;
}


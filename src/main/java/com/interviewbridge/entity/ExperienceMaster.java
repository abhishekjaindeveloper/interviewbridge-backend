package com.interviewbridge.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

import com.interviewbridge.common.BaseEntity;
import com.interviewbridge.constants.EntityConstants;

/**
 * Entity representing an experience level master in the InterviewBridge
 * application.
 */
@Entity
@Table(name = EntityConstants.Experience.TABLE_NAME, uniqueConstraints = {
		@UniqueConstraint(name = EntityConstants.Experience.UQ_EXP_LABEL, columnNames = EntityConstants.Experience.COL_EXP_LABEL) }, indexes = {
				@Index(name = EntityConstants.Experience.IDX_EXP_LABEL, columnList = EntityConstants.Experience.COL_EXP_LABEL) })
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class ExperienceMaster extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = EntityConstants.Experience.COL_ID, updatable = false, nullable = false)
	@EqualsAndHashCode.Include
	private UUID id;

	@NotBlank(message = EntityConstants.Experience.MSG_EXP_LABEL_BLANK)
	@Size(max = EntityConstants.Experience.EXP_LABEL_MAX_LENGTH, message = EntityConstants.Experience.MSG_EXP_LABEL_SIZE)
	@Column(name = EntityConstants.Experience.COL_EXP_LABEL, nullable = false, length = EntityConstants.Experience.EXP_LABEL_MAX_LENGTH)
	private String experienceLabel;
}

package com.interviewbridge.entity;

import java.util.UUID;

import com.interviewbridge.common.BaseEntity;
import com.interviewbridge.constants.EntityConstants;

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

/**
 * Entity representing a technology domain/skill in the InterviewBridge
 * application.
 */
@Entity
@Table(name = EntityConstants.Technology.TABLE_NAME, uniqueConstraints = {
		@UniqueConstraint(name = EntityConstants.Technology.UQ_TECH_NAME, columnNames = EntityConstants.Technology.COL_TECH_NAME) }, indexes = {
				@Index(name = EntityConstants.Technology.IDX_TECH_NAME, columnList = EntityConstants.Technology.COL_TECH_NAME) })
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class TechnologyMaster extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = EntityConstants.Technology.COL_ID, updatable = false, nullable = false)
	@EqualsAndHashCode.Include
	private UUID id;

	@NotBlank(message = EntityConstants.Technology.MSG_TECH_NAME_BLANK)
	@Size(max = EntityConstants.Technology.TECH_NAME_MAX_LENGTH, message = EntityConstants.Technology.MSG_TECH_NAME_SIZE)
	@Column(name = EntityConstants.Technology.COL_TECH_NAME, nullable = false, length = EntityConstants.Technology.TECH_NAME_MAX_LENGTH)
	private String technologyName;

	@Size(max = EntityConstants.Technology.DESC_MAX_LENGTH, message = EntityConstants.Technology.MSG_DESC_SIZE)
	@Column(name = EntityConstants.Technology.COL_DESCRIPTION, length = EntityConstants.Technology.DESC_MAX_LENGTH)
	private String description;
}

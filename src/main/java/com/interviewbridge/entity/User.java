package com.interviewbridge.entity;

import java.util.UUID;

import com.interviewbridge.Enum.ApprovalStatus;
import com.interviewbridge.Enum.Role;
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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Entity representing a User in the InterviewBridge application.
 */
@Entity
@Table(name = EntityConstants.User.TABLE_NAME, uniqueConstraints = {
		@UniqueConstraint(name = EntityConstants.User.UQ_EMAIL, columnNames = EntityConstants.User.COL_EMAIL),
		@UniqueConstraint(name = EntityConstants.User.UQ_PHONE_NUMBER, columnNames = EntityConstants.User.COL_PHONE_NUMBER) }, indexes = {
				@Index(name = EntityConstants.User.IDX_EMAIL, columnList = EntityConstants.User.COL_EMAIL),
				@Index(name = EntityConstants.User.IDX_PHONE_NUMBER, columnList = EntityConstants.User.COL_PHONE_NUMBER),
				@Index(name = EntityConstants.User.IDX_TECHNOLOGY_ID, columnList = EntityConstants.User.COL_TECHNOLOGY_ID),
				@Index(name = EntityConstants.User.IDX_EXPERIENCE_ID, columnList = EntityConstants.User.COL_EXPERIENCE_ID) })
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = EntityConstants.User.COL_ID, updatable = false, nullable = false)
	@EqualsAndHashCode.Include
	private UUID id;

	@NotBlank(message = EntityConstants.User.MSG_NAME_BLANK)
	@Size(max = EntityConstants.User.NAME_MAX_LENGTH, message = EntityConstants.User.MSG_NAME_SIZE)
	@Column(name = EntityConstants.User.COL_NAME, nullable = false, length = EntityConstants.User.NAME_MAX_LENGTH)
	private String name;

	@NotBlank(message = EntityConstants.User.MSG_EMAIL_BLANK)
	@Email(message = EntityConstants.User.MSG_EMAIL_INVALID)
	@Size(max = EntityConstants.User.EMAIL_MAX_LENGTH, message = EntityConstants.User.MSG_EMAIL_SIZE)
	@Column(name = EntityConstants.User.COL_EMAIL, nullable = false, length = EntityConstants.User.EMAIL_MAX_LENGTH)
	private String email;

	@Size(max = EntityConstants.User.PHONE_NUMBER_MAX_LENGTH, message = EntityConstants.User.MSG_PHONE_NUMBER_SIZE)
	@Column(name = EntityConstants.User.COL_PHONE_NUMBER, length = EntityConstants.User.PHONE_NUMBER_MAX_LENGTH)
	private String phoneNumber;

	@Column(name = EntityConstants.User.COL_TERMS_ACCEPTED)
	private Boolean termsAccepted;

	@Column(name = EntityConstants.User.COL_TERMS_ACCEPTED_AT)
	private java.time.LocalDateTime termsAcceptedAt;

	@NotBlank(message = EntityConstants.User.MSG_PASSWORD_BLANK)
	@Size(min = EntityConstants.User.PASSWORD_MIN_LENGTH, max = EntityConstants.User.PASSWORD_MAX_LENGTH, message = EntityConstants.User.MSG_PASSWORD_SIZE)
	@Column(name = EntityConstants.User.COL_PASSWORD, nullable = false, length = EntityConstants.User.PASSWORD_MAX_LENGTH)
	@com.fasterxml.jackson.annotation.JsonIgnore
	private String password;

	@NotNull(message = EntityConstants.User.MSG_ROLE_REQUIRED)
	@Enumerated(EnumType.STRING)
	@Column(name = EntityConstants.User.COL_ROLE, nullable = false)
	private Role role;

	@NotNull(message = EntityConstants.User.MSG_APPROVAL_STATUS_REQUIRED)
	@Enumerated(EnumType.STRING)
	@Column(name = EntityConstants.User.COL_APPROVAL_STATUS, nullable = false)
	private ApprovalStatus approvalStatus;

	@ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
	@JoinColumn(name = EntityConstants.User.COL_TECHNOLOGY_ID)
	private TechnologyMaster technology;

	@ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
	@JoinColumn(name = EntityConstants.User.COL_EXPERIENCE_ID)
	private ExperienceMaster experience;

	@Column(name = EntityConstants.User.COL_REJECTION_REASON, length = EntityConstants.User.REJECTION_REASON_MAX_LENGTH)
	private String rejectionReason;

	@Column(name = EntityConstants.User.COL_REJECTED_AT)
	private java.time.LocalDateTime rejectedAt;

	@Column(name = EntityConstants.User.COL_REJECTED_BY)
	private String rejectedBy;
}

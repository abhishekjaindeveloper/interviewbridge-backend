package com.interviewbridge.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.interviewbridge.constants.EntityConstants;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

	@CreatedDate
	@Column(name = EntityConstants.Base.COL_CREATED_AT, nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = EntityConstants.Base.COL_UPDATED_AT, nullable = false)
	private LocalDateTime updatedAt;

	@CreatedBy
	@Column(name = EntityConstants.Base.COL_CREATED_BY, updatable = false)
	private String createdBy;

	@LastModifiedBy
	@Column(name = EntityConstants.Base.COL_UPDATED_BY)
	private String updatedBy;

	@NotNull(message = EntityConstants.Base.MSG_ACTIVE_REQUIRED)
	@Column(name = EntityConstants.Base.COL_IS_ACTIVE, nullable = false)
	@lombok.Builder.Default
	private Boolean isActive = true;
}

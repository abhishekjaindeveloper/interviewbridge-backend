package com.interviewbridge.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import com.interviewbridge.common.BaseEntity;
import com.interviewbridge.constants.EntityConstants;
import com.interviewbridge.enums.WorkMode;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = EntityConstants.Job.TABLE_NAME, indexes = {
    @Index(name = EntityConstants.Job.IDX_PROVIDER, columnList = EntityConstants.Job.COL_PROVIDER),
    @Index(name = EntityConstants.Job.IDX_EXTERNAL_JOB_ID, columnList = EntityConstants.Job.COL_EXTERNAL_JOB_ID)
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Job extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = EntityConstants.Job.COL_ID, updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    private UUID id;

    @NotBlank(message = EntityConstants.Job.MSG_PROVIDER_BLANK)
    @Column(name = EntityConstants.Job.COL_PROVIDER, nullable = false, length = EntityConstants.Job.PROVIDER_MAX_LENGTH)
    private String provider;

    @Column(name = EntityConstants.Job.COL_EXTERNAL_JOB_ID, length = EntityConstants.Job.EXTERNAL_JOB_ID_MAX_LENGTH)
    private String externalJobId;

    @NotBlank(message = EntityConstants.Job.MSG_COMPANY_BLANK)
    @Column(name = EntityConstants.Job.COL_COMPANY, nullable = false, length = EntityConstants.Job.COMPANY_MAX_LENGTH)
    private String company;

    @NotBlank(message = EntityConstants.Job.MSG_TITLE_BLANK)
    @Column(name = EntityConstants.Job.COL_TITLE, nullable = false, length = EntityConstants.Job.TITLE_MAX_LENGTH)
    private String title;

    @Column(name = EntityConstants.Job.COL_DESCRIPTION, columnDefinition = "TEXT")
    private String description;

    @Column(name = EntityConstants.Job.COL_LOCATION, length = EntityConstants.Job.LOCATION_MAX_LENGTH)
    private String location;

    @Column(name = EntityConstants.Job.COL_SALARY_MIN)
    private Double salaryMin;

    @Column(name = EntityConstants.Job.COL_SALARY_MAX)
    private Double salaryMax;

    @Column(name = EntityConstants.Job.COL_EMPLOYMENT_TYPE, length = EntityConstants.Job.EMPLOYMENT_TYPE_MAX_LENGTH)
    private String employmentType;

    @Enumerated(EnumType.STRING)
    @Column(name = EntityConstants.Job.COL_WORK_MODE, length = EntityConstants.Job.WORK_MODE_MAX_LENGTH)
    private WorkMode workMode;

    @Column(name = EntityConstants.Job.COL_APPLY_URL, length = EntityConstants.Job.APPLY_URL_MAX_LENGTH)
    private String applyUrl;

    @Column(name = EntityConstants.Job.COL_COMPANY_LOGO, length = EntityConstants.Job.COMPANY_LOGO_MAX_LENGTH)
    private String companyLogo;

    @Column(name = EntityConstants.Job.COL_TAGS, length = EntityConstants.Job.TAGS_MAX_LENGTH)
    private String tags;

    @Column(name = EntityConstants.Job.COL_POSTED_AT)
    private LocalDateTime postedAt;

    @Column(name = EntityConstants.Job.COL_FETCHED_AT)
    private LocalDateTime fetchedAt;

    @Column(name = EntityConstants.Job.COL_STATUS, length = EntityConstants.Job.STATUS_MAX_LENGTH)
    private String status;
}

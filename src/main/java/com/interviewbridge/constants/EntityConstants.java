package com.interviewbridge.constants;

public class EntityConstants {
	
	private EntityConstants() {
        // Private constructor to prevent instantiation
    }

    /**
     * Shared auditing constants.
     */
    public static final class Base {
        public static final String COL_CREATED_AT = "created_at";
        public static final String COL_UPDATED_AT = "updated_at";
        public static final String COL_CREATED_BY = "created_by";
        public static final String COL_UPDATED_BY = "updated_by";
        public static final String COL_IS_ACTIVE = "is_active";

        public static final String MSG_ACTIVE_REQUIRED = "Active status is required";
    }

    /**
     * User entity constants.
     */
    public static final class User {
        public static final String TABLE_NAME = "ib_user";
        public static final String COL_ID = "id";
        public static final String COL_NAME = "name";
        public static final String COL_EMAIL = "email";
        public static final String COL_PHONE_NUMBER = "phone_number";
        public static final String COL_TERMS_ACCEPTED = "terms_accepted";
        public static final String COL_TERMS_ACCEPTED_AT = "terms_accepted_at";
        public static final String COL_PASSWORD = "password";
        public static final String COL_ROLE = "role";
        public static final String COL_APPROVAL_STATUS = "approval_status";
        public static final String COL_REJECTION_REASON = "rejection_reason";
        public static final String COL_REJECTED_AT = "rejected_at";
        public static final String COL_REJECTED_BY = "rejected_by";

        public static final String UQ_EMAIL = "uq_ib_user_email";
        public static final String UQ_PHONE_NUMBER = "uq_ib_user_phone_number";
        public static final String IDX_EMAIL = "idx_ib_user_email";
        public static final String IDX_PHONE_NUMBER = "idx_ib_user_phone_number";
        public static final String IDX_TECHNOLOGY_ID = "idx_ib_user_technology_id";
        public static final String IDX_EXPERIENCE_ID = "idx_ib_user_experience_id";

        // Field Length Limits
        public static final int NAME_MAX_LENGTH = 100;
        public static final int REJECTION_REASON_MIN_LENGTH = 10;
        public static final int REJECTION_REASON_MAX_LENGTH = 500;
        public static final int EMAIL_MAX_LENGTH = 255;
        public static final int PHONE_NUMBER_MAX_LENGTH = 20;
        public static final int PASSWORD_MIN_LENGTH = 8;
        public static final int PASSWORD_MAX_LENGTH = 255;

        // Validation Error Messages
        public static final String MSG_NAME_BLANK = "Name cannot be blank";
        public static final String MSG_NAME_SIZE = "Name must not exceed 100 characters";
        public static final String MSG_EMAIL_BLANK = "Email cannot be blank";
        public static final String MSG_EMAIL_INVALID = "Email must be a valid email address";
        public static final String MSG_EMAIL_SIZE = "Email must not exceed 255 characters";
        public static final String MSG_PHONE_NUMBER_SIZE = "Phone number must not exceed 20 characters";
        public static final String MSG_PHONE_NUMBER_BLANK = "Phone number cannot be blank";
        public static final String MSG_PHONE_NUMBER_INVALID = "Please enter a valid 10-digit mobile number.";
        public static final String MSG_PASSWORD_BLANK = "Password cannot be blank";
        public static final String MSG_PASSWORD_SIZE = "Password must be between 8 and 255 characters";
        public static final String MSG_ROLE_REQUIRED = "Role is required";
        public static final String MSG_APPROVAL_STATUS_REQUIRED = "Approval status is required";
        public static final String MSG_IDENTIFIER_BLANK = "Email or Phone Number cannot be blank";
        public static final String MSG_REJECTION_REASON_BLANK = "Rejection reason cannot be blank";
        public static final String MSG_REJECTION_REASON_SIZE = "Rejection reason must be between 10 and 500 characters";

        public static final String COL_TECHNOLOGY_ID = "technology_id";
        public static final String COL_EXPERIENCE_ID = "experience_id";

        public static final String MSG_TECH_ID_REQUIRED = "Technology ID is required";
        public static final String MSG_EXP_ID_REQUIRED = "Experience ID is required";
    }

    /**
     * TechnologyMaster entity constants.
     */
    public static final class Technology {
        public static final String TABLE_NAME = "ib_technology_master";
        public static final String COL_ID = "id";
        public static final String COL_TECH_NAME = "technology_name";
        public static final String COL_DESCRIPTION = "description";

        public static final String UQ_TECH_NAME = "uq_ib_tech_name";
        public static final String IDX_TECH_NAME = "idx_ib_tech_name";

        // Field Length Limits
        public static final int TECH_NAME_MAX_LENGTH = 100;
        public static final int DESC_MAX_LENGTH = 500;

        // Validation Error Messages
        public static final String MSG_TECH_NAME_BLANK = "Technology name cannot be blank";
        public static final String MSG_TECH_NAME_SIZE = "Technology name must not exceed 100 characters";
        public static final String MSG_DESC_SIZE = "Description must not exceed 500 characters";
    }

    /**
     * ExperienceMaster entity constants.
     */
    public static final class Experience {
        public static final String TABLE_NAME = "ib_experience_master";
        public static final String COL_ID = "id";
        public static final String COL_EXP_LABEL = "experience_label";

        public static final String UQ_EXP_LABEL = "uq_ib_experience_label";
        public static final String IDX_EXP_LABEL = "idx_ib_experience_label";

        // Field Length Limits
        public static final int EXP_LABEL_MAX_LENGTH = 50;

        // Validation Error Messages
        public static final String MSG_EXP_LABEL_BLANK = "Experience label cannot be blank";
        public static final String MSG_EXP_LABEL_SIZE = "Experience label must not exceed 50 characters";
    }

    /**
     * PracticeSession entity constants.
     */
    public static final class PracticeSession {
        public static final String TABLE_NAME = "ib_practice_session";
        public static final String COL_ID = "id";
        public static final String COL_USER_ID = "user_id";
        public static final String COL_TECHNOLOGY_ID = "technology_id";
        public static final String COL_EXPERIENCE_ID = "experience_id";
        public static final String COL_SESSION_STATUS = "session_status";
        public static final String COL_TOTAL_QUESTIONS = "total_questions";
        public static final String COL_COMPLETED_QUESTIONS = "completed_questions";
        public static final String COL_AVERAGE_SCORE = "average_score";
        public static final String COL_STARTED_AT = "started_at";
        public static final String COL_COMPLETED_AT = "completed_at";
        public static final String COL_VERSION = "version";

        public static final String IDX_SESSION_USER_ID = "idx_ib_practice_session_user_id";
        public static final String IDX_SESSION_TECH_ID = "idx_ib_practice_session_technology_id";
        public static final String IDX_SESSION_EXP_ID = "idx_ib_practice_session_experience_id";

        public static final int MIN_TOTAL_QUESTIONS = 1;
        public static final int MAX_TOTAL_QUESTIONS = 20;
        public static final int DEFAULT_TOTAL_QUESTIONS = 10;
        public static final String MSG_TOTAL_QUESTIONS_MIN = "Total questions must be at least 1";
        public static final String MSG_TOTAL_QUESTIONS_MAX = "Total questions must not exceed 20";
    }

    /**
     * PracticeQuestion entity constants.
     */
    public static final class PracticeQuestion {
        public static final String TABLE_NAME = "ib_practice_question";
        public static final String COL_ID = "id";
        public static final String COL_SESSION_ID = "practice_session_id";
        public static final String COL_QUESTION_NUMBER = "question_number";
        public static final String COL_QUESTION = "question";
        public static final String COL_USER_ANSWER = "user_answer";
        public static final String COL_TRANSLATED_ANSWER = "translated_answer";
        public static final String COL_IMPROVED_ANSWER = "improved_answer";
        public static final String COL_EXPLANATION = "explanation";
        public static final String COL_SCORE = "score";
        public static final String COL_QUESTION_STATUS = "question_status";

        public static final String IDX_QUESTION_SESSION_ID = "idx_ib_practice_question_session_id";

        // Answer constraints
        public static final int ANSWER_MAX_LENGTH = 5000;
        public static final String MSG_ANSWER_BLANK = "Answer cannot be blank";
        public static final String MSG_ANSWER_SIZE = "Answer must not exceed 5000 characters";

        // Evaluation constraints
        public static final String COL_EVALUATION_STATUS = "evaluation_status";
        public static final String COL_EVALUATED_AT = "evaluated_at";
        public static final int MIN_SCORE = 0;
        public static final int MAX_SCORE = 10;
    }
}

CREATE TABLE ib_job (
    id UUID PRIMARY KEY,
    provider VARCHAR(50) NOT NULL,
    external_job_id VARCHAR(100),
    company VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    location VARCHAR(100),
    salary_min DOUBLE PRECISION,
    salary_max DOUBLE PRECISION,
    employment_type VARCHAR(50),
    work_mode VARCHAR(20),
    apply_url VARCHAR(500),
    company_logo VARCHAR(500),
    tags VARCHAR(255),
    posted_at TIMESTAMP,
    fetched_at TIMESTAMP,
    status VARCHAR(20),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_ib_job_provider ON ib_job(provider);
CREATE INDEX idx_ib_job_external_id ON ib_job(external_job_id);

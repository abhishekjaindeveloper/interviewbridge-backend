ALTER TABLE ib_user ADD COLUMN preferred_job_role VARCHAR(100);
ALTER TABLE ib_user ADD COLUMN preferred_location VARCHAR(100);
ALTER TABLE ib_user ADD COLUMN preferred_work_mode VARCHAR(20);
ALTER TABLE ib_user ADD COLUMN expected_salary DOUBLE PRECISION;
ALTER TABLE ib_user ADD COLUMN job_alert_enabled BOOLEAN DEFAULT FALSE;

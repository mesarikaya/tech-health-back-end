CREATE TABLE IF NOT EXISTS public.attachment (
    id BIGINT NOT NULL,
    file_name VARCHAR(1000),
    data BYTEA,
    file_type VARCHAR(50),
    file_size BIGINT,
    create_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    update_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    site_id BIGINT NOT NULL,
    assessment_criteria_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_attachment_site_id
                         FOREIGN KEY(site_id)
                         REFERENCES site(id)
                         ON DELETE CASCADE
                         ON UPDATE CASCADE,
    CONSTRAINT fk_attachment_assessment_criteria_id
                         FOREIGN KEY(assessment_criteria_id)
                         REFERENCES assessment_criteria(id)
                         ON DELETE CASCADE
                         ON UPDATE CASCADE
);
CREATE SEQUENCE IF NOT EXISTS public.sequence_generator
    START WITH 1050
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS public.region (
    id BIGINT NOT NULL,
    code VARCHAR(255) UNIQUE NOT NULL,
    description VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.country (
    id BIGINT NOT NULL,
    code VARCHAR(255) UNIQUE NOT NULL,
    description VARCHAR(255) NOT NULL,
    region_id bigint,
    PRIMARY KEY (id),
    CONSTRAINT fk_region
          FOREIGN KEY(region_id)
    	  REFERENCES region(id)
    	  ON DELETE CASCADE
    	  ON UPDATE CASCADE
);


CREATE TABLE IF NOT EXISTS public.enterprise (
    id BIGINT NOT NULL,
    code VARCHAR(255) UNIQUE NOT NULL,
    description VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.business_unit_group (
    id BIGINT NOT NULL,
    code VARCHAR(255) UNIQUE NOT NULL,
    description VARCHAR(255) NOT NULL,
    enterprise_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_enterprise
              FOREIGN KEY(enterprise_id)
              REFERENCES enterprise(id)
              ON DELETE CASCADE
              ON UPDATE CASCADE
);


CREATE TABLE IF NOT EXISTS public.business_unit (
    id BIGINT NOT NULL,
    code VARCHAR(255) UNIQUE NOT NULL,
    description VARCHAR(255) NOT NULL,
    business_unit_group_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_business_unit_group
              FOREIGN KEY(business_unit_group_id)
              REFERENCES business_unit_group(id)
              ON DELETE CASCADE
              ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS public.investment_criticality (
    id BIGINT NOT NULL,
    description VARCHAR(255) UNIQUE NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.cost_range (
    id BIGINT NOT NULL,
    description VARCHAR(255) UNIQUE NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.recommendation_status (
    id BIGINT NOT NULL,
    description VARCHAR(255) UNIQUE NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.tech_domain (
    id BIGINT NOT NULL,
    description VARCHAR(255) UNIQUE NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.tech_area (
    id BIGINT NOT NULL,
    description VARCHAR(255) UNIQUE NOT NULL,
    domain_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_tech_area_domain_id
                  FOREIGN KEY(domain_id)
                  REFERENCES tech_domain(id)
                  ON DELETE CASCADE
                  ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS public.site (
    id BIGINT NOT NULL,
    description VARCHAR(255) UNIQUE NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    active BOOLEAN,
    comment VARCHAR(255),
    country_id BIGINT,
    business_unit_id BIGINT,
    investment_criticality_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_site_country_id
                      FOREIGN KEY(country_id)
                      REFERENCES country(id)
                      ON DELETE CASCADE
                      ON UPDATE CASCADE,
    CONSTRAINT fk_business_unit_id
                      FOREIGN KEY(business_unit_id)
                      REFERENCES business_unit(id)
                      ON DELETE CASCADE
                      ON UPDATE CASCADE,
    CONSTRAINT fk_investment_criticality_id
                      FOREIGN KEY(investment_criticality_id)
                      REFERENCES investment_criticality(id)
                      ON DELETE CASCADE
                      ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS public.category (
    id BIGINT NOT NULL,
    description VARCHAR(255) UNIQUE NOT NULL,
    tech_area_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_tech_area_id
                          FOREIGN KEY(tech_area_id)
                          REFERENCES tech_area(id)
                          ON DELETE CASCADE
                          ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS public.assessment_criteria (
    id BIGINT NOT NULL,
    description VARCHAR(255) UNIQUE NOT NULL,
    active_flag DOUBLE PRECISION,
    category_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_category_id
                          FOREIGN KEY(category_id)
                          REFERENCES category(id)
                          ON DELETE CASCADE
                          ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS public.cost_to_fix (
    id BIGINT NOT NULL,
    site_id BIGINT,
    cost_range_id BIGINT,
    category_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_cost_to_fix_site_id
                             FOREIGN KEY(site_id)
                             REFERENCES site(id)
                             ON DELETE CASCADE
                             ON UPDATE CASCADE,
        CONSTRAINT fk_cost_to_fix_category_id
                             FOREIGN KEY(category_id)
                             REFERENCES category(id)
                             ON DELETE CASCADE
                             ON UPDATE CASCADE,
       CONSTRAINT fk_cost_to_fix_cost_range_id
                             FOREIGN KEY(cost_range_id)
                             REFERENCES cost_range(id)
                             ON DELETE CASCADE
                             ON UPDATE CASCADE
    );

CREATE TABLE IF NOT EXISTS public.assessment_result (
    id BIGINT NOT NULL,
    score DOUBLE PRECISION,
    recommendation_text VARCHAR(1000),
    notes VARCHAR(1000),
    create_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    update_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    site_id BIGINT,
    assessment_criteria_id BIGINT,
    recommendation_status_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_assessment_result_site_id
                         FOREIGN KEY(site_id)
                         REFERENCES site(id)
                         ON DELETE CASCADE
                         ON UPDATE CASCADE,
    CONSTRAINT fk_assessment_result_assessment_criteria_id
                         FOREIGN KEY(assessment_criteria_id)
                         REFERENCES assessment_criteria(id)
                         ON DELETE CASCADE
                         ON UPDATE CASCADE,
   CONSTRAINT fk_assessment_result_recommendation_status_id
                         FOREIGN KEY(recommendation_status_id)
                         REFERENCES recommendation_status(id)
                         ON DELETE CASCADE
                         ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS public.site_assessment_criteria (
    id BIGINT NOT NULL,
    site_id BIGINT NOT NULL,
    assessment_criteria_id BIGINT NOT NULL ,
    active_flag DOUBLE PRECISION,
    PRIMARY KEY (id),
    UNIQUE(site_id, assessment_criteria_id),
    CONSTRAINT fk_site_assessment_criteria_site_id
                         FOREIGN KEY(site_id)
                         REFERENCES site(id)
                         ON DELETE CASCADE
                         ON UPDATE CASCADE,
    CONSTRAINT fk_site_assessment_criteria_assessment_criteria_id
                         FOREIGN KEY(assessment_criteria_id)
                         REFERENCES assessment_criteria(id)
                         ON DELETE CASCADE
                         ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS public.assessment_result_log (
    id BIGINT NOT NULL,
    log_date  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    old_score DOUBLE PRECISION,
    new_score DOUBLE PRECISION,
    old_recommendation_description VARCHAR(255),
    new_recommendation_description VARCHAR(255),
    old_notes VARCHAR(255),
    new_notes VARCHAR(255),
    create_date  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    update_date  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    site_id BIGINT,
    assessment_criteria_id BIGINT,
    old_recommendation_status_id BIGINT,
    new_recommendation_status_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_assessment_result_log_site_id
                             FOREIGN KEY(site_id)
                             REFERENCES site(id)
                             ON DELETE CASCADE
                             ON UPDATE CASCADE,
    CONSTRAINT fk_assessment_result_log_assessment_criteria_id
                             FOREIGN KEY(assessment_criteria_id)
                             REFERENCES assessment_criteria(id)
                             ON DELETE CASCADE
                             ON UPDATE CASCADE,
    CONSTRAINT fk_assessment_result_log_new_recommendation_status_id
                             FOREIGN KEY(new_recommendation_status_id)
                             REFERENCES recommendation_status(id)
                             ON DELETE CASCADE
                             ON UPDATE CASCADE,
    CONSTRAINT fk_assessment_result_log_old_recommendation_status_id
                             FOREIGN KEY(old_recommendation_status_id)
                             REFERENCES recommendation_status(id)
                             ON DELETE CASCADE
                             ON UPDATE CASCADE
);

ALTER TABLE IF EXISTS  public.assessment_result_log ALTER COLUMN old_recommendation_description TYPE VARCHAR(1000);

ALTER TABLE IF EXISTS  public.assessment_result_log ALTER COLUMN new_recommendation_description TYPE VARCHAR(1000);

ALTER TABLE IF EXISTS  public.assessment_result_log ALTER COLUMN old_notes TYPE VARCHAR(1000);

ALTER TABLE IF EXISTS  public.assessment_result_log ALTER COLUMN new_notes TYPE VARCHAR(1000);


ALTER TABLE IF EXISTS  public.site
DROP CONSTRAINT IF EXISTS fk_site_country_id;

ALTER TABLE IF EXISTS  public.site
DROP CONSTRAINT IF EXISTS fk_business_unit_id;

ALTER TABLE IF EXISTS  public.site
DROP CONSTRAINT IF EXISTS fk_investment_criticality_id;

DROP TABLE IF EXISTS public.country;

DROP TABLE IF EXISTS public.region;

ALTER TABLE IF EXISTS public.business_unit
DROP CONSTRAINT IF EXISTS fk_business_unit_group;

ALTER TABLE IF EXISTS  public.business_unit_group
DROP CONSTRAINT IF EXISTS fk_enterprise;

DROP TABLE IF EXISTS public.business_unit;

DROP TABLE IF EXISTS public.business_unit_group;

DROP TABLE IF EXISTS public.enterprise;

ALTER TABLE public.site DROP COLUMN IF EXISTS country_id;
ALTER TABLE IF EXISTS public.site ADD COLUMN country VARCHAR(255);

ALTER TABLE public.site DROP COLUMN IF EXISTS business_unit_id;
ALTER TABLE IF EXISTS public.site ADD COLUMN business_unit VARCHAR(255);

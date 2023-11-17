DROP TABLE IF EXISTS public.country;
DROP TABLE IF EXISTS public.region;
DROP TABLE IF EXISTS public.cost_to_fix;
DROP TABLE IF EXISTS public.cost_range;
DROP TABLE IF EXISTS public.assessment_result;
DROP TABLE IF EXISTS public.recommendation_status;
DROP TABLE IF EXISTS public.attachment;
DROP TABLE IF EXISTS public.assessment_criteria;
DROP TABLE IF EXISTS public.category;
DROP TABLE IF EXISTS public.tech_area;
DROP TABLE IF EXISTS public.tech_domain;
DROP TABLE IF EXISTS public.site;
DROP TABLE IF EXISTS public.investment_criticality;

CREATE SEQUENCE IF NOT EXISTS public.sequence_generator
    START WITH 50050
    INCREMENT BY 100
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
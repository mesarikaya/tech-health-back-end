DROP TABLE IF EXISTS country;
DROP TABLE IF EXISTS region;

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


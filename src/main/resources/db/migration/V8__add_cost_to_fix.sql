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

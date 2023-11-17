--ADD tech_standard_description field to assessment criteria for tooltip to show cargill standards
ALTER TABLE IF EXISTS public.assessment_criteria ADD COLUMN tech_standard_description VARCHAR(255);
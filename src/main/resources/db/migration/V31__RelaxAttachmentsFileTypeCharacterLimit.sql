--SET tech_standard_description to TestTechStandard for testing purposes
ALTER TABLE IF EXISTS public.attachment ALTER COLUMN file_type TYPE VARCHAR(255);
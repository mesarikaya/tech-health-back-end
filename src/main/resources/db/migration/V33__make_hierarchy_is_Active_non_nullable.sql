--ADd Not NULL constraint to hierarchy columns
ALTER TABLE IF EXISTS public.tech_domain ALTER COLUMN is_active SET NOT NULL;
ALTER TABLE IF EXISTS public.tech_area ALTER COLUMN is_active SET NOT NULL;
ALTER TABLE IF EXISTS public.category ALTER COLUMN is_active SET NOT NULL;
ALTER TABLE IF EXISTS public.assessment_criteria ALTER COLUMN is_active SET NOT NULL;

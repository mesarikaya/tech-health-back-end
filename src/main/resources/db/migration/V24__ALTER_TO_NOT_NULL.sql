ALTER TABLE IF EXISTS public.assessment_result ALTER COLUMN site_id SET NOT NULL;
ALTER TABLE IF EXISTS public.assessment_result ALTER COLUMN assessment_criteria_id SET NOT NULL;
ALTER TABLE IF EXISTS public.assessment_result ALTER COLUMN recommendation_status_id SET NOT NULL;

ALTER TABLE IF EXISTS public.cost_to_fix ALTER COLUMN cost_range_id SET NOT NULL;
ALTER TABLE IF EXISTS public.cost_to_fix ALTER COLUMN category_id SET NOT NULL;
ALTER TABLE IF EXISTS public.cost_to_fix ALTER COLUMN site_id SET NOT NULL;

ALTER TABLE IF EXISTS public.assessment_criteria ALTER COLUMN category_id SET NOT NULL;
ALTER TABLE IF EXISTS public.category ALTER COLUMN tech_area_id SET NOT NULL;
ALTER TABLE IF EXISTS public.tech_area ALTER COLUMN domain_id SET NOT NULL;

ALTER TABLE IF EXISTS public.site ALTER COLUMN investment_criticality_id SET NOT NULL;

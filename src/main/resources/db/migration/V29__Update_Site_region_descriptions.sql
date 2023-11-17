--SET tech_standard_description to TestTechStandard for testing purposes
UPDATE public.site
SET region = 'LA'
WHERE region = 'LATAM';

UPDATE public.site
SET region = 'AP'
WHERE region = 'APAC';
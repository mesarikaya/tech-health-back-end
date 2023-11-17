--DELETE ALL TABLE DATA TO ACTIVATE REPLACEMENT WITH PROD DATA
DELETE FROM public.cost_to_fix;
DELETE FROM public.cost_range;
DELETE FROM public.assessment_result;
DELETE FROM public.recommendation_status;
DELETE FROM public.assessment_criteria;
DELETE FROM public.category;
DELETE FROM public.tech_area;
DELETE FROM public.tech_domain;
DELETE FROM public.site;
DELETE FROM public.investment_criticality;
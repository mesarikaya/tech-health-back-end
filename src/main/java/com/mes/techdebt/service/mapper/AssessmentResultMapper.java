package com.mes.techdebt.service.mapper;

import com.mes.techdebt.domain.RecommendationStatus;
import com.mes.techdebt.service.dto.AssessmentResultDTO;
import com.mes.techdebt.service.dto.RecommendationStatusDTO;
import com.mes.techdebt.domain.AssessmentCriteria;
import com.mes.techdebt.domain.AssessmentResult;
import com.mes.techdebt.domain.Site;
import com.mes.techdebt.service.dto.AssessmentCriteriaDTO;
import com.mes.techdebt.service.dto.SiteDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link AssessmentResult} and its DTO {@link AssessmentResultDTO}.
 */
@Mapper(componentModel = "spring", uses=DateMapper.class)
public interface AssessmentResultMapper extends EntityMapper<AssessmentResultDTO, AssessmentResult> {
    @Mapping(target = "assessmentCriteria", source = "assessmentCriteria", qualifiedByName = "assessmentCriteriaId")
    @Mapping(target = "site", source = "site", qualifiedByName = "siteId")
    @Mapping(target = "recommendationStatus", source = "recommendationStatus", qualifiedByName = "recommendationStatusId")
    AssessmentResultDTO toDto(AssessmentResult s);

    @Named("assessmentCriteriaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "techStandardDescription", source = "techStandardDescription")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "isActive", source = "isActive")
    AssessmentCriteriaDTO toDtoAssessmentCriteriaId(AssessmentCriteria assessmentCriteria);

    @Named("siteId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "mdmSiteId", source = "mdmSiteId")
    @Mapping(target = "mdmSiteName", source = "mdmSiteName")
    @Mapping(target = "comment", source = "comment")
    @Mapping(target = "isActive", source = "isActive")
    @Mapping(target = "region", source = "region")
    @Mapping(target = "enterprise", source = "enterprise")
    @Mapping(target = "businessGroup", source = "businessGroup")
    @Mapping(target = "reportingUnit", source = "reportingUnit")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "country", source = "country")
    @Mapping(target = "countryCode", source = "countryCode")
    @Mapping(target = "city", source = "city")
    @Mapping(target = "state", source = "state")
    @Mapping(target = "latitude", source = "latitude")
    @Mapping(target = "longitude", source = "longitude")
    @Mapping(target = "investmentCriticality", source = "investmentCriticality")
    SiteDTO toDtoSiteId(Site site);

    @Named("recommendationStatusId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "description", source = "description")
    RecommendationStatusDTO toDtoRecommendationStatusId(RecommendationStatus recommendationStatus);
}

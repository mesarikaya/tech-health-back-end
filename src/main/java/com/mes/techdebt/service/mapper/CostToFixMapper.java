package com.mes.techdebt.service.mapper;

import com.mes.techdebt.domain.Category;
import com.mes.techdebt.domain.CostRange;
import com.mes.techdebt.service.dto.CategoryDTO;
import com.mes.techdebt.service.dto.CostRangeDTO;
import com.mes.techdebt.domain.CostToFix;
import com.mes.techdebt.domain.Site;
import com.mes.techdebt.service.dto.CostToFixDTO;
import com.mes.techdebt.service.dto.SiteDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link CostToFix} and its DTO {@link CostToFixDTO}.
 */
@Mapper(componentModel = "spring")
public interface CostToFixMapper extends EntityMapper<CostToFixDTO, CostToFix> {
    @Mapping(target = "site", source = "site", qualifiedByName = "siteId")
    @Mapping(target = "costRange", source = "costRange", qualifiedByName = "costRangeId")
    @Mapping(target = "category", source = "category", qualifiedByName = "categoryId")
    CostToFixDTO toDto(CostToFix s);

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

    @Named("costRangeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "description", source = "description")
    CostRangeDTO toDtoCostRangeId(CostRange costRange);

    @Named("categoryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "techArea", source = "techArea")
    @Mapping(target = "isActive", source = "isActive")
    CategoryDTO toDtoCategoryId(Category category);
}

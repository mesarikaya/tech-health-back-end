package com.mes.techdebt.service.mapper;

import com.mes.techdebt.domain.InvestmentCriticality;
import com.mes.techdebt.domain.Site;
import com.mes.techdebt.service.dto.InvestmentCriticalityDTO;
import com.mes.techdebt.service.dto.SiteDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * Mapper for the entity {@link Site} and its DTO {@link SiteDTO}.
 */
@Mapper(componentModel = "spring")
public interface SiteMapper extends EntityMapper<SiteDTO, Site> {
    @Mapping(target = "investmentCriticality", source = "investmentCriticality", qualifiedByName = "investmentCriticalityId")
    SiteDTO toDto(Site s);

    @Mapping(target = "investmentCriticality", source = "investmentCriticality", qualifiedByName = "investmentCriticalityId")
    List<SiteDTO> toDto(List<Site> s);

    @Named("investmentCriticalityId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "description", source = "description")
    InvestmentCriticalityDTO toDtoInvestmentCriticalityId(InvestmentCriticality investmentCriticality);
}

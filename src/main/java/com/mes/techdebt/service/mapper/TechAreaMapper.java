package com.mes.techdebt.service.mapper;

import com.mes.techdebt.domain.TechArea;
import com.mes.techdebt.domain.TechDomain;
import com.mes.techdebt.service.dto.TechAreaDTO;
import com.mes.techdebt.service.dto.TechDomainDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link TechArea} and its DTO {@link TechAreaDTO}.
 */
@Mapper(componentModel = "spring")
public interface TechAreaMapper extends EntityMapper<TechAreaDTO, TechArea> {
    @Mapping(target = "domain", source = "domain", qualifiedByName = "techDomainId")
    TechAreaDTO toDto(TechArea s);

    @Named("techDomainId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "isActive", source = "isActive")
    TechDomainDTO toDtoTechDomainId(TechDomain techDomain);
}

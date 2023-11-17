package com.mes.techdebt.service.mapper;

import com.mes.techdebt.domain.TechDomain;
import com.mes.techdebt.service.dto.TechDomainDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link TechDomain} and its DTO {@link TechDomainDTO}.
 */
@Mapper(componentModel = "spring")
public interface TechDomainMapper extends EntityMapper<TechDomainDTO, TechDomain> {}

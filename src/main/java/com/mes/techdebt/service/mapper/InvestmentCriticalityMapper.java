package com.mes.techdebt.service.mapper;

import com.mes.techdebt.domain.InvestmentCriticality;
import com.mes.techdebt.service.dto.InvestmentCriticalityDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link InvestmentCriticality} and its DTO {@link InvestmentCriticalityDTO}.
 */
@Mapper(componentModel = "spring")
public interface InvestmentCriticalityMapper extends EntityMapper<InvestmentCriticalityDTO, InvestmentCriticality> {}

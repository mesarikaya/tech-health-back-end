package com.mes.techdebt.service.mapper;

import com.mes.techdebt.domain.CostRange;
import com.mes.techdebt.service.dto.CostRangeDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link CostRange} and its DTO {@link CostRangeDTO}.
 */
@Mapper(componentModel = "spring")
public interface CostRangeMapper extends EntityMapper<CostRangeDTO, CostRange> {}

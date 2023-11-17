package com.mes.techdebt.service.mapper;

import com.mes.techdebt.domain.RecommendationStatus;
import com.mes.techdebt.service.dto.RecommendationStatusDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link RecommendationStatus} and its DTO {@link RecommendationStatusDTO}.
 */
@Mapper(componentModel = "spring")
public interface RecommendationStatusMapper extends EntityMapper<RecommendationStatusDTO, RecommendationStatus> {}

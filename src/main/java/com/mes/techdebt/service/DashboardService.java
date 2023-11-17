package com.mes.techdebt.service;

import com.mes.techdebt.service.dto.AssessmentResultDTO;
import com.mes.techdebt.web.rest.request.DashboardRequestDTO;
import com.mes.techdebt.web.rest.request.DashboardResponseDTO;
import com.mes.techdebt.web.rest.request.HierarchyResponseDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link DashboardResponseDTO}.
 */
public interface DashboardService {
    /**
     * Get dashboardResponse by site.
     *
     * @param dashboardRequest entities to search from.
     * @return the persisted entity.
     */
    List<DashboardResponseDTO> getAssessmentResult(DashboardRequestDTO dashboardRequest,
                                                   List<AssessmentResultDTO> assessmentResultDTOOptional);

    List<HierarchyResponseDTO> getHierarchy(Optional<Boolean> isOnlyActive);
}

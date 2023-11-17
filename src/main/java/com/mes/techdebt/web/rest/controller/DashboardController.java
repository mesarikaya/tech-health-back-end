package com.mes.techdebt.web.rest.controller;

import com.mes.techdebt.service.AssessmentResultService;
import com.mes.techdebt.service.DashboardService;
import com.mes.techdebt.service.dto.AssessmentResultDTO;
import com.mes.techdebt.web.rest.request.DashboardRequestDTO;
import com.mes.techdebt.web.rest.request.DashboardResponseDTO;
import com.mes.techdebt.web.rest.request.HierarchyResponseDTO;
import com.mes.techdebt.domain.AssessmentResult;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link AssessmentResult}.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Read') or " +
        "hasAuthority('APPROLE_TechHealth_User_Write') or " +
        "hasAuthority('APPROLE_TechHealth_User_Admin')")
public class DashboardController {

    private static final String ENTITY_NAME = "dashboardData";
    @Value("${spring.application.name}")
    private String applicationName;
    private final AssessmentResultService assessmentResultService;
    private final DashboardService dashboardService;

    /**
     * {@code POST  /dashboard-data/site} : get the "dashboard-data" DashboardResponseDTO and attachments.
     *
     * @param dashboardRequest the request details to retrieve DashboardRequestDTO.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the assessmentResultDTO, or with status {@code 404 (Not Found)}.
     */
    @PostMapping(path="/dashboard-data/site", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DashboardResponseDTO>> getAssessmentResult(@Valid @RequestBody DashboardRequestDTO dashboardRequest) {
        log.debug("REST request to get DashboardData by site: {}", dashboardRequest.getName());
        Optional<List<AssessmentResultDTO>> assessmentResultDTOOptional = assessmentResultService.findBySiteName(dashboardRequest.getName());

        if (!assessmentResultDTOOptional.isPresent())  return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        List<DashboardResponseDTO> response = dashboardService.getAssessmentResult(dashboardRequest, assessmentResultDTOOptional.get());

        return ResponseEntity.ok().body(response);
    }

    /**
     * {@code GET  /dashboard-data/hierarchy} : get the "dashboard data" hierarchy.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the assessmentResultDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping(path="/dashboard-data/hierarchy", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<HierarchyResponseDTO>> getHierarchy(@RequestParam(required = false, defaultValue = "false") Optional<Boolean> isOnlyActive) {
        log.debug("REST request to get Hierarchy for Filters");
        List<HierarchyResponseDTO> response = dashboardService.getHierarchy(isOnlyActive);
        return ResponseEntity.ok().body(response);
    }

}

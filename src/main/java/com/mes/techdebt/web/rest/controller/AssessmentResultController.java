package com.mes.techdebt.web.rest.controller;

import com.cargill.techdebt.service.*;
import com.mes.techdebt.service.dto.AssessmentResultDTO;
import com.mes.techdebt.web.rest.errors.BadRequestAlertException;
import com.mes.techdebt.web.rest.request.DashboardRequestDTO;
import com.mes.techdebt.domain.AssessmentResult;
import com.mes.techdebt.service.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
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
public class AssessmentResultController {

    private static final String ENTITY_NAME = "assessmentResult";
    @Value("${spring.application.name}")
    private String applicationName;

    private final AssessmentResultService assessmentResultService;
    private final AssessmentCriteriaService assessmentCriteriaService;
    private final CategoryService categoryService;
    private final TechAreaService techAreaService;
    private final TechDomainService techDomainService;

    /**
     * {@code GET  /assessment-results} : get all the assessmentResult.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of assessmentResult in body.
     */
    @GetMapping(path="/assessment-results", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AssessmentResultDTO>> getAllAssessmentResult(@ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of AssessmentResult");
        Page<AssessmentResultDTO> page = assessmentResultService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
	
    /**
     * {@code GET  /assessment-results/:id} : get the "id" assessmentResult.
     *
     * @param id the id of the assessmentResultDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the assessmentResultDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping(path="/assessment-results/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AssessmentResultDTO> getAssessmentResult(@PathVariable Long id) {
        log.debug("REST request to get AssessmentResult: {}", id);
        Optional<AssessmentResultDTO> assessmentResultDTO = assessmentResultService.findOne(id);
        return ResponseUtil.wrapOrNotFound(assessmentResultDTO);
    }

    /**
     * {@code GET  /assessment-results/site} : get the "site" assessmentResult.
     *
     * @param dashboardRequest the request details to retrieve assessmentResultDTO.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the assessmentResultDTO, or with status {@code 404 (Not Found)}.
     */
    @PostMapping(path="/assessment-results/site", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AssessmentResultDTO>> getAssessmentResult(@Valid @RequestBody DashboardRequestDTO dashboardRequest) {
        log.debug("REST request to get AssessmentResult by site: {}", dashboardRequest.getName());
        Optional<List<AssessmentResultDTO>> assessmentResultDTO = assessmentResultService.findBySiteName(dashboardRequest.getName());
        return ResponseUtil.wrapOrNotFound(assessmentResultDTO);
    }

    /**
     * {@code POST  /assessment-results} : Create a new assessmentResult.
     *
     * @param assessmentResultDTO the assessmentResultDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new assessmentResultDTO, or with status {@code 400 (Bad Request)} if the assessmentResult has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Write') or hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PostMapping(path="/assessment-results", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AssessmentResultDTO> createAssessmentResult(@RequestBody AssessmentResultDTO assessmentResultDTO) throws URISyntaxException {
        log.debug("REST request to save AssessmentResult: {}", assessmentResultDTO);
        if (assessmentResultDTO.getId() != null) {
            throw new BadRequestAlertException("A new assessmentResult cannot already have an existing id", ENTITY_NAME, "idexists");
        }

        if (assessmentResultDTO.getSite().getId() == null) {
            throw new BadRequestAlertException("A new assessmentResult must have a site id", ENTITY_NAME, "idnotexists");
        }

        if (assessmentResultDTO.getAssessmentCriteria().getId() == null) {
            throw new BadRequestAlertException("A new assessmentResult must have a assessmentCriteria id", ENTITY_NAME, "idnotexists");
        }

        if (assessmentResultDTO.getRecommendationStatus().getId() == null) {
            throw new BadRequestAlertException("A new assessmentResult must have a recommendationStatus id", ENTITY_NAME, "idnotexists");
        }

        AssessmentResultDTO result = assessmentResultService.save(assessmentResultDTO);
        return ResponseEntity
                .created(new URI("/api/v1/assessment-results/" + result.getId()))
                .headers(
                        HeaderUtil
                                .createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString())
                ).body(result);
    }

    private void validateDependencies(AssessmentResultDTO assessmentResultDTO) {
        if(assessmentResultDTO.getAssessmentCriteria() == null){
            throw new BadRequestAlertException("Entity assessmentCriteria dependency not exists", ENTITY_NAME, "assessmentCriteria isnull");
        }

        if(assessmentResultDTO.getAssessmentCriteria().getId() == null){
            throw new BadRequestAlertException("Entity assessmentCriteria dependency id not exists", ENTITY_NAME, "assessmentCriteria id isnull");
        }

        if(!assessmentCriteriaService.existsByDescription(assessmentResultDTO.getAssessmentCriteria().getDescription())){
            throw new BadRequestAlertException("Entity assessmentCriteria dependency not exists", ENTITY_NAME, "assessmentCriteria notexists");
        }

        if(assessmentResultDTO.getAssessmentCriteria().getCategory() == null){
            throw new BadRequestAlertException("Entity category dependency not exists", ENTITY_NAME, "category isnull");
        }

        if(assessmentResultDTO.getAssessmentCriteria().getCategory().getId() == null){
            throw new BadRequestAlertException("Entity category dependency id not exists", ENTITY_NAME, "category id isnull");
        }

        if(!categoryService.existsByDescription(assessmentResultDTO.getAssessmentCriteria().getCategory().getDescription())){
            throw new BadRequestAlertException("Entity category dependency not exists", ENTITY_NAME, "category notexists");
        }

        if(assessmentResultDTO.getAssessmentCriteria().getCategory().getTechArea().getId() == null){
            throw new BadRequestAlertException("Entity techArea dependency id not exists", ENTITY_NAME, "techArea id isnull");
        }

        if(assessmentResultDTO.getAssessmentCriteria().getCategory().getTechArea() == null){
            throw new BadRequestAlertException("Entity techArea dependency not exists", ENTITY_NAME, "techArea isnull");
        }

        if(!techAreaService.existsByDescription(assessmentResultDTO.getAssessmentCriteria().getCategory().getTechArea().getDescription())){
            throw new BadRequestAlertException("Entity techArea dependency not exists", ENTITY_NAME, "techArea notexists");
        }

        if(assessmentResultDTO.getAssessmentCriteria().getCategory().getTechArea().getDomain() == null){
            throw new BadRequestAlertException("Entity techDomain dependency not exists", ENTITY_NAME, "techDomain isnull");
        }

        if(assessmentResultDTO.getAssessmentCriteria().getCategory().getTechArea().getDomain().getId() == null){
            throw new BadRequestAlertException("Entity techDomain dependency id not exists", ENTITY_NAME, "techDomain id isnull");
        }

        if(!techDomainService.existsByDescription(assessmentResultDTO.getAssessmentCriteria().getCategory().getTechArea().getDomain().getDescription())){
            throw new BadRequestAlertException("Entity techDomain dependency not exists", ENTITY_NAME, "techDomain notexists");
        }
    }

    /**
     * {@code PUT  /assessment-results/:id} : Updates an existing assessmentResult.
     *
     * @param id the id of the assessmentResultDTO to save.
     * @param assessmentResultDTO the assessmentResultDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assessmentResultDTO,
     * or with status {@code 400 (Bad Request)} if the assessmentResultDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the assessmentResultDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Write') or hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PutMapping(path="/assessment-results/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AssessmentResultDTO> updateAssessmentResult(@PathVariable(value = "id", required = false) final Long id, @RequestBody AssessmentResultDTO assessmentResultDTO)
            throws URISyntaxException {
        log.debug("REST request to update AssessmentResult : {}, {}", id, assessmentResultDTO);

        exists(id, assessmentResultDTO);

        AssessmentResultDTO result = assessmentResultService.save(assessmentResultDTO);
        return ResponseEntity
                .ok()
                .headers(
                        HeaderUtil
                                .createEntityUpdateAlert(applicationName, true, ENTITY_NAME, assessmentResultDTO.getId().toString()))
                .body(result);
    }

    /**
     * {@code PATCH  /assessment-results/:id} : Partial updates given fields of an existing assessmentResult, field will ignore if it is null
     *
     * @param id the id of the assessmentResultDTO to save.
     * @param assessmentResultDTO the assessmentResultDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assessmentResultDTO,
     * or with status {@code 400 (Bad Request)} if the assessmentResultDTO is not valid,
     * or with status {@code 404 (Not Found)} if the assessmentResultDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the assessmentResultDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Write') or hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PatchMapping(path = "/assessment-results/{id}", consumes = { "application/json", "application/merge-patch+json" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AssessmentResultDTO> partialUpdateAssessmentResult(
            @PathVariable(value = "id", required = false) final Long id,
            @RequestBody AssessmentResultDTO assessmentResultDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update AssessmentResult partially : {}, {}", id, assessmentResultDTO);

        exists(id, assessmentResultDTO);

        Optional<AssessmentResultDTO> result = assessmentResultService.partialUpdate(assessmentResultDTO);

        return ResponseUtil.wrapOrNotFound(
                result,
                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, assessmentResultDTO.getId().toString())
        );
    }

    private void exists(@PathVariable(value = "id", required = false) Long id, @RequestBody AssessmentResultDTO assessmentResultDTO) {
        if (!assessmentResultService.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "id not found");
        }

        if (assessmentResultDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "id null");
        }

        if (!Objects.equals(id, assessmentResultDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "id invalid");
        }
    }

    /**
     * {@code DELETE  /assessment-results/:id} : delete the "id" assessmentResult.
     *
     * @param id the id of the assessmentResultDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @DeleteMapping(path="/assessment-results/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteAssessmentResult(@PathVariable Long id) {
        log.debug("REST request to delete AssessmentResult : {}", id);
        assessmentResultService.delete(id);
        return ResponseEntity
                .noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .build();
    }

    /**
     * {@code GET  /assessment-results/count/assessment-criteria/:id} : get the assessmentResult count by assessment criteria "id".
     *
     * @param id the id of the assessmentCriteria to within assessment results
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the long, or with status {@code 404 (Not Found)}.
     */
    @GetMapping(path="/assessment-results/count/assessment-criteria/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> getAssessmentResultCountByByCriteria(@PathVariable Long id) {
        log.debug("REST request to get count of AssessmentResult by criteria id: {}", id);
        long count = assessmentResultService.countByAssessmentCriteriaId(id);
        return ResponseEntity.ok().body(count);
    }


    /**
     * {@code GET  /assessment-results/count/category/:id} : get the assessmentResult count by category "id".
     *
     * @param id the id of the assessmentCriteria within assessment results
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the long, or with status {@code 404 (Not Found)}.
     */
    @GetMapping(path="/assessment-results/count/category/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> getAssessmentResultCountByByCategory(@PathVariable Long id) {
        log.debug("REST request to get count of AssessmentResult by category id: {}", id);
        long count = assessmentResultService.countByCategoryId(id);
        return ResponseEntity.ok().body(count);
    }


    /**
     * {@code GET  /assessment-results/count/tech-area/:id} : get the assessmentResult count by tech_area "id".
     *
     * @param id the id of the tech-area within assessment results
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the long, or with status {@code 404 (Not Found)}.
     */
    @GetMapping(path="/assessment-results/count/tech-area/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> getAssessmentResultCountByByTechArea(@PathVariable Long id) {
        log.debug("REST request to get count of AssessmentResult by tech-area id: {}", id);
        long count = assessmentResultService.countByTechAreaId(id);
        return ResponseEntity.ok().body(count);
    }

    /**
     * {@code GET  /assessment-results/count/tech-domain/:id} : get the assessmentResult count by tech-domain "id".
     *
     * @param id the id of the tech-domain within assessment results
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the long, or with status {@code 404 (Not Found)}.
     */
    @GetMapping(path="/assessment-results/count/tech-domain/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> getAssessmentResultCountByByTechDomain(@PathVariable Long id) {
        log.debug("REST request to get count of AssessmentResult by tech-domain id: {}", id);
        long count = assessmentResultService.countByTechDomainId(id);
        return ResponseEntity.ok().body(count);
    }

}

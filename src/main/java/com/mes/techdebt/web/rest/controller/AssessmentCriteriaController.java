package com.mes.techdebt.web.rest.controller;

import com.mes.techdebt.service.AssessmentCriteriaService;
import com.mes.techdebt.service.CategoryService;
import com.mes.techdebt.service.TechAreaService;
import com.mes.techdebt.service.TechDomainService;
import com.mes.techdebt.service.dto.AssessmentCriteriaDTO;
import com.mes.techdebt.web.rest.errors.BadRequestAlertException;
import com.mes.techdebt.domain.AssessmentCriteria;
import com.mes.techdebt.web.rest.controller.utils.ControllerUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link AssessmentCriteria}.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Read') or hasAuthority('APPROLE_TechHealth_User_Write') or hasAuthority('APPROLE_TechHealth_User_Admin')")
public class AssessmentCriteriaController {
    private static final String ENTITY_NAME = "assessmentCriteria";
    @Value("${spring.application.name}")
    private String applicationName;

    private final AssessmentCriteriaService assessmentCriteriaService;
    private final CategoryService categoryService;
    private final TechAreaService techAreaService;
    private final TechDomainService techDomainService;

    /**
     * {@code GET  /assessment-criteria} : get all the assessmentCriteria.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of assessmentCriteria in body.
     */
    @GetMapping(path="/assessment-criteria", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AssessmentCriteriaDTO>> getAllAssessmentCriteria(@ParameterObject  @PageableDefault(size = 100, sort = "description", direction =
            Sort.Direction.ASC) Pageable pageable) {
        log.debug("REST request to get a page of AssessmentCriteria");
        Page<AssessmentCriteriaDTO> page = assessmentCriteriaService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
	
    /**
     * {@code GET  /assessment-criteria/:id} : get the "id" assessmentCriteria.
     *
     * @param id the id of the assessmentCriteriaDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the assessmentCriteriaDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping(path="/assessment-criteria/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AssessmentCriteriaDTO> getAssessmentCriteria(@PathVariable Long id) {
        log.debug("REST request to get AssessmentCriteria: {}", id);
        Optional<AssessmentCriteriaDTO> assessmentCriteriaDTO = assessmentCriteriaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(assessmentCriteriaDTO);
    }

    /**
     * {@code POST  /assessment-criteria} : Create a new assessmentCriteria.
     *
     * @param assessmentCriteriaDTO the assessmentCriteriaDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new assessmentCriteriaDTO, or with status {@code 400 (Bad Request)} if the assessmentCriteria has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PostMapping(path="/assessment-criteria", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AssessmentCriteriaDTO> createAssessmentCriteria(@RequestBody AssessmentCriteriaDTO assessmentCriteriaDTO) throws URISyntaxException {
        log.debug("REST request to save AssessmentCriteria: {}", assessmentCriteriaDTO);
        if (assessmentCriteriaDTO.getId() != null) {
            throw new BadRequestAlertException("A new assessmentCriteria cannot already have an existing id", ENTITY_NAME, "idexists");
        }

        if (assessmentCriteriaService.existsByDescription(assessmentCriteriaDTO.getDescription())) {
            ControllerUtil.prepareNotUniqueException(ENTITY_NAME, "description not unique");
        }

        validateDependencies(assessmentCriteriaDTO);

        AssessmentCriteriaDTO result = assessmentCriteriaService.save(assessmentCriteriaDTO);
        return ResponseEntity
                .created(new URI("/api/v1/assessmentCriteria/" + result.getId()))
                .headers(
                        HeaderUtil
                                .createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getDescription().toString())
                )
                .body(result);
    }

    private void validateDependencies(AssessmentCriteriaDTO assessmentCriteriaDTO) {
        if(assessmentCriteriaDTO.getCategory() == null){
            throw new BadRequestAlertException("Entity category dependency not exists", ENTITY_NAME, "category isnull");
        }

        if(assessmentCriteriaDTO.getCategory().getId() == null){
            throw new BadRequestAlertException("Entity category dependency id not exists", ENTITY_NAME, "category id isnull");
        }

        if(!categoryService.existsByDescription(assessmentCriteriaDTO.getCategory().getDescription())){
            throw new BadRequestAlertException("Entity category dependency not exists", ENTITY_NAME, "category notexists");
        }

        if(assessmentCriteriaDTO.getCategory().getTechArea().getId() == null){
            throw new BadRequestAlertException("Entity techArea dependency id not exists", ENTITY_NAME, "techArea id isnull");
        }

        if(assessmentCriteriaDTO.getCategory().getTechArea() == null){
            throw new BadRequestAlertException("Entity techArea dependency not exists", ENTITY_NAME, "techArea isnull");
        }

        if(!techAreaService.existsByDescription(assessmentCriteriaDTO.getCategory().getTechArea().getDescription())){
            throw new BadRequestAlertException("Entity techArea dependency not exists", ENTITY_NAME, "techArea notexists");
        }

        if(assessmentCriteriaDTO.getCategory().getTechArea().getDomain() == null){
            throw new BadRequestAlertException("Entity techDomain dependency not exists", ENTITY_NAME, "techDomain isnull");
        }

        if(assessmentCriteriaDTO.getCategory().getTechArea().getDomain().getId() == null){
            throw new BadRequestAlertException("Entity techDomain dependency id not exists", ENTITY_NAME, "techDomain id isnull");
        }

        if(!techDomainService.existsByDescription(assessmentCriteriaDTO.getCategory().getTechArea().getDomain().getDescription())){
            throw new BadRequestAlertException("Entity techDomain dependency not exists", ENTITY_NAME, "techDomain notexists");
        }
    }

    /**
     * {@code PUT  /assessment-criteria/:id} : Updates an existing assessmentCriteria.
     *
     * @param id the id of the assessmentCriteriaDTO to save.
     * @param assessmentCriteriaDTO the assessmentCriteriaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assessmentCriteriaDTO,
     * or with status {@code 400 (Bad Request)} if the assessmentCriteriaDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the assessmentCriteriaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PutMapping(path="/assessment-criteria/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AssessmentCriteriaDTO> updateAssessmentCriteria(@PathVariable(value = "id", required = false) final Long id, @RequestBody AssessmentCriteriaDTO assessmentCriteriaDTO)
            throws URISyntaxException {
        log.debug("REST request to update AssessmentCriteria : {}, {}", id, assessmentCriteriaDTO);

        if (!assessmentCriteriaService.existsById(id)) {
            ControllerUtil.prepareNotFoundEntity(ENTITY_NAME, "idnotfound");
        }

        if (assessmentCriteriaDTO.getId() == null) {
            ControllerUtil.prepareInvalidId(ENTITY_NAME, "idnull");
        }

        if (!Objects.equals(id, assessmentCriteriaDTO.getId())) {
            ControllerUtil.prepareInvalidId(ENTITY_NAME,  "idinvalid");
        }else{
            if (assessmentCriteriaService.existsByDescription(assessmentCriteriaDTO.getDescription()) &&
                    !Objects.equals(id,assessmentCriteriaService
                            .getIdByDescription(assessmentCriteriaDTO.getDescription()))) {
                ControllerUtil.prepareNotUniqueException(ENTITY_NAME, "description not unique");
            }
        }

        AssessmentCriteriaDTO result = assessmentCriteriaService.update(assessmentCriteriaDTO);
        return ResponseEntity
                .ok()
                .headers(
                        HeaderUtil
                                .createEntityUpdateAlert(applicationName, true, ENTITY_NAME, assessmentCriteriaDTO.getId().toString()))
                .body(result);
    }

    /**
     * {@code PATCH  /assessment-criteria/:id} : Partial updates given fields of an existing assessmentCriteria, field will ignore if it is null
     *
     * @param id the id of the assessmentCriteriaDTO to save.
     * @param assessmentCriteriaDTO the assessmentCriteriaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assessmentCriteriaDTO,
     * or with status {@code 400 (Bad Request)} if the assessmentCriteriaDTO is not valid,
     * or with status {@code 404 (Not Found)} if the assessmentCriteriaDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the assessmentCriteriaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PatchMapping(path = "/assessment-criteria/{id}", consumes = { "application/json", "application/merge-patch+json" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AssessmentCriteriaDTO> partialUpdateAssessmentCriteria(
            @PathVariable(value = "id", required = false) final Long id,
            @RequestBody AssessmentCriteriaDTO assessmentCriteriaDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update AssessmentCriteria partially : {}, {}", id, assessmentCriteriaDTO);
        if (!assessmentCriteriaService.existsById(id)) {
            ControllerUtil.prepareNotFoundEntity(ENTITY_NAME, "idnotfound");
        }

        if (assessmentCriteriaDTO.getId() == null) {
            ControllerUtil.prepareInvalidId(ENTITY_NAME, "idnull");
        }

        if (!Objects.equals(id, assessmentCriteriaDTO.getId())) {
            ControllerUtil.prepareInvalidId(ENTITY_NAME,  "idinvalid");
        }else{
            if (assessmentCriteriaService.existsByDescription(assessmentCriteriaDTO.getDescription()) &&
                    !Objects.equals(id,assessmentCriteriaService
                            .getIdByDescription(assessmentCriteriaDTO.getDescription()))) {
                ControllerUtil.prepareNotUniqueException(ENTITY_NAME, "description not unique");
            }
        }

        Optional<AssessmentCriteriaDTO> result = assessmentCriteriaService.partialUpdate(assessmentCriteriaDTO);

        return ResponseUtil.wrapOrNotFound(
                result,
                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
                        assessmentCriteriaDTO.getId().toString())
        );
    }

    /**
     * {@code DELETE  /assessmentCriteria/:id} : delete the "id" assessmentCriteria.
     *
     * @param id the id of the assessmentCriteriaDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @DeleteMapping(path="/assessment-criteria/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteAssessmentCriteria(@PathVariable Long id) {
        log.debug("REST request to delete AssessmentCriteria : {}", id);
        assessmentCriteriaService.delete(id);
        return ResponseEntity
                .noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .build();
    }
}

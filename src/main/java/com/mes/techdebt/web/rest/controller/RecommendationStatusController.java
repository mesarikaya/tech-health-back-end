package com.mes.techdebt.web.rest.controller;

import com.mes.techdebt.service.RecommendationStatusService;
import com.mes.techdebt.service.dto.RecommendationStatusDTO;
import com.mes.techdebt.web.rest.errors.BadRequestAlertException;
import com.mes.techdebt.domain.RecommendationStatus;
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link RecommendationStatus}.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Read') or hasAuthority('APPROLE_TechHealth_User_Write') or hasAuthority('APPROLE_TechHealth_User_Admin')")
public class RecommendationStatusController {
    private static final String ENTITY_NAME = "recommendationStatus";
    @Value("${spring.application.name}")
    private String applicationName;
    private final RecommendationStatusService recommendationStatusService;

    /**
     * {@code GET  /recommendation-statuses} : get all the recommendationStatuses.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of recommendationStatuses in body.
     */
    @GetMapping(path="/recommendation-statuses",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RecommendationStatusDTO>> getAllRecommendationStatuss(@ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of RecommendationStatus");
        Page<RecommendationStatusDTO> page = recommendationStatusService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
	
	/**
     * {@code GET  /recommendation-statuses/:id} : get the "id" recommendationStatus.
     *
     * @param id the id of the recommendationStatusDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the recommendationStatusDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping(path="/recommendation-statuses/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RecommendationStatusDTO> getRecommendationStatus(@PathVariable Long id) {
        log.debug("REST request to get RecommendationStatus : {}", id);
        Optional<RecommendationStatusDTO> recommendationStatusDTO = recommendationStatusService.findOne(id);
        return ResponseUtil.wrapOrNotFound(recommendationStatusDTO);
    }

    /**
     * {@code POST  /recommendation-statuses} : Create a new recommendationStatus.
     *
     * @param recommendationStatusDTO the recommendationStatusDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new recommendationStatusDTO, or with status {@code 400 (Bad Request)} if the recommendationStatus has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PostMapping(path="/recommendation-statuses",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RecommendationStatusDTO> createRecommendationStatus(@RequestBody RecommendationStatusDTO recommendationStatusDTO) throws URISyntaxException {
        log.debug("REST request to save RecommendationStatus: {}", recommendationStatusDTO);
        if (recommendationStatusDTO.getId() != null) {
            throw new BadRequestAlertException("A new recommendationStatus cannot already have an existing id", ENTITY_NAME, "id exists");
        }

        if (recommendationStatusService.existsByDescription(recommendationStatusDTO.getDescription())) {
            throw new BadRequestAlertException("Entity not unique", ENTITY_NAME, "description not unique");
        }

        RecommendationStatusDTO result = recommendationStatusService.save(recommendationStatusDTO);
        return ResponseEntity
                .created(new URI("/api/v1/recommendationStatuses/" + result.getId()))
                .headers(
                        HeaderUtil
                                .createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getDescription().toString())
                )
                .body(result);
    }

    /**
     * {@code PUT  /recommendation-statuses/:id} : Updates an existing recommendationStatus.
     *
     * @param id the id of the recommendationStatusDTO to save.
     * @param recommendationStatusDTO the recommendationStatusDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated recommendationStatusDTO,
     * or with status {@code 400 (Bad Request)} if the recommendationStatusDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the recommendationStatusDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Write') or hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PutMapping(path="/recommendation-statuses/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RecommendationStatusDTO> updateRecommendationStatus(@PathVariable(value = "id", required = false) final Long id, @RequestBody RecommendationStatusDTO recommendationStatusDTO)
            throws URISyntaxException {
        log.debug("REST request to update RecommendationStatus : {}, {}", id, recommendationStatusDTO);

        if (!recommendationStatusService.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "id not found");
        }

        if (recommendationStatusDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "id null");
        }

        if (!Objects.equals(id, recommendationStatusDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "id invalid");
        }else{
            if (recommendationStatusService.existsByDescription(recommendationStatusDTO.getDescription()) &&
                    !Objects.equals(id,recommendationStatusService
                            .getIdByDescription(recommendationStatusDTO.getDescription()))) {
                throw new BadRequestAlertException("Entity not unique", ENTITY_NAME, "description not unique");
            }
        }

        RecommendationStatusDTO result = recommendationStatusService.update(recommendationStatusDTO);
        return ResponseEntity
                .ok()
                .headers(
                        HeaderUtil
                                .createEntityUpdateAlert(applicationName, true, ENTITY_NAME, recommendationStatusDTO.getId().toString()))
                .body(result);
    }

    /**
     * {@code PATCH  /recommendation-statuses/:id} : Partial updates given fields of an existing recommendationStatus, field will ignore if it is null
     *
     * @param id the id of the recommendationStatusDTO to save.
     * @param recommendationStatusDTO the recommendationStatusDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated recommendationStatusDTO,
     * or with status {@code 400 (Bad Request)} if the recommendationStatusDTO is not valid,
     * or with status {@code 404 (Not Found)} if the recommendationStatusDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the recommendationStatusDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Write') or hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PatchMapping(path = "/recommendation-statuses/{id}", consumes = { "application/json", "application/merge-patch+json" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RecommendationStatusDTO> partialUpdateRecommendationStatus(
            @PathVariable(value = "id", required = false) final Long id,
            @RequestBody RecommendationStatusDTO recommendationStatusDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update RecommendationStatus partially : {}, {}", id, recommendationStatusDTO);
        if (!recommendationStatusService.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "id not found");
        }

        if (recommendationStatusDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "id null");
        }

        if (!Objects.equals(id, recommendationStatusDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "id invalid");
        }else{
            if (recommendationStatusService.existsByDescription(recommendationStatusDTO.getDescription()) &&
                    !Objects.equals(id,recommendationStatusService
                            .getIdByDescription(recommendationStatusDTO.getDescription()))) {
                throw new BadRequestAlertException("Entity not unique", ENTITY_NAME, "description not unique");
            }
        }

        Optional<RecommendationStatusDTO> result = recommendationStatusService.partialUpdate(recommendationStatusDTO);

        return ResponseUtil.wrapOrNotFound(
                result,
                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, recommendationStatusDTO.getId().toString())
        );
    }

    /**
     * {@code DELETE  /recommendation-statuses/:id} : delete the "id" recommendationStatus.
     *
     * @param id the id of the recommendationStatusDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @DeleteMapping(path = "/recommendation-statuses/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteRecommendationStatus(@PathVariable Long id) {
        log.debug("REST request to delete RecommendationStatus : {}", id);
        recommendationStatusService.delete(id);
        return ResponseEntity
                .noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .build();
    }

}

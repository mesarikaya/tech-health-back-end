package com.mes.techdebt.web.rest.controller;

import com.mes.techdebt.service.CostRangeService;
import com.mes.techdebt.service.dto.CostRangeDTO;
import com.mes.techdebt.web.rest.errors.BadRequestAlertException;
import com.mes.techdebt.domain.CostRange;
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
 * REST controller for managing {@link CostRange}.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Read') or hasAuthority('APPROLE_TechHealth_User_Write') or hasAuthority('APPROLE_TechHealth_User_Admin')")
public class CostRangeController {

    private static final String ENTITY_NAME = "costRange";
    @Value("${spring.application.name}")
    private String applicationName;
    private final CostRangeService costRangeService;

    /**
     * {@code GET  /cost-ranges} : get all the costRanges.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of costRanges in body.
     */
    @GetMapping(path="/cost-ranges", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CostRangeDTO>> getAllCostRanges(@ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of CostRanges");
        Page<CostRangeDTO> page = costRangeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
	
    /**
     * {@code GET  /costRanges/:id} : get the "id" costRange.
     *
     * @param id the id of the costRangeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the costRangeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping(path="/cost-ranges/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CostRangeDTO> getCostRange(@PathVariable Long id) {
        log.debug("REST request to get CostRange: {}", id);
        Optional<CostRangeDTO> costRangeDTO = costRangeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(costRangeDTO);
    }

    /**
     * {@code POST  /cost-ranges} : Create a new costRange.
     *
     * @param costRangeDTO the costRangeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new costRangeDTO, or with status {@code 400 (Bad Request)} if the costRange has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PostMapping(path="/cost-ranges", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CostRangeDTO> createCostRange(@RequestBody CostRangeDTO costRangeDTO) throws URISyntaxException {
        log.debug("REST request to save CostRange: {}", costRangeDTO);
        if (costRangeDTO.getId() != null) {
            throw new BadRequestAlertException("A new costRange cannot already have an existing id", ENTITY_NAME, "id exists");
        }

        if (costRangeService.existsByDescription(costRangeDTO.getDescription())) {
            throw new BadRequestAlertException("Entity not unique", ENTITY_NAME, "description not unique");
        }

        CostRangeDTO result = costRangeService.save(costRangeDTO);
        return ResponseEntity
                .created(new URI("/api/v1/cost-ranges/" + result.getId()))
                .headers(
                        HeaderUtil
                                .createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getDescription().toString())
                )
                .body(result);
    }

    /**
     * {@code PUT  /cost-ranges/:id} : Updates an existing costRange.
     *
     * @param id the id of the costRangeDTO to save.
     * @param costRangeDTO the costRangeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated costRangeDTO,
     * or with status {@code 400 (Bad Request)} if the costRangeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the costRangeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PutMapping(path="/cost-ranges/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CostRangeDTO> updateCostRange(@PathVariable(value = "id", required = false) final Long id, @RequestBody CostRangeDTO costRangeDTO)
            throws URISyntaxException {
        log.debug("REST request to update CostRange : {}, {}", id, costRangeDTO);

        if (!costRangeService.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "id not found");
        }

        if (costRangeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "id null");
        }

        if (!Objects.equals(id, costRangeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "id invalid");
        }else{
            if (costRangeService.existsByDescription(costRangeDTO.getDescription()) &&
                    !Objects.equals(id,costRangeService
                            .getIdByDescription(costRangeDTO.getDescription()))) {
                throw new BadRequestAlertException("Entity not unique", ENTITY_NAME, "description not unique");
            }
        }

        CostRangeDTO result = costRangeService.update(costRangeDTO);
        return ResponseEntity
                .ok()
                .headers(
                        HeaderUtil
                                .createEntityUpdateAlert(applicationName, true, ENTITY_NAME, costRangeDTO.getId().toString()))
                .body(result);
    }

    /**
     * {@code PATCH  /cost-ranges/:id} : Partial updates given fields of an existing costRange, field will ignore if it is null
     *
     * @param id the id of the costRangeDTO to save.
     * @param costRangeDTO the costRangeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated costRangeDTO,
     * or with status {@code 400 (Bad Request)} if the costRangeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the costRangeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the costRangeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PatchMapping(path = "/cost-ranges/{id}", consumes = { "application/json", "application/merge-patch+json" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CostRangeDTO> partialUpdateCostRange(
            @PathVariable(value = "id", required = false) final Long id,
            @RequestBody CostRangeDTO costRangeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update CostRange partially : {}, {}", id, costRangeDTO);
        if (!costRangeService.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "id not found");
        }

        if (costRangeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "id null");
        }

        if (!Objects.equals(id, costRangeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "id invalid");
        }else{
            if (costRangeService.existsByDescription(costRangeDTO.getDescription()) &&
                    !Objects.equals(id,costRangeService
                            .getIdByDescription(costRangeDTO.getDescription()))) {
                throw new BadRequestAlertException("Entity not unique", ENTITY_NAME, "description not unique");
            }
        }

        Optional<CostRangeDTO> result = costRangeService.partialUpdate(costRangeDTO);

        return ResponseUtil.wrapOrNotFound(
                result,
                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, costRangeDTO.getId().toString())
        );
    }

    /**
     * {@code DELETE  /cost-ranges/:id} : delete the "id" costRange.
     *
     * @param id the id of the costRangeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @DeleteMapping(path="/cost-ranges/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteCostRange(@PathVariable Long id) {
        log.debug("REST request to delete CostRange : {}", id);
        costRangeService.delete(id);
        return ResponseEntity
                .noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .build();
    }
}

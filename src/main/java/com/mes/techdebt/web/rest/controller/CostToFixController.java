package com.mes.techdebt.web.rest.controller;

import com.mes.techdebt.service.CostToFixService;
import com.mes.techdebt.service.dto.CostToFixDTO;
import com.mes.techdebt.web.rest.errors.BadRequestAlertException;
import com.mes.techdebt.domain.CostToFix;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link CostToFix}.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Read') or hasAuthority('APPROLE_TechHealth_User_Write') or hasAuthority('APPROLE_TechHealth_User_Admin')")
public class CostToFixController {

    private static final String ENTITY_NAME = "costToFix";
    @Value("${spring.application.name}")
    private String applicationName;
    private final CostToFixService costToFixService;

    /**
     * {@code GET  /cost-to-fixes} : get all the costToFixs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of costToFixs in body.
     */
    @GetMapping(path="/cost-to-fixes", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CostToFixDTO> getAllCostToFixes() {
        log.debug("REST request to get all CostToFixes");
        return costToFixService.findAll();
    }
	
	/**
     * {@code GET  /cost-to-fixes/:id} : get the "id" costToFix.
     *
     * @param id the id of the costToFixDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the costToFixDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping(path="/cost-to-fixes/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CostToFixDTO> getCostToFix(@PathVariable Long id) {
        log.debug("REST request to get CostToFix: {}", id);
        Optional<CostToFixDTO> costToFixDTO = costToFixService.findOne(id);
        return ResponseUtil.wrapOrNotFound(costToFixDTO);
    }

    /**
     * {@code POST  /cost-to-fixes} : Create a new costToFix.
     *
     * @param costToFixDTO the costToFixDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new costToFixDTO, or with status {@code 400 (Bad Request)} if the costToFix has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PostMapping(path="/cost-to-fixes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CostToFixDTO> createCostToFix(@RequestBody CostToFixDTO costToFixDTO) throws URISyntaxException {
        log.debug("REST request to save CostToFix: {}", costToFixDTO);
        if (costToFixDTO.getId() != null) {
            throw new BadRequestAlertException("A new costToFix cannot already have an existing id", ENTITY_NAME, "id exists");
        }

        log.debug("CostToFix: {}", costToFixDTO);
        if (costToFixDTO.getSite().getId() == null) {
            throw new BadRequestAlertException("A new costToFix must have a site id", ENTITY_NAME, "id notexists");
        }

        if (costToFixDTO.getCategory().getId() == null) {
            throw new BadRequestAlertException("A new costToFix must have a category id", ENTITY_NAME, "id notexists");
        }

        if (costToFixDTO.getCostRange().getId() == null) {
            throw new BadRequestAlertException("A new costToFix must have a costRange id", ENTITY_NAME, "id notexists");
        }

        if (costToFixService.existsBySiteIdAndCategoryId(costToFixDTO.getSite().getId(), costToFixDTO.getCategory().getId()) &&
                !Objects.nonNull(costToFixService
                        .findBySiteIdAndCategoryIdAndCostRangeId(
                                costToFixDTO.getSite().getId(),
                                costToFixDTO.getCategory().getId(),
                                costToFixDTO.getCostRange().getId())
                        .stream().findFirst().orElse(null))) {
            throw new BadRequestAlertException("Entity not unique", ENTITY_NAME, "site id, category id and costRange id combination not unique");
        }

        CostToFixDTO result = costToFixService.save(costToFixDTO);
        return ResponseEntity
                .created(new URI("/api/v1/cost-to-fixes/" + result.getId()))
                .headers(
                        HeaderUtil
                                .createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString())
                )
                .body(result);
    }

    /**
     * {@code PUT  /cost-to-fixes/:id} : Updates an existing costToFix.
     *
     * @param id the id of the costToFixDTO to save.
     * @param costToFixDTO the costToFixDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated costToFixDTO,
     * or with status {@code 400 (Bad Request)} if the costToFixDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the costToFixDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PutMapping(path="/cost-to-fixes/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CostToFixDTO> updateCostToFix(@PathVariable(value = "id", required = false) final Long id, @RequestBody CostToFixDTO costToFixDTO)
            throws URISyntaxException {
        log.debug("REST request to update CostToFix : {}, {}", id, costToFixDTO);

        if (costToFixDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "id null");
        }

        if (!costToFixService.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "id not found");
        }

        if (costToFixDTO.getSite().getId() == null) {
            throw new BadRequestAlertException("A new costToFix must have a site id", ENTITY_NAME, "id notexists");
        }

        if (costToFixDTO.getCategory().getId() == null) {
            throw new BadRequestAlertException("A new costToFix must have a category id", ENTITY_NAME, "id notexists");
        }

        if (costToFixDTO.getCostRange().getId() == null) {
            throw new BadRequestAlertException("A new costToFix must have a costRange id", ENTITY_NAME, "id notexists");
        }

        if (!Objects.equals(id, costToFixDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "id invalid");
        }else{
            if (costToFixService.existsBySiteIdAndCategoryId(costToFixDTO.getSite().getId(),
                    costToFixDTO.getCategory().getId()) &&
                    !Objects.equals(id,costToFixService
                            .findBySiteIdAndCategoryIdAndCostRangeId(costToFixDTO.getSite().getId(),
                                    costToFixDTO.getCategory().getId(),
                                    costToFixDTO.getCostRange().getId())
                            .stream().findFirst().orElse(null))) {
                throw new BadRequestAlertException("Entity not unique", ENTITY_NAME, "site id, category id and costRange id combination not unique");
            }
        }

        CostToFixDTO result = costToFixService.update(costToFixDTO);
        return ResponseEntity
                .ok()
                .headers(
                        HeaderUtil
                                .createEntityUpdateAlert(applicationName, true, ENTITY_NAME, costToFixDTO.getId().toString()))
                .body(result);
    }

    /**
     * {@code PATCH  /cost-to-fixes/:id} : Partial updates given fields of an existing costToFix, field will ignore if it is null
     *
     * @param id the id of the costToFixDTO to save.
     * @param costToFixDTO the costToFixDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated costToFixDTO,
     * or with status {@code 400 (Bad Request)} if the costToFixDTO is not valid,
     * or with status {@code 404 (Not Found)} if the costToFixDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the costToFixDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PatchMapping(path = "/cost-to-fixes/{id}", consumes = { "application/json", "application/merge-patch+json" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CostToFixDTO> partialUpdateCostToFix(
            @PathVariable(value = "id", required = false) final Long id,
            @RequestBody CostToFixDTO costToFixDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update CostToFix partially : {}, {}", id, costToFixDTO);
        if (costToFixDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "id null");
        }

        if (!costToFixService.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        if (costToFixDTO.getSite().getId() == null) {
            throw new BadRequestAlertException("A new costToFix must have a site id", ENTITY_NAME, "idnotexists");
        }

        if (costToFixDTO.getCategory().getId() == null) {
            throw new BadRequestAlertException("A new costToFix must have a category id", ENTITY_NAME, "idnotexists");
        }

        if (costToFixDTO.getCostRange().getId() == null) {
            throw new BadRequestAlertException("A new costToFix must have a costRange id", ENTITY_NAME, "idnotexists");
        }

        if (!Objects.equals(id, costToFixDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "id invalid");
        }else{
            if (costToFixService.existsBySiteIdAndCategoryId(costToFixDTO.getSite().getId(),
                    costToFixDTO.getCategory().getId()) &&
                    !Objects.equals(id,costToFixService
                            .findBySiteIdAndCategoryIdAndCostRangeId(costToFixDTO.getSite().getId(),
                                    costToFixDTO.getCategory().getId(),
                                    costToFixDTO.getCostRange().getId())
                            .stream().findFirst().orElse(null))) {
                throw new BadRequestAlertException("Entity not unique", ENTITY_NAME, "site id, category id and costRange id combination not unique");
            }
        }

        Optional<CostToFixDTO> result = costToFixService.partialUpdate(costToFixDTO);
        log.debug("Saved result: {}", result.isPresent() ? result.get() : "No result");

        return ResponseUtil.wrapOrNotFound(
                result,
                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, costToFixDTO.getId().toString())
        );
    }

    /**
     * {@code DELETE  /cost-to-fixes/:id} : delete the "id" costToFix.
     *
     * @param id the id of the costToFixDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @DeleteMapping(path="/cost-to-fixes/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteCostToFix(@PathVariable Long id) {
        log.debug("REST request to delete CostToFix : {}", id);
        costToFixService.delete(id);
        return ResponseEntity
                .noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .build();
    }

}

package com.mes.techdebt.web.rest.controller;

import com.mes.techdebt.service.TechAreaService;
import com.mes.techdebt.service.TechDomainService;
import com.mes.techdebt.service.dto.TechAreaDTO;
import com.mes.techdebt.web.rest.errors.BadRequestAlertException;
import com.mes.techdebt.domain.TechArea;
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
 * REST controller for managing {@link TechArea}.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Read') or hasAuthority('APPROLE_TechHealth_User_Write') or hasAuthority('APPROLE_TechHealth_User_Admin')")
public class TechAreaController {
    private static final String ENTITY_NAME = "techArea";
    @Value("${spring.application.name}")
    private String applicationName;
    private final TechAreaService techAreaService;
    private final TechDomainService techDomainService;

    /**
     * {@code GET  /tech-areas} : get all the techAreas.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of techAreas in body.
     */
    @GetMapping(path="/tech-areas",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TechAreaDTO>> getAllTechAreas(@ParameterObject  @PageableDefault(size = 100, sort = "description", direction = Sort.Direction.ASC) Pageable pageable) {
        log.debug("REST request to get a page of TechAreas");
        Page<TechAreaDTO> page = techAreaService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
	
	/**
     * {@code GET  /tech-areas/:id} : get the "id" techArea.
     *
     * @param id the id of the techAreaDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the techAreaDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping(path="/tech-areas/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TechAreaDTO> getTechArea(@PathVariable Long id) {
        log.debug("REST request to get TechArea : {}", id);
        Optional<TechAreaDTO> techAreaDTO = techAreaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(techAreaDTO);
    }

    /**
     * {@code POST  /tech-areas} : Create a new techArea.
     *
     * @param techAreaDTO the techAreaDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new techAreaDTO, or with status {@code 400 (Bad Request)} if the techArea has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PostMapping(path="/tech-areas",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TechAreaDTO> createTechArea(@RequestBody TechAreaDTO techAreaDTO) throws URISyntaxException {
        log.debug("REST request to save TechArea: {}", techAreaDTO);
        if (techAreaDTO.getId() != null) {
            throw new BadRequestAlertException("A new techArea cannot already have an existing id", ENTITY_NAME, "id exists");
        }

        if (techAreaService.existsByDescription(techAreaDTO.getDescription())) {
            throw new BadRequestAlertException("Entity not unique", ENTITY_NAME, "description notunique");
        }

        TechAreaDTO result = techAreaService.save(techAreaDTO);
        return ResponseEntity
                .created(new URI("/api/v1/tech-areas/" + result.getId()))
                .headers(
                        HeaderUtil
                                .createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getDescription().toString())
                )
                .body(result);
    }

    private void validateDependencies(TechAreaDTO techAreaDTO) {
        if(techAreaDTO.getDomain() == null){
            throw new BadRequestAlertException("Entity techDomain dependency not exists", ENTITY_NAME, "techDomain isnull");
        }

        if(techAreaDTO.getDomain().getId() == null){
            throw new BadRequestAlertException("Entity techDomain dependency id not exists", ENTITY_NAME, "techDomain id isnull");
        }

        if(!techDomainService.existsByDescription(techAreaDTO.getDomain().getDescription())){
            throw new BadRequestAlertException("Entity techDomain dependency not exists", ENTITY_NAME, "techDomain notexists");
        }
    }

    /**
     * {@code PUT  /tech-areas/:id} : Updates an existing techArea.
     *
     * @param id the id of the techAreaDTO to save.
     * @param techAreaDTO the techAreaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated techAreaDTO,
     * or with status {@code 400 (Bad Request)} if the techAreaDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the techAreaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PutMapping(path="/tech-areas/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TechAreaDTO> updateTechArea(@PathVariable(value = "id", required = false) final Long id, @RequestBody TechAreaDTO techAreaDTO)
            throws URISyntaxException {
        log.debug("REST request to update TechArea : {}, {}", id, techAreaDTO);

        if (!techAreaService.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "id not found");
        }

        if (techAreaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "id null");
        }

        if (!Objects.equals(id, techAreaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "id invalid");
        }else{
            if (techAreaService.existsByDescription(techAreaDTO.getDescription()) &&
                    !Objects.equals(id,techAreaService
                            .getIdByDescription(techAreaDTO.getDescription()))) {
                throw new BadRequestAlertException("Entity not unique", ENTITY_NAME, "description not unique");
            }
        }

        TechAreaDTO result = techAreaService.save(techAreaDTO);
        return ResponseEntity
                .ok()
                .headers(
                        HeaderUtil
                                .createEntityUpdateAlert(applicationName, true, ENTITY_NAME, techAreaDTO.getId().toString()))
                .body(result);
    }

    /**
     * {@code PATCH  /tech-areas/:id} : Partial updates given fields of an existing techArea, field will ignore if it is null
     *
     * @param id the id of the techAreaDTO to save.
     * @param techAreaDTO the techAreaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated techAreaDTO,
     * or with status {@code 400 (Bad Request)} if the techAreaDTO is not valid,
     * or with status {@code 404 (Not Found)} if the techAreaDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the techAreaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PatchMapping(path = "/tech-areas/{id}", consumes = { "application/json", "application/merge-patch+json" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TechAreaDTO> partialUpdateTechArea(
            @PathVariable(value = "id", required = false) final Long id,
            @RequestBody TechAreaDTO techAreaDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TechArea partially : {}, {}", id, techAreaDTO);
        if (!techAreaService.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "id not found");
        }

        if (techAreaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "id null");
        }

        if (!Objects.equals(id, techAreaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "id invalid");
        }else{
            if (techAreaService.existsByDescription(techAreaDTO.getDescription()) &&
                    !Objects.equals(id,techAreaService
                            .getIdByDescription(techAreaDTO.getDescription()))) {
                throw new BadRequestAlertException("Entity not unique", ENTITY_NAME, "description not unique");
            }
        }

        Optional<TechAreaDTO> result = techAreaService.partialUpdate(techAreaDTO);

        return ResponseUtil.wrapOrNotFound(
                result,
                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, techAreaDTO.getId().toString())
        );
    }

    /**
     * {@code DELETE  /tech-areas/:id} : delete the "id" techArea.
     *
     * @param id the id of the techAreaDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @DeleteMapping(path="/tech-areas/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteTechArea(@PathVariable Long id) {
        log.debug("REST request to delete TechArea : {}", id);
        techAreaService.delete(id);
        return ResponseEntity
                .noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .build();
    }
}

package com.mes.techdebt.web.rest.controller;

import com.mes.techdebt.service.TechDomainService;
import com.mes.techdebt.service.dto.TechDomainDTO;
import com.mes.techdebt.web.rest.errors.BadRequestAlertException;
import com.mes.techdebt.domain.TechDomain;
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
 * REST controller for managing {@link TechDomain}.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Read') or hasAuthority('APPROLE_TechHealth_User_Write') or hasAuthority('APPROLE_TechHealth_User_Admin')")
public class TechDomainController {
    private static final String ENTITY_NAME = "techDomain";
    @Value("${spring.application.name}")
    private String applicationName;
    private final TechDomainService techDomainService;

    /**
     * {@code GET  /tech-domains} : get all the techDomains.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of techDomains in body.
     */
    @GetMapping(path="/tech-domains",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TechDomainDTO>> getAllTechDomains(@ParameterObject  @PageableDefault(size = 100, sort = "description", direction = Sort.Direction.ASC) Pageable pageable) {
        log.debug("REST request to get a page of TechDomains");
        Page<TechDomainDTO> page = techDomainService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
	
	/**
     * {@code GET  /tech-domains/:id} : get the "id" techDomain.
     *
     * @param id the id of the techDomainDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the techDomainDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping(path="/tech-domains/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TechDomainDTO> getTechDomain(@PathVariable Long id) {
        log.debug("REST request to get TechDomain : {}", id);
        Optional<TechDomainDTO> techDomainDTO = techDomainService.findOne(id);
        return ResponseUtil.wrapOrNotFound(techDomainDTO);
    }

    /**
     * {@code POST  /tech-domains} : Create a new techDomain.
     *
     * @param techDomainDTO the techDomainDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new techDomainDTO, or with status {@code 400 (Bad Request)} if the techDomain has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PostMapping(path="/tech-domains", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TechDomainDTO> createTechDomain(@RequestBody TechDomainDTO techDomainDTO) throws URISyntaxException {
        log.debug("REST request to save TechDomain: {}", techDomainDTO);
        if (techDomainDTO.getId() != null) {
            throw new BadRequestAlertException("A new techDomain cannot already have an existing id", ENTITY_NAME, "id exists");
        }

        if (techDomainService.existsByDescription(techDomainDTO.getDescription())) {
            throw new BadRequestAlertException("Entity not unique", ENTITY_NAME, "description not unique");
        }

        TechDomainDTO result = techDomainService.save(techDomainDTO);
        return ResponseEntity
                .created(new URI("/api/v1/tech-domains/" + result.getId()))
                .headers(
                        HeaderUtil
                                .createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getDescription().toString())
                )
                .body(result);
    }

    /**
     * {@code PUT  /tech-domains/:id} : Updates an existing techDomain.
     *
     * @param id the id of the techDomainDTO to save.
     * @param techDomainDTO the techDomainDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated techDomainDTO,
     * or with status {@code 400 (Bad Request)} if the techDomainDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the techDomainDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PutMapping(path="/tech-domains/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TechDomainDTO> updateTechDomain(@PathVariable(value = "id", required = false) final Long id, @RequestBody TechDomainDTO techDomainDTO)
            throws URISyntaxException {
        log.debug("REST request to update TechDomain : {}, {}", id, techDomainDTO);

        if (!techDomainService.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "id not found");
        }

        if (techDomainDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "id null");
        }

        if (!Objects.equals(id, techDomainDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "id invalid");
        }else{
            if (techDomainService.existsByDescription(techDomainDTO.getDescription()) &&
                    !Objects.equals(id,techDomainService
                            .getIdByDescription(techDomainDTO.getDescription()))) {
                throw new BadRequestAlertException("Entity not unique", ENTITY_NAME, "description not unique");
            }
        }

        TechDomainDTO result = techDomainService.save(techDomainDTO);
        return ResponseEntity
                .ok()
                .headers(
                        HeaderUtil
                                .createEntityUpdateAlert(applicationName, true, ENTITY_NAME, techDomainDTO.getId().toString()))
                .body(result);
    }

    /**
     * {@code PATCH  /tech-domains/:id} : Partial updates given fields of an existing techDomain, field will ignore if it is null
     *
     * @param id the id of the techDomainDTO to save.
     * @param techDomainDTO the techDomainDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated techDomainDTO,
     * or with status {@code 400 (Bad Request)} if the techDomainDTO is not valid,
     * or with status {@code 404 (Not Found)} if the techDomainDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the techDomainDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PatchMapping(path = "/tech-domains/{id}", consumes = { "application/json", "application/merge-patch+json" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TechDomainDTO> partialUpdateTechDomain(
            @PathVariable(value = "id", required = false) final Long id,
            @RequestBody TechDomainDTO techDomainDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TechDomain partially : {}, {}", id, techDomainDTO);
        if (!techDomainService.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "id not found");
        }

        if (techDomainDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "id null");
        }

        if (!Objects.equals(id, techDomainDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "id invalid");
        }else{
            if (techDomainService.existsByDescription(techDomainDTO.getDescription()) &&
                    !Objects.equals(id,techDomainService
                            .getIdByDescription(techDomainDTO.getDescription()))) {
                throw new BadRequestAlertException("Entity not unique", ENTITY_NAME, "description not unique");
            }
        }

        Optional<TechDomainDTO> result = techDomainService.partialUpdate(techDomainDTO);

        return ResponseUtil.wrapOrNotFound(
                result,
                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, techDomainDTO.getId().toString())
        );
    }

    /**
     * {@code DELETE  /tech-domains/:id} : delete the "id" techDomain.
     *
     * @param id the id of the techDomainDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @DeleteMapping(path="/tech-domains/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteTechDomain(@PathVariable Long id) {
        log.debug("REST request to delete TechDomain : {}", id);
        techDomainService.delete(id);
        return ResponseEntity
                .noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .build();
    }

}

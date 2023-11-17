package com.mes.techdebt.web.rest.controller;

import com.mes.techdebt.service.InvestmentCriticalityService;
import com.mes.techdebt.service.dto.InvestmentCriticalityDTO;
import com.mes.techdebt.web.rest.errors.BadRequestAlertException;
import com.mes.techdebt.domain.InvestmentCriticality;
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
 * REST controller for managing {@link InvestmentCriticality}.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Read') or hasAuthority('APPROLE_TechHealth_User_Write') or hasAuthority('APPROLE_TechHealth_User_Admin')")
public class InvestmentCriticalityController {

    private static final String ENTITY_NAME = "investmentCriticality";

    @Value("${spring.application.name}")
    private String applicationName;

    private final InvestmentCriticalityService investmentCriticalityService;

    /**
     * {@code GET  /investment-criticalities} : get all the investmentCriticality.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of investmentCriticality in body.
     */
    @GetMapping(path="/investment-criticalities" ,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<InvestmentCriticalityDTO>> getAllInvestmentCriticality(@ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of InvestmentCriticality");
        Page<InvestmentCriticalityDTO> page = investmentCriticalityService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /investment-criticalities/:id} : get the "id" investmentCriticality.
     *
     * @param id the id of the investmentCriticalityDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the investmentCriticalityDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping(path="/investment-criticalities/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InvestmentCriticalityDTO> getInvestmentCriticality(@PathVariable Long id) {
        log.debug("REST request to get InvestmentCriticality : {}", id);
        Optional<InvestmentCriticalityDTO> investmentCriticalityDTO = investmentCriticalityService.findOne(id);
        return ResponseUtil.wrapOrNotFound(investmentCriticalityDTO);
    }
	
    /**
     * {@code POST  /investment-criticalities} : Create a new investmentCriticality.
     *
     * @param investmentCriticalityDTO the investmentCriticalityTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new investmentCriticalityTO, or with status {@code 400 (Bad Request)} if the
     * investmentCriticality has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PostMapping(path="/investment-criticalities", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InvestmentCriticalityDTO> createInvestmentCriticality(@RequestBody InvestmentCriticalityDTO investmentCriticalityDTO) throws URISyntaxException {
        log.debug("REST request to save InvestmentCriticality: {}", investmentCriticalityDTO);
        if (investmentCriticalityDTO.getId() != null) {
            throw new BadRequestAlertException("A new investmentCriticality cannot already have an existing id", ENTITY_NAME, "idexists");
        }

        if (investmentCriticalityService.existsByDescription(investmentCriticalityDTO.getDescription())) {
            throw new BadRequestAlertException("Entity not unique", ENTITY_NAME, "description not unique");
        }

        InvestmentCriticalityDTO result = investmentCriticalityService.save(investmentCriticalityDTO);
        return ResponseEntity
                .created(new URI("/api/v1/investmentCriticality/" + result.getId()))
                .headers(
                        HeaderUtil
                                .createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getDescription().toString())
                )
                .body(result);
    }

    /**
     * {@code PUT  /investment-criticalities/:id} : Updates an existing investmentCriticality.
     *
     * @param id the id of the investmentCriticalityDTO to save.
     * @param investmentCriticalityDTO the investmentCriticalityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated investmentCriticalityDTO,
     * or with status {@code 400 (Bad Request)} if the investmentCriticalityDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the investmentCriticalityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PutMapping(path="/investment-criticalities/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InvestmentCriticalityDTO> updateInvestmentCriticality(@PathVariable(value = "id", required = false) final Long id, @RequestBody InvestmentCriticalityDTO investmentCriticalityDTO)
            throws URISyntaxException {
        log.debug("REST request to update InvestmentCriticality : {}, {}", id, investmentCriticalityDTO);

        if (!investmentCriticalityService.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "id not found");
        }

        if (investmentCriticalityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "id null");
        }

        if (!Objects.equals(id, investmentCriticalityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "id invalid");
        }else{
            if (investmentCriticalityService.existsByDescription(investmentCriticalityDTO.getDescription()) &&
                    !Objects.equals(id,investmentCriticalityService
                            .getIdByDescription(investmentCriticalityDTO.getDescription()))) {
                throw new BadRequestAlertException("Entity not unique", ENTITY_NAME, "description not unique");
            }
        }

        InvestmentCriticalityDTO result = investmentCriticalityService.save(investmentCriticalityDTO);
        return ResponseEntity
                .ok()
                .headers(
                        HeaderUtil
                                .createEntityUpdateAlert(applicationName, true, ENTITY_NAME, investmentCriticalityDTO.getId().toString()))
                .body(result);
    }

    /**
     * {@code PATCH  /investment-criticalities/:id} : Partial updates given fields of an existing investmentCriticality, field will ignore if it is null
     *
     * @param id the id of the investmentCriticalityDTO to save.
     * @param investmentCriticalityDTO the investmentCriticalityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated investmentCriticalityDTO,
     * or with status {@code 400 (Bad Request)} if the investmentCriticalityDTO is not valid,
     * or with status {@code 404 (Not Found)} if the investmentCriticalityDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the investmentCriticalityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PatchMapping(path = "/investment-criticalities/{id}", consumes = { "application/json", "application/merge-patch+json" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InvestmentCriticalityDTO> partialUpdateInvestmentCriticality(
            @PathVariable(value = "id", required = false) final Long id,
            @RequestBody InvestmentCriticalityDTO investmentCriticalityDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update InvestmentCriticality partially : {}, {}", id, investmentCriticalityDTO);
        if (!investmentCriticalityService.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "id not found");
        }

        if (investmentCriticalityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "id null");
        }

        if (!Objects.equals(id, investmentCriticalityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "id invalid");
        }else{
            if (investmentCriticalityService.existsByDescription(investmentCriticalityDTO.getDescription()) &&
                    !Objects.equals(id,investmentCriticalityService
                            .getIdByDescription(investmentCriticalityDTO.getDescription()))) {
                throw new BadRequestAlertException("Entity not unique", ENTITY_NAME, "description not unique");
            }
        }

        Optional<InvestmentCriticalityDTO> result = investmentCriticalityService.partialUpdate(investmentCriticalityDTO);

        return ResponseUtil.wrapOrNotFound(
                result,
                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, investmentCriticalityDTO.getId().toString())
        );
    }

    /**
     * {@code DELETE  /investment-criticalities/:id} : delete the "id" investmentCriticality.
     *
     * @param id the id of the investmentCriticalityDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @DeleteMapping(path="/investment-criticalities/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteInvestmentCriticality(@PathVariable Long id) {
        log.debug("REST request to delete InvestmentCriticality : {}", id);
        investmentCriticalityService.delete(id);
        return ResponseEntity
                .noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .build();
    }

}

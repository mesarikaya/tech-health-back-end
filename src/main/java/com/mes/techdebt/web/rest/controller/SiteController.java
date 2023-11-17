package com.mes.techdebt.web.rest.controller;

import com.mes.techdebt.service.InvestmentCriticalityService;
import com.mes.techdebt.service.SiteService;
import com.mes.techdebt.service.dto.SiteDTO;
import com.mes.techdebt.web.rest.errors.BadRequestAlertException;
import com.mes.techdebt.web.rest.request.SiteRequestDTO;
import com.mes.techdebt.web.rest.response.DashboardSiteAndCountryFilterDTO;
import com.mes.techdebt.domain.Site;
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

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link Site}.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Read') or hasAuthority('APPROLE_TechHealth_User_Write') or hasAuthority('APPROLE_TechHealth_User_Admin')")
public class SiteController {
    private static final String ENTITY_NAME = "site";
    @Value("${spring.application.name}")
    private String applicationName;
    private final SiteService siteService;
    private final InvestmentCriticalityService investmentCriticalityService;

    /**
     * {@code GET  /sites} : get all the sites.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sites in body.
     */
    @GetMapping(path="/sites",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SiteDTO>> getAllSites(@ParameterObject  @PageableDefault(size = 100, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        log.debug("REST request to get a page of Sites");
        Page<SiteDTO> page = siteService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /sites/:id} : get the "id" site.
     *
     * @param id the id of the siteDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the siteDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping(path="/sites/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SiteDTO> getSite(@PathVariable Long id) {
        log.debug("REST request to get Site : {}", id);
        Optional<SiteDTO> siteDTO = siteService.findOne(id);
        return ResponseUtil.wrapOrNotFound(siteDTO);
    }

    @PostMapping(path="/region/sites",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DashboardSiteAndCountryFilterDTO>> getSitesByRegion(@Valid @RequestBody SiteRequestDTO request) {
        log.debug("REST request to get Site by region: {}", request.getRegion());
        Optional<List<DashboardSiteAndCountryFilterDTO>> siteDTO = siteService.findSitesByRegions(request.getRegion());
        return ResponseUtil.wrapOrNotFound(siteDTO);
    }

    /**
     * {@code POST  /sites} : Create a new site.
     *
     * @param siteDTO the siteDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new siteDTO, or with status {@code 400 (Bad Request)} if the site has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PostMapping(path="/sites",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SiteDTO> createSite(@RequestBody SiteDTO siteDTO) throws URISyntaxException {
        log.debug("REST request to save Site: {}", siteDTO);
        if (siteDTO.getId() != null) {
            throw new BadRequestAlertException("A new site cannot already have an existing id", ENTITY_NAME, "idexists");
        }

        if (siteService.existsByName(siteDTO.getName())) {
            throw new BadRequestAlertException("Entity not unique", ENTITY_NAME, "name not unique");
        }

        validateDependencies(siteDTO);

        SiteDTO result = siteService.save(siteDTO);
        return ResponseEntity
                .created(new URI("/api/v1/sites/" + result.getId()))
                .headers(
                        HeaderUtil
                                .createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getName().toString())
                )
                .body(result);
    }

    private void validateDependencies(SiteDTO siteDTO) {

        if(siteDTO.getInvestmentCriticality() == null){
            throw new BadRequestAlertException("Entity investmentCriticality dependency not exists", ENTITY_NAME, "investmentCriticality isnull");
        }

        if(!investmentCriticalityService.existsByDescription(siteDTO.getInvestmentCriticality().getDescription())){
            throw new BadRequestAlertException("Entity investmentCriticality dependency not exists", ENTITY_NAME, "investmentCriticality notexists");
        }
    }

    /**
     * {@code PUT  /sites/:id} : Updates an existing site.
     *
     * @param id the id of the siteDTO to save.
     * @param siteDTO the siteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated siteDTO,
     * or with status {@code 400 (Bad Request)} if the siteDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the siteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PutMapping(path="/sites/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SiteDTO> updateSite(@PathVariable(value = "id", required = false) final Long id, @RequestBody SiteDTO siteDTO)
            throws URISyntaxException {
        log.debug("REST request to update Site : {}, {}", id, siteDTO);

        if (!siteService.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "id not found");
        }

        if (siteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "id null");
        }

        if (!Objects.equals(id, siteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "id invalid");
        }else{
            if (siteService.existsByName(siteDTO.getName()) &&
                    !Objects.equals(id,siteService.getIdByName(siteDTO.getName()))) {
                throw new BadRequestAlertException("Entity not unique", ENTITY_NAME, "name not unique");
            }
        }

        SiteDTO result = siteService.update(siteDTO);
        return ResponseEntity
                .ok()
                .headers(
                        HeaderUtil
                                .createEntityUpdateAlert(applicationName, true, ENTITY_NAME, siteDTO.getId().toString()))
                .body(result);
    }

    /**
     * {@code PATCH  /sites/:id} : Partial updates given fields of an existing site, field will ignore if it is null
     *
     * @param id the id of the siteDTO to save.
     * @param siteDTO the siteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated siteDTO,
     * or with status {@code 400 (Bad Request)} if the siteDTO is not valid,
     * or with status {@code 404 (Not Found)} if the siteDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the siteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PatchMapping(path = "/sites/{id}", consumes = { "application/json", "application/merge-patch+json" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SiteDTO> partialUpdateSite(
            @PathVariable(value = "id", required = false) final Long id,
            @RequestBody SiteDTO siteDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Site partially : {}, {}", id, siteDTO);
        if (!siteService.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "id not found");
        }

        if (siteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "id null");
        }

        if (!Objects.equals(id, siteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "id invalid");
        }else{
            if (siteService.existsByName(siteDTO.getName()) &&
                    !Objects.equals(id,siteService.getIdByName(siteDTO.getName()))) {
                throw new BadRequestAlertException("Entity not unique", ENTITY_NAME, "name not unique");
            }
        }

        Optional<SiteDTO> result = siteService.partialUpdate(siteDTO);

        return ResponseUtil.wrapOrNotFound(
                result,
                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, siteDTO.getId().toString())
        );
    }

    /**
     * {@code DELETE  /sites/:id} : delete the "id" site.
     *
     * @param id the id of the siteDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @DeleteMapping(path="/sites/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteSite(@PathVariable Long id) {
        log.debug("REST request to delete Site : {}", id);
        siteService.delete(id);
        return ResponseEntity
                .noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .build();
    }

}

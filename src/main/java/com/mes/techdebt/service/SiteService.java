package com.mes.techdebt.service;

import com.mes.techdebt.domain.Site;
import com.mes.techdebt.service.dto.SiteDTO;
import com.mes.techdebt.web.rest.response.DashboardSiteAndCountryFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service Interface for managing {@link Site}.
 */
public interface SiteService {
    /**
     * Save a site.
     *
     * @param siteDTO the entity to save.
     * @return the persisted entity.
     */
    SiteDTO save(SiteDTO siteDTO);

    /**
     * Updates a site.
     *
     * @param siteDTO the entity to update.
     * @return the persisted entity.
     */
    SiteDTO update(SiteDTO siteDTO);

    /**
     * Partially updates a site.
     *
     * @param siteDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SiteDTO> partialUpdate(SiteDTO siteDTO);

    /**
     * Get all the sites.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SiteDTO> findAll(Pageable pageable);

    /**
     * Get the "id" site.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SiteDTO> findOne(Long id);

    Optional<List<DashboardSiteAndCountryFilterDTO>> findSitesByRegions(Set<String> regions);

    /**
     * Delete the "id" site.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Exists by the "id" site.
     *
     * @param id the id of the entity.
     * @return
     */
    boolean existsById(Long id);

    boolean existsByName(String name);

    boolean existsByMdmSiteId(Long mdmSiteId);

    Long getIdByName(String name);

    Long getIdByMdmSiteId(Long mdmSiteId);
}

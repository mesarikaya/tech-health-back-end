package com.mes.techdebt.service;

import com.mes.techdebt.domain.CostToFix;
import com.mes.techdebt.service.dto.CostToFixDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link CostToFix}.
 */
public interface CostToFixService {
    /**
     * Save a costToFix.
     *
     * @param costToFixDTO the entity to save.
     * @return the persisted entity.
     */
    CostToFixDTO save(CostToFixDTO costToFixDTO);

    /**
     * Updates a costToFix.
     *
     * @param costToFixDTO the entity to update.
     * @return the persisted entity.
     */
    CostToFixDTO update(CostToFixDTO costToFixDTO);

    /**
     * Partially updates a costToFix.
     *
     * @param costToFixDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CostToFixDTO> partialUpdate(CostToFixDTO costToFixDTO);

    /**
     * Get all the costToFixes.
     *
     * @return the list of entities.
     */
    List<CostToFixDTO> findAll();

    /**
     * Get the "id" costToFix.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CostToFixDTO> findOne(Long id);

    /**
     * Delete the "id" costToFix.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    boolean existsById(Long id);

    Optional<CostToFixDTO> findBySiteIdAndCategoryIdAndCostRangeId(Long siteId, Long categoryId, Long costRangeId);

    boolean existsBySiteId(Long siteId);

    boolean existsBySiteIdAndCategoryId(Long siteId, Long categoryId);
}

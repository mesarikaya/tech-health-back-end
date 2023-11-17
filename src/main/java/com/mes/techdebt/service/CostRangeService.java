package com.mes.techdebt.service;

import com.mes.techdebt.domain.CostRange;
import com.mes.techdebt.service.dto.CostRangeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link CostRange}.
 */
public interface CostRangeService {
    /**
     * Save a costRange.
     *
     * @param costRangeDTO the entity to save.
     * @return the persisted entity.
     */
    CostRangeDTO save(CostRangeDTO costRangeDTO);

    /**
     * Updates a costRange.
     *
     * @param costRangeDTO the entity to update.
     * @return the persisted entity.
     */
    CostRangeDTO update(CostRangeDTO costRangeDTO);

    /**
     * Partially updates a costRange.
     *
     * @param costRangeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CostRangeDTO> partialUpdate(CostRangeDTO costRangeDTO);

    /**
     * Get all the costRanges.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CostRangeDTO> findAll(Pageable pageable);

    /**
     * Get the "id" costRange.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CostRangeDTO> findOne(Long id);

    /**
     * Delete the "id" costRange.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    boolean existsById(Long id);

    boolean existsByDescription(String description);

    Long getIdByDescription(String description);
}

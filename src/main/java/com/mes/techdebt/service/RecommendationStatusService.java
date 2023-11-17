package com.mes.techdebt.service;

import com.mes.techdebt.domain.RecommendationStatus;
import com.mes.techdebt.service.dto.RecommendationStatusDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link RecommendationStatus}.
 */
public interface RecommendationStatusService {
    /**
     * Save a recommendationStatus.
     *
     * @param recommendationStatusDTO the entity to save.
     * @return the persisted entity.
     */
    RecommendationStatusDTO save(RecommendationStatusDTO recommendationStatusDTO);

    /**
     * Updates a recommendationStatus.
     *
     * @param recommendationStatusDTO the entity to update.
     * @return the persisted entity.
     */
    RecommendationStatusDTO update(RecommendationStatusDTO recommendationStatusDTO);

    /**
     * Partially updates a recommendationStatus.
     *
     * @param recommendationStatusDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<RecommendationStatusDTO> partialUpdate(RecommendationStatusDTO recommendationStatusDTO);

    /**
     * Get all the recommendationStatuses.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<RecommendationStatusDTO> findAll(Pageable pageable);

    /**
     * Get the "id" recommendationStatus.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<RecommendationStatusDTO> findOne(Long id);

    /**
     * Delete the "id" recommendationStatus.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    boolean existsById(Long id);

    boolean existsByDescription(String description);

    Long getIdByDescription(String description);
}

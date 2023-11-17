package com.mes.techdebt.service;

import com.mes.techdebt.domain.AssessmentCriteria;
import com.mes.techdebt.service.dto.AssessmentCriteriaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link AssessmentCriteria}.
 */
public interface AssessmentCriteriaService {
    /**
     * Save a assessmentCriteria.
     *
     * @param assessmentCriteriaDTO the entity to save.
     * @return the persisted entity.
     */
    AssessmentCriteriaDTO save(AssessmentCriteriaDTO assessmentCriteriaDTO);

    /**
     * Updates a assessmentCriteria.
     *
     * @param assessmentCriteriaDTO the entity to update.
     * @return the persisted entity.
     */
    AssessmentCriteriaDTO update(AssessmentCriteriaDTO assessmentCriteriaDTO);

    /**
     * Partially updates a assessmentCriteria.
     *
     * @param assessmentCriteriaDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AssessmentCriteriaDTO> partialUpdate(AssessmentCriteriaDTO assessmentCriteriaDTO);

    /**
     * Get all the assessmentCriteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AssessmentCriteriaDTO> findAll(Pageable pageable);

    /**
     * Get the "id" assessmentCriteria.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AssessmentCriteriaDTO> findOne(Long id);

    /**
     * Delete the "id" assessmentCriteria.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    boolean existsById(Long id);

    boolean existsByDescription(String description);

    Long getIdByDescription(String description);
}

package com.mes.techdebt.service;

import com.mes.techdebt.domain.AssessmentResult;
import com.mes.techdebt.service.dto.AssessmentResultDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link AssessmentResult}.
 */
public interface AssessmentResultService {
    /**
     * Save a assesmentResult.
     *
     * @param assessmentResultDTO the entity to save.
     * @return the persisted entity.
     */
    AssessmentResultDTO save(AssessmentResultDTO assessmentResultDTO);

    /**
     * Updates a assessmentResult.
     *
     * @param assessmentResultDTO the entity to update.
     * @return the persisted entity.
     */
    AssessmentResultDTO update(AssessmentResultDTO assessmentResultDTO);

    /**
     * Partially updates a assessmentResult.
     *
     * @param assessmentResultDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AssessmentResultDTO> partialUpdate(AssessmentResultDTO assessmentResultDTO);

    /**
     * Get all the assessmentResults.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AssessmentResultDTO> findAll(Pageable pageable);

    /**
     * Get the "id" assessmentResult.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AssessmentResultDTO> findOne(Long id);

    /**
     * Get all the assessmentResult based on siteName.
     *
     * @param siteName the site name of the searched entity.
     * @return the list of entities.
     */
    Optional<List<AssessmentResultDTO>> findBySiteName(String siteName);

    /**
     * Delete the "id" assessmentResult.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    boolean existsById(Long id);

    long countByAssessmentCriteriaId(Long id);

    long countByCategoryId(Long id);

    long countByTechAreaId(Long id);

    long countByTechDomainId(Long id);
}

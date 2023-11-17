package com.mes.techdebt.service;

import com.mes.techdebt.domain.InvestmentCriticality;
import com.mes.techdebt.service.dto.InvestmentCriticalityDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link InvestmentCriticality}.
 */
public interface InvestmentCriticalityService {
    /**
     * Save a investmentCriticality.
     *
     * @param investmentCriticalityDTO the entity to save.
     * @return the persisted entity.
     */
    InvestmentCriticalityDTO save(InvestmentCriticalityDTO investmentCriticalityDTO);

    /**
     * Updates a investmentCriticality.
     *
     * @param investmentCriticalityDTO the entity to update.
     * @return the persisted entity.
     */
    InvestmentCriticalityDTO update(InvestmentCriticalityDTO investmentCriticalityDTO);

    /**
     * Partially updates a investmentCriticality.
     *
     * @param investmentCriticalityDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<InvestmentCriticalityDTO> partialUpdate(InvestmentCriticalityDTO investmentCriticalityDTO);

    /**
     * Get all the investmentCriticalities.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<InvestmentCriticalityDTO> findAll(Pageable pageable);

    /**
     * Get the "id" investmentCriticality.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<InvestmentCriticalityDTO> findOne(Long id);

    /**
     * Delete the "id" investmentCriticality.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    boolean existsById(Long id);

    boolean existsByDescription(String description);

    Long getIdByDescription(String description);
}

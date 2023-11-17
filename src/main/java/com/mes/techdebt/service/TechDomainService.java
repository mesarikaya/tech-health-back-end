package com.mes.techdebt.service;

import com.mes.techdebt.domain.TechDomain;
import com.mes.techdebt.service.dto.TechDomainDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link TechDomain}.
 */
public interface TechDomainService {
    /**
     * Save a techDomain.
     *
     * @param techDomainDTO the entity to save.
     * @return the persisted entity.
     */
    TechDomainDTO save(TechDomainDTO techDomainDTO);

    /**
     * Updates a techDomain.
     *
     * @param techDomainDTO the entity to update.
     * @return the persisted entity.
     */
    TechDomainDTO update(TechDomainDTO techDomainDTO);

    /**
     * Partially updates a techDomain.
     *
     * @param techDomainDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TechDomainDTO> partialUpdate(TechDomainDTO techDomainDTO);

    /**
     * Get all the techDomains.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TechDomainDTO> findAll(Pageable pageable);

    /**
     * Get the "id" techDomain.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TechDomainDTO> findOne(Long id);

    /**
     * Delete the "id" techDomain.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    boolean existsById(Long id);

    boolean existsByDescription(String description);

    Long getIdByDescription(String description);
}

package com.mes.techdebt.service;

import com.mes.techdebt.domain.TechArea;
import com.mes.techdebt.service.dto.TechAreaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link TechArea}.
 */
public interface TechAreaService {
    /**
     * Save a techArea.
     *
     * @param techAreaDTO the entity to save.
     * @return the persisted entity.
     */
    TechAreaDTO save(TechAreaDTO techAreaDTO);

    /**
     * Updates a techArea.
     *
     * @param techAreaDTO the entity to update.
     * @return the persisted entity.
     */
    TechAreaDTO update(TechAreaDTO techAreaDTO);

    /**
     * Partially updates a techArea.
     *
     * @param techAreaDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TechAreaDTO> partialUpdate(TechAreaDTO techAreaDTO);

    /**
     * Get all the techAreas.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TechAreaDTO> findAll(Pageable pageable);

    /**
     * Get the "id" techArea.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TechAreaDTO> findOne(Long id);

    /**
     * Delete the "id" techArea.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    boolean existsById(Long id);

    boolean existsByDescription(String description);

    Long getIdByDescription(String description);
}

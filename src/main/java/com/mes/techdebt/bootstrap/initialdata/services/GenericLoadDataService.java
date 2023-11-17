package com.mes.techdebt.bootstrap.initialdata.services;

import java.util.List;

/**
 * Service Interface for managing a domain entity T.
 */
public interface GenericLoadDataService<T>{

    /**
     * Save a domain entity with specific id.
     *
     * @param domainDTO the entity to save.
     * @return the persisted entity.
     */
    Long saveWithSpecificId(T domainDTO);

    /**
     * Save all domain entities with specific id.
     *
     * @param domainDTO the entity to save.
     * @return the persisted entity.
     */
    List<Long> saveAllWithSpecificId(List<T> domainDTO);

}


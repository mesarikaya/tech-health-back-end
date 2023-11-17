package com.mes.techdebt.service;

import com.mes.techdebt.web.rest.request.LocationRequestDTO;
import com.mes.techdebt.web.rest.response.LocationResponseDTO;

import java.util.List;

public interface LocationService {

    /**
     * Get possible locations.
     *
     * @param locationRequestDTO entity to get.
     * @return the persisted entity.
     */
    List<LocationResponseDTO> getLocations(LocationRequestDTO locationRequestDTO);

    /**
     * Get site name list.
     *
     * @return the persisted entity.
     */
    List<String> getDistinctSiteNames();
}

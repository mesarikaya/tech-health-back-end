package com.mes.techdebt.web.rest.controller;

import com.mes.techdebt.service.LocationService;
import com.mes.techdebt.web.rest.errors.BadRequestAlertException;
import com.mes.techdebt.web.rest.request.LocationRequestDTO;
import com.mes.techdebt.web.rest.response.LocationResponseDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.jhipster.web.util.HeaderUtil;

import javax.validation.Valid;
import java.util.List;

/**
 * REST controller for managing {@link LocationResponseDTO}.
 */
@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Read') or hasAuthority('APPROLE_TechHealth_User_Write') or hasAuthority('APPROLE_TechHealth_User_Admin')")
public class LocationController {

    private static final String ENTITY_NAME = "location";

    @Value("${spring.application.name}")
    private String applicationName;

    private final LocationService locationService;

    /**
     * {@code POST  /locations} : get all the location data.
     *
     * @return the {@link LocationResponseDTO} with status {@code 200 (OK)} and the list of locations in body.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PostMapping(path="/locations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LocationResponseDTO>> getLocations(@RequestBody @Valid LocationRequestDTO locationRequest) {
        log.debug("REST location request to get site update/create filters");

        if (locationRequest.getSiteName() == null || locationRequest.getSiteName().isBlank() ) {
            throw new BadRequestAlertException("A site name has to be provided", ENTITY_NAME, "site_name notexists");
        }

        List<LocationResponseDTO> result = locationService.getLocations(locationRequest);
        return ResponseEntity
                .ok()
                .headers(
                        HeaderUtil
                                .createAlert(applicationName, ENTITY_NAME, locationRequest.getSiteName()))
                .body(result);
    }

    /**
     * {@code GET  /locations/sites} : get all distinct sites.
     *
     * @return the {@link LocationResponseDTO} with status {@code 200 (OK)} and the list of locations in body.
     */
    @PreAuthorize("hasAuthority('APPROLE_TechHealth_User_Admin')")
    @PostMapping(path="/location/sites", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getDistinctSiteNames() {
        log.debug("REST location request to get site update/create filters");
        List<String> result = locationService.getDistinctSiteNames();
        return ResponseEntity
                .ok()
                .body(result);
    }
}

package com.mes.techdebt.service.impl;

import com.mes.techdebt.service.LocationService;
import com.mes.techdebt.web.rest.request.DistinctSitesRequestDTO;
import com.mes.techdebt.web.rest.request.DistinctSitesResponseDTO;
import com.mes.techdebt.web.rest.request.LocationRequestDTO;
import com.mes.techdebt.web.rest.request.LocationRequestWrapperDTO;
import com.mes.techdebt.web.rest.response.LocationResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LocationServiceImpl implements LocationService {

    private final WebClient defaultWebClient;

    public LocationServiceImpl(@Qualifier("defaultWebClient") WebClient defaultWebClient,
                               @Value("${location_data.url}") String url) {
        this.defaultWebClient = defaultWebClient;
    }

    @Override
    public List<LocationResponseDTO> getLocations(LocationRequestDTO locationRequestDTO) {
        log.debug("Making calls to initiate location master data request: {}", locationRequestDTO);
        LocationRequestWrapperDTO locationRequestWrapperDTO = new LocationRequestWrapperDTO();
        locationRequestWrapperDTO.setSiteMasterFlag(List.of("y"));
        locationRequestWrapperDTO.setSiteName(List.of(locationRequestDTO.getSiteName()));
        log.debug("Converted Request: {}", locationRequestWrapperDTO);

        List<LocationResponseDTO> response = defaultWebClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("columns", "site_name,mdm_site_id,region,enterprise,business_group," +
                                "reporting_unit,country,country_desc,city,state,latitude,longitude,address_line_1," +
                                "address_line_2,address_line_3,address_line_4")
                        .queryParam("hasDistinctRecords", "true")
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.CONNECTION, "keep-alive")
                .body(Mono.just(locationRequestWrapperDTO), LocationRequestWrapperDTO.class)
                .retrieve()
                .bodyToFlux(LocationResponseDTO.class)
                .collectList()
                .block(Duration.ofSeconds(50));
        return response;
    }

    @Override
    public List<String> getDistinctSiteNames() {

        DistinctSitesRequestDTO distinctSitesRequestDTO = new DistinctSitesRequestDTO();
        distinctSitesRequestDTO.setSiteMasterFlag(List.of("y"));
        log.debug("Converted Request: {}", distinctSitesRequestDTO);

        List<DistinctSitesResponseDTO>  distinctSites = defaultWebClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("columns", "site_name")
                        .queryParam("hasDistinctRecords", "true")
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.CONNECTION, "keep-alive")
                .body(Mono.just(distinctSitesRequestDTO), DistinctSitesRequestDTO.class)
                .retrieve()
                .bodyToFlux(DistinctSitesResponseDTO.class)
                .collectList()
                .block(Duration.ofSeconds(50));

        if (distinctSites==null){
            return Collections.emptyList();
        }

        return distinctSites.stream()
                .map( distinctSitesResponseDTO -> distinctSitesResponseDTO.getSiteName())
                .collect(Collectors.toList());
    }
}

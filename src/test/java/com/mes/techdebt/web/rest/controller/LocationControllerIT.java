package com.mes.techdebt.web.rest.controller;

import com.mes.techdebt.IntegrationTest;
import com.mes.techdebt.web.rest.controller.utils.TestUtil;
import com.mes.techdebt.web.rest.request.LocationRequestDTO;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link AttachmentController} REST controller.
 */
@Disabled
@AutoConfigureMockMvc
@IntegrationTest
@Slf4j
class LocationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getLocationDataSites() throws Exception {
        // Get all the available site names
        MvcResult result = getAvailableSiteNames();
        String responseString = result.getResponse().getContentAsString();
        List<String> responseList = JsonPath.read(responseString, "$");
        assertThat(responseList.size()).isGreaterThan(0);
    }

    private MvcResult getAvailableSiteNames() throws Exception {
        return mockMvc
                .perform(
                        post( "/api/v1/location/sites")
                                .with(jwt().authorities(TestUtil.adminAuthority))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
    }

    @Test
    void getLocationDataForSpecificSite() throws Exception {
        // Get all the available site names
        MvcResult result = getAvailableSiteNames();
        String response = result.getResponse().getContentAsString();
        List<String> siteNames = JsonPath.parse(response).read("$");
        log.debug("site names: {}", siteNames.get(0));
        assertThat(siteNames.size()).isGreaterThan(0);

        // Get all the data for a specific site name
        LocationRequestDTO locationRequestDTO = LocationRequestDTO.builder()
                .siteName(siteNames.get(0)).build();
        mockMvc
                .perform(
                        post( "/api/v1/locations")
                                .content(TestUtil.convertObjectToJsonBytes(locationRequestDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(jwt()
                                .authorities(TestUtil.adminAuthority))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].site_name").value(hasItem(locationRequestDTO.getSiteName())))
                .andExpect(jsonPath("$.[*].mdm_site_id").exists())
                .andExpect(jsonPath("$.[*].region").exists())
                .andExpect(jsonPath("$.[*].enterprise").exists())
                .andExpect(jsonPath("$.[*].business_group").exists())
                .andExpect(jsonPath("$.[*].reporting_unit").exists())
                .andExpect(jsonPath("$.[*].country_desc").exists())
                .andExpect(jsonPath("$.[*].country").exists())
                .andExpect(jsonPath("$.[*].city").exists())
                .andExpect(jsonPath("$.[*].state").exists())
                .andExpect(jsonPath("$.[*].latitude").exists())
                .andExpect(jsonPath("$.[*].longitude").exists())
                .andExpect(jsonPath("$.[*].mdm_site_id").exists())
                .andExpect(jsonPath("$.[*].address_line_1").exists())
                .andExpect(jsonPath("$.[*].address_line_2").exists())
                .andExpect(jsonPath("$.[*].address_line_3").exists())
                .andExpect(jsonPath("$.[*].address_line_4").exists());
    }
}
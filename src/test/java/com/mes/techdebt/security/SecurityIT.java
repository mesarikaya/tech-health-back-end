package com.mes.techdebt.security;

import com.mes.techdebt.IntegrationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the security layer
 */
@AutoConfigureMockMvc
@IntegrationTest
@Slf4j
public class SecurityIT {

    @Autowired
    private MockMvc mockMvc;

    @Value("${cors.dev_allowed_origin}")
    private String devAllowedOrigin;

    @Value("${cors.stage_allowed_origin}")
    private String stageAllowedOrigin;

    @Value("${cors.prod_allowed_origin}")
    private String prodAllowedOrigin;

    @Value("${cors.local_allowed_origin}")
    private String localAllowedOrigin;

    @Value("${cors.api_gateway_dev}")
    private String gatewayDevAllowedOrigin;

    @Value("${cors.api_gateway_stage}")
    private String gatewayStageAllowedOrigin;

    @Value("${cors.api_gateway_prod}")
    private String gatewayProdAllowedOrigin;

    @Test
    void testDevCorsFilter() throws Exception {
        checkCors(devAllowedOrigin);
    }

    @Test
    void testStageCorsFilter() throws Exception {
        checkCors(stageAllowedOrigin);
    }

    @Test
    void testProdCorsFilter() throws Exception {
        checkCors(prodAllowedOrigin);
    }

    @Test
    void testGatewayLocalDevCorsFilter() throws Exception {
        checkCors(localAllowedOrigin);
    }

    @Test
    void testGatewayDevCorsFilter() throws Exception {
        checkCors(gatewayDevAllowedOrigin);
    }

    @Test
    void testGatewayStageCorsFilter() throws Exception {
        checkCors(gatewayStageAllowedOrigin);
    }

    @Test
    void testGatewayProdCorsFilter() throws Exception {
        checkCors(gatewayProdAllowedOrigin);
    }

    private void checkCors(String allowedOrigin) throws Exception {
        String ENTITY_API_URL = "/dashboard-data/hierarchy";
        MvcResult result = mockMvc
                .perform(options(ENTITY_API_URL)
                        .header("Access-Control-Request-Method", "POST")
                        .header("Origin", allowedOrigin)
                )
                .andExpect(status().isOk()).andReturn();

        MockHttpServletResponse mockResponse = result.getResponse();
        Collection<String> responseHeaders = mockResponse.getHeaderNames();
        assertThat(responseHeaders).isNotNull();
        assertThat(responseHeaders.size()).isBetween(5, 15);
    }
}

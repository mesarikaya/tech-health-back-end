package com.mes.techdebt.service.dto;

import com.mes.techdebt.domain.Site;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link Site} entity.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SiteDTO implements Serializable {

    private static final long serialVersionUID = -8491475125828816221L;

    private Long id;

    private String name;

    private Long mdmSiteId;

    private String mdmSiteName;

    private String comment;

    private Boolean isActive;

    private String region;

    private String enterprise;

    private String businessGroup;

    private String reportingUnit;

    private String address;

    private String country;

    private String countryCode;

    private String city;

    private String state;

    private Double latitude;

    private Double longitude;

    @JsonProperty("investmentCriticalityId")
    private Long investment_criticality_id;

    private InvestmentCriticalityDTO investmentCriticality;
}

package com.mes.techdebt.web.rest.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationResponseDTO implements Serializable {
    private static final long serialVersionUID = -5107018908595368059L;

    @JsonProperty("site_name")
    private String siteName;

    @JsonProperty("mdm_site_id")
    private Integer mdmSiteId;

    @JsonProperty("region")
    private String region;

    @JsonProperty("enterprise")
    private String enterprise;

    @JsonProperty("business_group")
    private String businessGroup;

    @JsonProperty("reporting_unit")
    private String reportingUnit;

    @JsonProperty("country_desc")
    private String country;

    @JsonProperty("country")
    private String countryCode;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("latitude")
    private Double latitude;

    @JsonProperty("longitude")
    private Double longitude;

    @JsonProperty("address_line_1")
    private String addressLine1;

    @JsonProperty("address_line_2")
    private String addressLine2;

    @JsonProperty("address_line_3")
    private String addressLine3;

    @JsonProperty("address_line_4")
    private String addressLine4;
}

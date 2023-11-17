package com.mes.techdebt.service.dto;

import com.mes.techdebt.domain.CostToFix;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link CostToFix} entity.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CostToFixDTO implements Serializable {

    private static final long serialVersionUID = -3284266079798831906L;

    private Long id;

    @JsonProperty("siteId")
    private Long site_id;

    @JsonProperty("categoryId")
    private Long category_id;

    @JsonProperty("costRangeId")
    private Long cost_range_id;

    private SiteDTO site;

    private CostRangeDTO costRange;

    private CategoryDTO category;
}

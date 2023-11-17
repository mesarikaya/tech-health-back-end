package com.mes.techdebt.service.dto;

import com.mes.techdebt.domain.AssessmentCriteria;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link AssessmentCriteria} entity.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssessmentCriteriaDTO implements Serializable {

    private static final long serialVersionUID = 7577088252400953933L;

    private Long id;

    private String description;

    @JsonProperty("tech_standard_description")
    private String techStandardDescription;
    
    @JsonProperty("isActive")
    private Boolean isActive;

    @JsonProperty("categoryId")
    private Long category_id;

    private CategoryDTO category;
}

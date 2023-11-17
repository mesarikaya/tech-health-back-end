package com.mes.techdebt.service.dto;

import com.mes.techdebt.domain.Category;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link Category} entity.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryDTO implements Serializable {

    private static final long serialVersionUID = 621052904242490834L;

    private Long id;

    private String description;

    @JsonProperty("isActive")
    private Boolean isActive;

    @JsonProperty("techAreaId")
    private Long tech_area_id;

    private TechAreaDTO techArea;
}

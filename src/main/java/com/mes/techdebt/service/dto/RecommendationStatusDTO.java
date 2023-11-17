package com.mes.techdebt.service.dto;

import com.mes.techdebt.domain.RecommendationStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link RecommendationStatus} entity.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecommendationStatusDTO implements Serializable {

    private static final long serialVersionUID = 1545977539720682225L;

    private Long id;

    private String description;
}

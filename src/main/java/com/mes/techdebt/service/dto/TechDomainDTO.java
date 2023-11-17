package com.mes.techdebt.service.dto;

import com.mes.techdebt.domain.TechDomain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link TechDomain} entity.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TechDomainDTO implements Serializable {

    private static final long serialVersionUID = 3963768594138829402L;

    private Long id;

    private String description;

    @JsonProperty("isActive")
    private Boolean isActive;
}

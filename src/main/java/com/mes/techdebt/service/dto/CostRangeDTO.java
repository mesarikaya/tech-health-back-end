package com.mes.techdebt.service.dto;

import com.mes.techdebt.domain.CostRange;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link CostRange} entity.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CostRangeDTO implements Serializable {

    private static final long serialVersionUID = 1024040803794665895L;

    private Long id;

    private String description;
}

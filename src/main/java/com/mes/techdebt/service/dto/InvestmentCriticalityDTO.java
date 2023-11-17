package com.mes.techdebt.service.dto;

import com.mes.techdebt.domain.InvestmentCriticality;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link InvestmentCriticality} entity.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvestmentCriticalityDTO implements Serializable {

    private static final long serialVersionUID = 8705364568545265308L;

    private Long id;

    private String description;
}

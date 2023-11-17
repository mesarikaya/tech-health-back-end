package com.mes.techdebt.web.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * A DTO for site name retrieval.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SiteRequestDTO {
    @NotNull
    private Set<String> region;
}

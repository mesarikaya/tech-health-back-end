package com.mes.techdebt.web.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class HierarchyResponseDTO implements Serializable {

    private static final long serialVersionUID = -5616472621764670801L;

    @NotNull
    private String assessmentCriteriaDescription;

    @NotNull
    private Long assessmentCriteriaId;

    @NotNull
    private String categoryDescription;

    @NotNull
    private Long categoryId;

    @NotNull
    private String techAreaDescription;

    @NotNull
    private Long techAreaId;

    @NotNull
    private String domainDescription;

    @NotNull
    private Long domainId;
}

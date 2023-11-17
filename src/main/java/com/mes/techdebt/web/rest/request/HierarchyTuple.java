package com.mes.techdebt.web.rest.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
public class HierarchyTuple implements Serializable {

    private static final long serialVersionUID = -5616472621764670801L;

    @NotNull
    private String assessmentCriteriaDescription;

    @NotNull
    private String categoryDescription;

    @NotNull
    private String techAreaDescription;

    @NotNull
    private String domainDescription;

    @Override
    public String toString() {
        return assessmentCriteriaDescription + "-" + categoryDescription + "-" + techAreaDescription + '-' + domainDescription;
    }
}
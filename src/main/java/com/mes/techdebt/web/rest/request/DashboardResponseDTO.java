package com.mes.techdebt.web.rest.request;

import com.mes.techdebt.service.dto.AssessmentResultDTO;
import com.mes.techdebt.web.rest.response.DashboardAttachmentListDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * A DTO for the dashboard data request response.
 */
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardResponseDTO implements Serializable {

    private static final long serialVersionUID = -1837386356818612155L;

    @NotNull
    private String siteName;

    @NotNull
    private Long siteId;

    @NotNull
    private String assessmentCriteriaDescription;

    @NotNull
    private Long assessmentCriteriaId;

    private String techStandardDescription;

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

    @NotNull
    private List<AssessmentResultDTO> assessmentResults;

    private List<DashboardAttachmentListDTO> attachments;
}

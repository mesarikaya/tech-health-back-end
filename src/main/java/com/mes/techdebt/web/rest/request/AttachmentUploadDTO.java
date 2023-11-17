package com.mes.techdebt.web.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttachmentUploadDTO implements Serializable {

    private static final long serialVersionUID = 4931674983753872274L;

    @NotNull
    @JsonProperty("siteId")
    private Long siteId;

    @JsonProperty("assessmentCriteriaId")
    private Long assessmentCriteriaId;

    @NotNull
    @JsonProperty("createdBy")
    private String createdBy;

    @NotNull
    @JsonProperty("createdBy")
    private String updatedBy;
}

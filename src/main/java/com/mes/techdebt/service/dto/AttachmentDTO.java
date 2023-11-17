package com.mes.techdebt.service.dto;

import com.mes.techdebt.config.OffsetDateTimeDeserializer;
import com.mes.techdebt.config.OffsetDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * A Attachment.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttachmentDTO implements Serializable {

    private static final long serialVersionUID = 1331483954354685709L;

    private Long id;

    @Column(name = "fileName")
    private String fileName;

    @Column(name = "fileType")
    private String fileType;

    @Column(name = "fileSize")
    private Long fileSize;

    @Column(name = "data")
    private byte[] data;

    @Column(name = "stringData")
    private String stringData;

    @JsonProperty("siteId")
    private Long site_id;

    @JsonProperty("assessmentCriteriaId")
    private Long assessment_criteria_id;

    @JsonProperty("recommendationStatusId")
    private Long recommendation_status_id;

    private AssessmentCriteriaDTO assessmentCriteria;

    private SiteDTO site;

    //@JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty("createDate")
    @JsonSerialize(using= OffsetDateTimeSerializer.class)
    @JsonDeserialize(using= OffsetDateTimeDeserializer.class)
    private OffsetDateTime createDate;

    @JsonProperty("createdBy")
    private String createdBy;

    //@JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(value="updateDate")
    @JsonSerialize(using= OffsetDateTimeSerializer.class)
    @JsonDeserialize(using= OffsetDateTimeDeserializer.class)
    private OffsetDateTime updateDate;

    @JsonProperty("updatedBy")
    private String updatedBy;
}

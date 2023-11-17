package com.mes.techdebt.web.rest.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardAttachmentListDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3941448601900526606L;

    private Long id;
    private String attachmentEndpoint;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private OffsetDateTime updateDate;
    private String updatedBy;
}

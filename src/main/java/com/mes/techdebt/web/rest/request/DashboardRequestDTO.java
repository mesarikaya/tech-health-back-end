package com.mes.techdebt.web.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A DTO for dashboard data request.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardRequestDTO implements Serializable {

    private static final long serialVersionUID = 6186855270206791957L;

    @NotNull
    private String name;
}

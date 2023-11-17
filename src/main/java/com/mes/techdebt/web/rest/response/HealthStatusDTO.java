package com.mes.techdebt.web.rest.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public final class HealthStatusDTO {
    private final String status;
}

package com.mes.techdebt.web.rest.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardSiteAndCountryFilterDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3850043005369075933L;
    private String site;
    private String region;
    private Long siteId;
    private String country;
    private boolean isActive;
}

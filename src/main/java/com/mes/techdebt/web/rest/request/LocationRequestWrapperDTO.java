package com.mes.techdebt.web.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationRequestWrapperDTO implements Serializable {

    private static final long serialVersionUID = -5107018908595368059L;

    @JsonProperty("site_name")
    private List<String> siteName;

    @JsonProperty("site_master_flag")
    private List<String> siteMasterFlag;


}

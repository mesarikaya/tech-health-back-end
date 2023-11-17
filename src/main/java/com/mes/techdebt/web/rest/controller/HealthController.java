package com.mes.techdebt.web.rest.controller;

import com.mes.techdebt.web.rest.response.HealthStatusDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class HealthController {
    @GetMapping(path = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public HealthStatusDTO healthCheck() {
        return HealthStatusDTO.builder().status("UP").build();
    }
}

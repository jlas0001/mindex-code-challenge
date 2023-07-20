package com.mindex.challenge.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;

@RestController
public class CompensationController {
    private static final Logger LOG = LoggerFactory.getLogger(CompensationController.class);

    @Autowired
    CompensationService compensationService;
    
    @PostMapping(path = "/compensation")
    public Compensation create(@RequestBody Compensation data) {
        LOG.debug("Received Compensation create request for [{}]", data);
        return compensationService.create(data);
    }

    @GetMapping(path = "/compensation/byEmployee/{employeeId}")
    public List<Compensation> findByEmployeeId(@PathVariable String employeeId) {
        LOG.debug("Received Compensation find request for employee id [{}]", employeeId);
        return compensationService.findByEmployeeId(employeeId);
    }
}

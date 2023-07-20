package com.mindex.challenge.service.impl;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;

@Service
public class CompensationServiceImpl implements CompensationService {
    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);

    @Autowired
    CompensationRepository compensationRepository;

    @Override
    public Compensation create(Compensation compensation) {
        LOG.debug("Creating compensation [{}]", compensation);
        compensation.setCompensationId(UUID.randomUUID().toString());
        return compensationRepository.insert(compensation);
    }

    @Override
    public List<Compensation> findByEmployeeId(String employeeId) {
        LOG.debug("Finding compensation by employee id [{}]", employeeId);
        return compensationRepository.findByEmployee_employeeId(employeeId);
    }
    
}

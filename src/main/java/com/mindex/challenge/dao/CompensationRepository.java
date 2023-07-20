package com.mindex.challenge.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.mindex.challenge.data.Compensation;

@Repository
public interface CompensationRepository extends MongoRepository<Compensation,String> {
    Compensation findByCompensationId(String compensationId);
    List<Compensation> findByEmployee_employeeId(String employeeId);
}

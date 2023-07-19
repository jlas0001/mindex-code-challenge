package com.mindex.challenge.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {

    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public ReportingStructure read(String employeeId) {
        LOG.debug("Getting reporting structure for employee [{}]", employeeId);
        Employee emp = employeeRepository.findByEmployeeId(employeeId);

        if (emp == null) {
            LOG.debug("Employee not found [{}]", employeeId);
            throw new RuntimeException("Invalid employeeId: " + employeeId);
        }

        ReportingStructure o = new ReportingStructure();
        o.setEmployee(emp);
        o.setNumberOfReports(countReports(emp, new HashSet<String>()));
        return o;
    }

    /*
     * Count the distinct employees in the reporting structure.  The 
     * countedEmployees set keeps track of who has already been counted, so
     * that they aren't counted multiple times, in case there is an employee
     * who is listed as a report of multiple other employees.  This will also
     * prevent an infinite loop if there is a cycle such as: A -> B -> C -> A
     */
    private int countReports(Employee emp, Set<String> countedEmployees) {
        int numReports = 0;
        if (emp.getDirectReports() != null) {
            for (Employee report: emp.getDirectReports()) {
                if (!countedEmployees.contains(report.getEmployeeId())) {
                    countedEmployees.add(report.getEmployeeId());
                    Employee reportPopulated = employeeRepository.findByEmployeeId(report.getEmployeeId());
                    numReports += (1 + countReports(reportPopulated, countedEmployees));
                }
            }
        }
        return numReports;
    }
    
}

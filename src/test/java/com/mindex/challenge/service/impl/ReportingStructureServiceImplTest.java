package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportingStructureServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String reportingStructureUrl;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        reportingStructureUrl = "http://localhost:" + port + "/reportingStructure/{id}";
    }

    @Test
    public void testReportingStructure() {
        // Read checks
        String[][] employeeChecks = {
            {"16a596ae-edd3-4847-99fe-c4518e82c86f","Lennon","4"},
            {"b7839309-3348-463b-a7e3-5de1c168beb3","McCartney","0"},
            {"03aa1462-ffa9-4978-901b-7c001562cf6f","Starr","2"},
            {"62c1084e-6e34-4630-93fd-9153afb65309","Best","0"},
            {"c0c2293d-16bd-4603-8e08-638a9d18b22c","Harrison","0"}
        };

        for (String[] employee: employeeChecks ) {
            String employeeId = employee[0];
            String lastName = employee[1];
            int expectedReports = Integer.parseInt(employee[2]);

            ReportingStructure readReportingStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class,  employeeId).getBody();
            assertEquals( readReportingStructure.getNumberOfReports(), expectedReports );
            // to make sure the correct Employee object is attached, check the last name too
            assertEquals( readReportingStructure.getEmployee().getLastName(), lastName );
        }
    }

    /**
     * Test to make sure nodes in the graph that are repeated are not counted twice.
     */
    @Test
    public void testDistinctReports() {
        // create 4 employees with one being a direct report of 2 different employees, creating a loop in the graph:
        //   A
        //  / \
        //  B  C
        //  \ /
        //   D
        Employee empA = createEmployee("Employee","A","Engineering","Manager 1");
        Employee empB = createEmployee("Employee","B","Engineering","Manager 2");
        Employee empC = createEmployee("Employee","C","Engineering","Manager 2");
        Employee empD = createEmployee("Employee","D","Engineering","Developer");
        empA = addDirectReports(empA, empB, empC);
        empB = addDirectReports(empB, empD);
        empC = addDirectReports(empC, empD);
        
        String[][] employeeChecks = {
            {empA.getEmployeeId(),empA.getLastName(),"3"},
            {empB.getEmployeeId(),empB.getLastName(),"1"},
            {empC.getEmployeeId(),empC.getLastName(),"1"},
            {empD.getEmployeeId(),empD.getLastName(),"0"}
        };

        for (String[] employee: employeeChecks ) {
            String employeeId = employee[0];
            String lastName = employee[1];
            int expectedReports = Integer.parseInt(employee[2]);

            ReportingStructure readReportingStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class,  employeeId).getBody();
            assertEquals( readReportingStructure.getNumberOfReports(), expectedReports );
            // to make sure the correct Employee object is attached, check the last name too
            assertEquals( readReportingStructure.getEmployee().getLastName(), lastName );
        }

    }

    @Test
    public void testErrorResponse() {
        String invalidEmployeeId = "invalid_id";
        ResponseEntity<String> result = restTemplate.getForEntity(reportingStructureUrl, String.class,  invalidEmployeeId);
        assertTrue(result.getBody().contains("Invalid employeeId"));
        assertEquals(500, result.getStatusCodeValue());
    }

    private Employee createEmployee(String firstName, String lastName, String department, String position) {
        Employee emp = new Employee();
        emp.setFirstName(firstName);
        emp.setLastName(lastName);
        emp.setDepartment(department);
        emp.setPosition(position);
        emp = restTemplate.postForEntity(employeeUrl, emp, Employee.class).getBody();

        return emp;
    }

    private Employee addDirectReports( Employee manager, Employee... reports ) {
        if ( manager.getDirectReports() == null ) {
            manager.setDirectReports(new ArrayList<Employee>());
        }

        for ( Employee report: reports ) {
            Employee reportId = new Employee();
            reportId.setEmployeeId(report.getEmployeeId());
            manager.getDirectReports().add(reportId);
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        manager =
            restTemplate.exchange(employeeIdUrl,
                    HttpMethod.PUT,
                    new HttpEntity<Employee>(manager, headers),
                    Employee.class,
                    manager.getEmployeeId()).getBody();
        return manager;
    }
}

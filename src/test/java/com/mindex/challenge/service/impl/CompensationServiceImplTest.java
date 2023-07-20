package com.mindex.challenge.service.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {

    private String compensationUrl;
    private String compensationByEmployeeIdUrl;

    @LocalServerPort
    private int port;

    @Autowired TestRestTemplate restTemplate;


    @Before
    public void setup() {
        compensationUrl = "http://localhost:" + port + "/compensation";
        compensationByEmployeeIdUrl = "http://localhost:" + port + "/compensation/byEmployee/{id}";
    }

    @Test
    public void testCreateAndReadCompensation() {
        Map<String,List<Pair<BigDecimal,LocalDate>>> testData = new HashMap<>();
        List<Pair<BigDecimal,LocalDate>> list = new ArrayList<>();
        testData.put("16a596ae-edd3-4847-99fe-c4518e82c86f",list);
        list.add(Pair.of(new BigDecimal("1000.00"), LocalDate.of(1950,01,01)));
        list.add(Pair.of(new BigDecimal("100000.00"), LocalDate.of(2020,12,31)));
        list.add(Pair.of(new BigDecimal("500000.17"), LocalDate.of(2023,07,01)));

        list = new ArrayList<>();
        testData.put("c0c2293d-16bd-4603-8e08-638a9d18b22c",list);
        list.add(Pair.of(new BigDecimal("0"), LocalDate.of(2000,01,01)));
        list.add(Pair.of(new BigDecimal("100001.00"), LocalDate.of(2023,12,01)));

        list = new ArrayList<>();
        testData.put("c0c2293d-16bd-4603-8e08-638a9d18b22c",list);

        for (String empId: testData.keySet()) {
            for ( Pair<BigDecimal,LocalDate> compData: testData.get(empId) ) {
                createCompensation(empId, compData.getFirst(), compData.getSecond());
            }
        }


        for (String empId: testData.keySet()) {

            List<Compensation> compList = restTemplate.exchange(
                compensationByEmployeeIdUrl, 
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Compensation>>(){},  
                empId).getBody();
            
            List<Pair<BigDecimal,LocalDate>> testCompList = testData.get(empId);
            assertEquals( compList.size(), testCompList.size() );
            
            // make sure each entry in the test data matches one in the retrieved data
            for ( Pair<BigDecimal,LocalDate> compData: testData.get(empId) ) {
                boolean found = false;
                for (Compensation retrievedComp: compList) {
                    if (retrievedComp.getEffectiveDate().equals(compData.getSecond()) && retrievedComp.getSalary().equals(compData.getFirst())) {
                        found = true;
                        break;
                    }
                }
                assertTrue(found);
            }
        }
    }

    private Compensation createCompensation(String employeeId, BigDecimal salary, LocalDate effectiveDate) {
        Compensation comp = new Compensation();
        Employee emp = new Employee();
        emp.setEmployeeId(employeeId);
        comp.setEmployee(emp);
        comp.setSalary(salary);
        comp.setEffectiveDate(effectiveDate);
        comp = restTemplate.postForEntity(compensationUrl, comp, Compensation.class).getBody();

        return comp;
    }
}

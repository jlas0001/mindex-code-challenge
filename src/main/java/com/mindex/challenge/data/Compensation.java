package com.mindex.challenge.data;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Compensation {
    @Id
    String compensationId;

    @DBRef
    Employee employee;

    BigDecimal salary;

    @JsonFormat(pattern="yyyy-MM-dd")
    LocalDate effectiveDate;

    public String getCompensationId() {
        return compensationId;
    }
    public void setCompensationId(String compensationId) {
        this.compensationId = compensationId;
    }
    public Employee getEmployee() {
        return employee;
    }
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
    public BigDecimal getSalary() {
        return salary;
    }
    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }
    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }
    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
}

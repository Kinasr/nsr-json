package test_helper;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Accessors(chain = true)
@Data
public class Company {
    String name;
    String address;
    Integer numOfEmployees;
    LocalDateTime establishDate;

    public Company() {
    }

    public Company(String name, Integer numOfEmployees) {
        this.name = name;
        this.numOfEmployees = numOfEmployees;
    }

    public Company(String name, String address, Integer numOfEmployees) {
        this.name = name;
        this.address = address;
        this.numOfEmployees = numOfEmployees;
    }

    public Company(String name, Integer numOfEmployees, LocalDateTime establishDate) {
        this.name = name;
        this.numOfEmployees = numOfEmployees;
        this.establishDate = establishDate;
    }
}

package test_helper;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Data
public class Person {
    private Integer id;
    private String name;
    private String email;
    private Integer age;
    private Double balance;
    private Boolean isMarried;
    private Long phoneNumber;
    private Calendar dateOfBirth;
    private LocalDateTime dateOfBirth2;
    private List<String> pets;
    private Map<String, String> skills;
    private Company workAt;
    private List<Company> previousCompanies;
    private Map<String, Company> companiesOrder;

    public Person(Integer id, String name, LocalDateTime dateOfBirth2) {
        this.id = id;
        this.name = name;
        this.dateOfBirth2 = dateOfBirth2;
    }

    public Person(Integer id, String name, String email, Integer age, Map<String, String> skills) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.skills = skills;
    }

    public Person() {
    }

    public Person(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Person(Integer id, String name, String email, Integer age) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
    }

    public Person(Integer id, String name, String email, Integer age, List<String> pets) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.pets = pets;
    }

    public Person(Integer id, String name, String email, Integer age, Calendar dateOfBirth) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.dateOfBirth = dateOfBirth;
    }

    public Person(Integer id, String name, String email, Integer age, Boolean isMarried) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.isMarried = isMarried;
    }

    public Person(Integer id, String name, Long phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public Person(Integer id, String name, Double balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    public Person(Integer id, String name, Company workAt) {
        this.id = id;
        this.name = name;
        this.workAt = workAt;
    }
}

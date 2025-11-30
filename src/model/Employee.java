package model;

public class Employee {

    private String empSSN;
    private Double salary;         // can be null
    private String position;
    private String firstName;
    private String middleInitial;
    private String lastName;

    public Employee() {}

    public Employee(String empSSN, Double salary, String position,
                    String firstName, String middleInitial, String lastName) {
        this.empSSN = empSSN;
        this.salary = salary;
        this.position = position;
        this.firstName = firstName;
        this.middleInitial = middleInitial;
        this.lastName = lastName;
    }

    public String getEmpSSN() {
        return empSSN;
    }

    public void setEmpSSN(String empSSN) {
        this.empSSN = empSSN;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleInitial() {
        return middleInitial;
    }

    public void setMiddleInitial(String middleInitial) {
        this.middleInitial = middleInitial;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}

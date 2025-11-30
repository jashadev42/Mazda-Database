package model;

public class Customer {
    private String licenseId;
    private String firstName;
    private String middleInitial;
    private String lastName;
    private String address;
    private String email;
    private String phone;

    public Customer() {}

    public Customer(String licenseId, String firstName, String middleInitial,
                    String lastName, String address, String email, String phone) {
        this.licenseId = licenseId;
        this.firstName = firstName;
        this.middleInitial = middleInitial;
        this.lastName = lastName;
        this.address = address;
        this.email = email;
        this.phone = phone;
    }

    // getters & setters
    public String getLicenseId() { return licenseId; }
    public void setLicenseId(String licenseId) { this.licenseId = licenseId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleInitial() { return middleInitial; }
    public void setMiddleInitial(String middleInitial) { this.middleInitial = middleInitial; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
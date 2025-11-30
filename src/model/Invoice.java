package model;

public class Invoice {

    private Integer invoiceId;
    private String empSSN;
    private String licenseId;
    private Double amount;
    private String purpose;
    private String date; // yyyy-MM-dd

    // Extra display fields
    private String customerFirstName;
    private String customerLastName;
    private String employeeFirstName;
    private String employeeLastName;

    public Invoice() {}

    // Base constructor
    public Invoice(Integer invoiceId,
                   String empSSN,
                   String licenseId,
                   Double amount,
                   String purpose,
                   String date) {
        this.invoiceId = invoiceId;
        this.empSSN = empSSN;
        this.licenseId = licenseId;
        this.amount = amount;
        this.purpose = purpose;
        this.date = date;
    }

    // Constructor with display fields
    public Invoice(Integer invoiceId,
                   String empSSN,
                   String licenseId,
                   Double amount,
                   String purpose,
                   String date,
                   String customerFirstName,
                   String customerLastName,
                   String employeeFirstName,
                   String employeeLastName) {
        this(invoiceId, empSSN, licenseId, amount, purpose, date);
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.employeeFirstName = employeeFirstName;
        this.employeeLastName = employeeLastName;
    }

    public Integer getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getEmpSSN() {
        return empSSN;
    }

    public void setEmpSSN(String empSSN) {
        this.empSSN = empSSN;
    }

    public String getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(String licenseId) {
        this.licenseId = licenseId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public String getEmployeeFirstName() {
        return employeeFirstName;
    }

    public void setEmployeeFirstName(String employeeFirstName) {
        this.employeeFirstName = employeeFirstName;
    }

    public String getEmployeeLastName() {
        return employeeLastName;
    }

    public void setEmployeeLastName(String employeeLastName) {
        this.employeeLastName = employeeLastName;
    }
}

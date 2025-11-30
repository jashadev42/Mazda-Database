package model;

public class PartSale {

    private Integer partSaleId;
    private String licenseId;
    private String partId;
    private String empSSN;
    private Integer count;
    private Double amount;
    private String saleDate; // yyyy-MM-dd

    // Extra display fields
    private String customerFirstName;
    private String customerLastName;
    private String employeeFirstName;
    private String employeeLastName;
    private String partName;

    public PartSale() {}

    // Base constructor
    public PartSale(Integer partSaleId,
                    String licenseId,
                    String partId,
                    String empSSN,
                    Integer count,
                    Double amount,
                    String saleDate) {
        this.partSaleId = partSaleId;
        this.licenseId = licenseId;
        this.partId = partId;
        this.empSSN = empSSN;
        this.count = count;
        this.amount = amount;
        this.saleDate = saleDate;
    }

    // With display fields
    public PartSale(Integer partSaleId,
                    String licenseId,
                    String partId,
                    String empSSN,
                    Integer count,
                    Double amount,
                    String saleDate,
                    String customerFirstName,
                    String customerLastName,
                    String employeeFirstName,
                    String employeeLastName,
                    String partName) {
        this(partSaleId, licenseId, partId, empSSN, count, amount, saleDate);
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.employeeFirstName = employeeFirstName;
        this.employeeLastName = employeeLastName;
        this.partName = partName;
    }

    public Integer getPartSaleId() {
        return partSaleId;
    }

    public void setPartSaleId(Integer partSaleId) {
        this.partSaleId = partSaleId;
    }

    public String getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(String licenseId) {
        this.licenseId = licenseId;
    }

    public String getPartId() {
        return partId;
    }

    public void setPartId(String partId) {
        this.partId = partId;
    }

    public String getEmpSSN() {
        return empSSN;
    }

    public void setEmpSSN(String empSSN) {
        this.empSSN = empSSN;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(String saleDate) {
        this.saleDate = saleDate;
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

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }
}

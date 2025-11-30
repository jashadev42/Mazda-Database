package model;

public class VehicleSale {

    private Integer saleId;
    private String licenseId;
    private String vin;
    private String empSSN;
    private String terms;
    private Double finalPrice;
    // Stored as "yyyy-MM-dd"
    private String saleDate;
    private String saleType;

    // Extra display fields from joins
    private String customerFirstName;
    private String customerLastName;
    private String employeeFirstName;
    private String employeeLastName;
    private String vehicleDescription; // e.g. "2021 Mazda CX-5"

    public VehicleSale() {}

    // Constructor without extra display fields (for inserts/updates)
    public VehicleSale(Integer saleId,
                       String licenseId,
                       String vin,
                       String empSSN,
                       String terms,
                       Double finalPrice,
                       String saleDate,
                       String saleType) {
        this.saleId = saleId;
        this.licenseId = licenseId;
        this.vin = vin;
        this.empSSN = empSSN;
        this.terms = terms;
        this.finalPrice = finalPrice;
        this.saleDate = saleDate;
        this.saleType = saleType;
    }

    // Constructor with display fields
    public VehicleSale(Integer saleId,
                       String licenseId,
                       String vin,
                       String empSSN,
                       String terms,
                       Double finalPrice,
                       String saleDate,
                       String saleType,
                       String customerFirstName,
                       String customerLastName,
                       String employeeFirstName,
                       String employeeLastName,
                       String vehicleDescription) {
        this(saleId, licenseId, vin, empSSN, terms, finalPrice, saleDate, saleType);
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.employeeFirstName = employeeFirstName;
        this.employeeLastName = employeeLastName;
        this.vehicleDescription = vehicleDescription;
    }

    public Integer getSaleId() {
        return saleId;
    }

    public void setSaleId(Integer saleId) {
        this.saleId = saleId;
    }

    public String getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(String licenseId) {
        this.licenseId = licenseId;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getEmpSSSN() {
        return empSSN;
    }

    public String getEmpSSN() {
        return empSSN;
    }

    public void setEmpSSN(String empSSN) {
        this.empSSN = empSSN;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public Double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(Double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(String saleDate) {
        this.saleDate = saleDate;
    }

    public String getSaleType() {
        return saleType;
    }

    public void setSaleType(String saleType) {
        this.saleType = saleType;
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

    public String getVehicleDescription() {
        return vehicleDescription;
    }

    public void setVehicleDescription(String vehicleDescription) {
        this.vehicleDescription = vehicleDescription;
    }
}

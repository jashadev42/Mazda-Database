package model;

public class ServiceRequest {

    private int requestId;       // corresponds to Request_ID (PK)
    private String licenseId;   
    private String vin;        
    private Double cost;         // Cost (Nullable)
    private String description;  
    private String status;       
    private String startDate;    // YYYY-MM-DD
    private String finishDate;   // YYYY-MM-DD or null

    public ServiceRequest() {}

    public ServiceRequest(int requestId,
                          String licenseId,
                          String vin,
                          Double cost,
                          String description,
                          String status,
                          String startDate,
                          String finishDate) {
        this.requestId = requestId;
        this.licenseId = licenseId;
        this.vin = vin;
        this.cost = cost;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
        this.finishDate = finishDate;
    }

    // For inserts where Request_ID is auto-generated
    public ServiceRequest(String licenseId,
                          String vin,
                          Double cost,
                          String description,
                          String status,
                          String startDate,
                          String finishDate) {
        this(0, licenseId, vin, cost, description, status, startDate, finishDate);
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
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

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }
}

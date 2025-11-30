package model;

public class CustomerVehicleOwnership {

    private String vin;
    private String licenseId;

    // Extra fields for display
    private String ownerFirstName;
    private String ownerLastName;
    private String vehicleDescription;

    public CustomerVehicleOwnership() {}

    public CustomerVehicleOwnership(String vin, String licenseId) {
        this.vin = vin;
        this.licenseId = licenseId;
    }

    public CustomerVehicleOwnership(String vin,
                                    String licenseId,
                                    String ownerFirstName,
                                    String ownerLastName,
                                    String vehicleDescription) {
        this.vin = vin;
        this.licenseId = licenseId;
        this.ownerFirstName = ownerFirstName;
        this.ownerLastName = ownerLastName;
        this.vehicleDescription = vehicleDescription;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(String licenseId) {
        this.licenseId = licenseId;
    }

    public String getOwnerFirstName() {
        return ownerFirstName;
    }

    public void setOwnerFirstName(String ownerFirstName) {
        this.ownerFirstName = ownerFirstName;
    }

    public String getOwnerLastName() {
        return ownerLastName;
    }

    public void setOwnerLastName(String ownerLastName) {
        this.ownerLastName = ownerLastName;
    }

    public String getVehicleDescription() {
        return vehicleDescription;
    }

    public void setVehicleDescription(String vehicleDescription) {
        this.vehicleDescription = vehicleDescription;
    }
}

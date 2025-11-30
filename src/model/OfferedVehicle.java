package model;

public class OfferedVehicle {

    private String vin;
    private String make;
    private String model;
    private int year;
    private double price;
    private String status;

    public OfferedVehicle() {}

    public OfferedVehicle(String vin, String make, String model, int year,
                          double price, String status) {
        this.vin = vin;
        this.make = make;
        this.model = model;
        this.year = year;
        this.price = price;
        this.status = status;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

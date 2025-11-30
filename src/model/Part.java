package model;

public class Part {

    private String partId;
    private String partName;
    private int quantity;
    private double unitPrice;

    public Part() {}

    public Part(String partId, String partName, int quantity, double unitPrice) {
        this.partId = partId;
        this.partName = partName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getPartId() {
        return partId;
    }

    public void setPartId(String partId) {
        this.partId = partId;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
}

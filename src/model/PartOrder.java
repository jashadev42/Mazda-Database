package model;

public class PartOrder {

    private Integer orderId;
    private String empSSN;
    private String partId;
    private Integer count;
    private String orderDate; // yyyy-MM-dd

    // Extra display fields
    private String employeeFirstName;
    private String employeeLastName;
    private String partName;

    public PartOrder() {}

    // Base constructor
    public PartOrder(Integer orderId,
                     String empSSN,
                     String partId,
                     Integer count,
                     String orderDate) {
        this.orderId = orderId;
        this.empSSN = empSSN;
        this.partId = partId;
        this.count = count;
        this.orderDate = orderDate;
    }

    // Constructor with display fields
    public PartOrder(Integer orderId,
                     String empSSN,
                     String partId,
                     Integer count,
                     String orderDate,
                     String employeeFirstName,
                     String employeeLastName,
                     String partName) {
        this(orderId, empSSN, partId, count, orderDate);
        this.employeeFirstName = employeeFirstName;
        this.employeeLastName = employeeLastName;
        this.partName = partName;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getEmpSSN() {
        return empSSN;
    }

    public void setEmpSSN(String empSSN) {
        this.empSSN = empSSN;
    }

    public String getPartId() {
        return partId;
    }

    public void setPartId(String partId) {
        this.partId = partId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
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

package dao;

import db.DBConnection;
import model.ServiceRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceRequestDAO {

    // INSERT
    public void addServiceRequest(ServiceRequest r) throws SQLException {
        String sql = "INSERT INTO Service_Request " +
                "(License_ID, VIN, Cost, Description, Status, Start_Date, Finish_Date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, r.getLicenseId());
            stmt.setString(2, r.getVin());

            if (r.getCost() != null) {
                stmt.setDouble(3, r.getCost());
            } else {
                stmt.setNull(3, Types.DECIMAL);
            }

            stmt.setString(4, r.getDescription());
            stmt.setString(5, r.getStatus());

            if (r.getStartDate() != null && !r.getStartDate().isEmpty()) {
                stmt.setDate(6, Date.valueOf(r.getStartDate()));
            } else {
                stmt.setNull(6, Types.DATE);
            }

            if (r.getFinishDate() != null && !r.getFinishDate().isEmpty()) {
                stmt.setDate(7, Date.valueOf(r.getFinishDate()));
            } else {
                stmt.setNull(7, Types.DATE);
            }

            stmt.executeUpdate();
        }
    }

    // SELECT all
    public List<ServiceRequest> getAllServiceRequests() throws SQLException {
        List<ServiceRequest> list = new ArrayList<>();

        String sql = "SELECT Request_ID, License_ID, VIN, Cost, Description, " +
                     "Status, Start_Date, Finish_Date " +
                     "FROM Service_Request";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int requestId      = rs.getInt("Request_ID");
                String licenseId   = rs.getString("License_ID");
                String vin         = rs.getString("VIN");
                Double cost        = rs.getObject("Cost") == null ? null : rs.getDouble("Cost");
                String description = rs.getString("Description");
                String status      = rs.getString("Status");

                Date startDateSql  = rs.getDate("Start_Date");
                Date finishDateSql = rs.getDate("Finish_Date");

                String startDate  = (startDateSql  != null) ? startDateSql.toString()  : null;
                String finishDate = (finishDateSql != null) ? finishDateSql.toString() : null;

                ServiceRequest r = new ServiceRequest(
                        requestId,
                        licenseId,
                        vin,
                        cost,
                        description,
                        status,
                        startDate,
                        finishDate
                );
                list.add(r);
            }
        }

        return list;
    }

    // UPDATE full record by Request_ID
    public void updateServiceRequest(ServiceRequest r) throws SQLException {
        String sql = "UPDATE Service_Request SET " +
                "License_ID = ?, VIN = ?, Cost = ?, Description = ?, " +
                "Status = ?, Start_Date = ?, Finish_Date = ? " +
                "WHERE Request_ID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, r.getLicenseId());
            stmt.setString(2, r.getVin());

            if (r.getCost() != null) {
                stmt.setDouble(3, r.getCost());
            } else {
                stmt.setNull(3, Types.DECIMAL);
            }

            stmt.setString(4, r.getDescription());
            stmt.setString(5, r.getStatus());

            if (r.getStartDate() != null && !r.getStartDate().isEmpty()) {
                stmt.setDate(6, Date.valueOf(r.getStartDate()));
            } else {
                stmt.setNull(6, Types.DATE);
            }

            if (r.getFinishDate() != null && !r.getFinishDate().isEmpty()) {
                stmt.setDate(7, Date.valueOf(r.getFinishDate()));
            } else {
                stmt.setNull(7, Types.DATE);
            }

            stmt.setInt(8, r.getRequestId());

            stmt.executeUpdate();
        }
    }

    // MARK COMPLETED: Status, Cost, Finish_Date
    public void markCompleted(int requestId, Double cost, String finishDate) throws SQLException {
        String sql = "UPDATE Service_Request SET Status = ?, Cost = ?, Finish_Date = ? " +
                     "WHERE Request_ID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "Completed"); // or whatever status you like

            if (cost != null) {
                stmt.setDouble(2, cost);
            } else {
                stmt.setNull(2, Types.DECIMAL);
            }

            if (finishDate != null && !finishDate.isEmpty()) {
                stmt.setDate(3, Date.valueOf(finishDate));
            } else {
                stmt.setNull(3, Types.DATE);
            }

            stmt.setInt(4, requestId);

            stmt.executeUpdate();
        }
    }
}

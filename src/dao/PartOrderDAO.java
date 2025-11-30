package dao;

import db.DBConnection;
import model.PartOrder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartOrderDAO {

    // INSERT
    public void addOrder(PartOrder order) throws SQLException {
        String sql = "INSERT INTO Part_Orders (Emp_SSN, Part_ID, Count, Order_Date) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, order.getEmpSSN());
            stmt.setString(2, order.getPartId());

            if (order.getCount() != null) {
                stmt.setInt(3, order.getCount());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            if (order.getOrderDate() != null && !order.getOrderDate().isEmpty()) {
                stmt.setDate(4, Date.valueOf(order.getOrderDate())); // yyyy-MM-dd
            } else {
                stmt.setNull(4, Types.DATE);
            }

            stmt.executeUpdate();
        }
    }

    // SELECT all with joined info
    public List<PartOrder> getAllOrders() throws SQLException {
        List<PartOrder> list = new ArrayList<>();

        String sql =
            "SELECT po.Order_ID, po.Emp_SSN, po.Part_ID, po.Count, po.Order_Date, " +
            "       e.First_Name AS Emp_First_Name, e.Last_Name AS Emp_Last_Name, " +
            "       p.Part_Name " +
            "FROM Part_Orders po " +
            "JOIN Employee e        ON po.Emp_SSN = e.Emp_SSN " +
            "JOIN Parts_Inventory p ON po.Part_ID = p.Part_ID";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int orderId          = rs.getInt("Order_ID");
                String empSSN        = rs.getString("Emp_SSN");
                String partId        = rs.getString("Part_ID");
                Integer count        = rs.getObject("Count") == null
                        ? null
                        : rs.getInt("Count");
                Date date            = rs.getDate("Order_Date");
                String orderDateStr  = (date != null) ? date.toString() : null;
                String empFirstName  = rs.getString("Emp_First_Name");
                String empLastName   = rs.getString("Emp_Last_Name");
                String partName      = rs.getString("Part_Name");

                PartOrder order = new PartOrder(
                        orderId,
                        empSSN,
                        partId,
                        count,
                        orderDateStr,
                        empFirstName,
                        empLastName,
                        partName
                );

                list.add(order);
            }
        }

        return list;
    }

    // UPDATE
    public void updateOrder(PartOrder order) throws SQLException {
        String sql =
            "UPDATE Part_Orders SET Emp_SSN = ?, Part_ID = ?, Count = ?, Order_Date = ? " +
            "WHERE Order_ID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, order.getEmpSSN());
            stmt.setString(2, order.getPartId());

            if (order.getCount() != null) {
                stmt.setInt(3, order.getCount());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            if (order.getOrderDate() != null && !order.getOrderDate().isEmpty()) {
                stmt.setDate(4, Date.valueOf(order.getOrderDate()));
            } else {
                stmt.setNull(4, Types.DATE);
            }

            stmt.setInt(5, order.getOrderId());

            stmt.executeUpdate();
        }
    }

    // DELETE
    public void deleteOrder(int orderId) throws SQLException {
        String sql = "DELETE FROM Part_Orders WHERE Order_ID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            stmt.executeUpdate();
        }
    }

    // RECEIVE order: add Count to Parts_Inventory.Quantity
    public void receiveOrder(int orderId) throws SQLException {
        String selectSql = "SELECT Part_ID, Count FROM Part_Orders WHERE Order_ID = ?";
        String updateSql = "UPDATE Parts_Inventory SET Quantity = Quantity + ? WHERE Part_ID = ?";

        Connection conn = null;
        PreparedStatement selectStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            selectStmt = conn.prepareStatement(selectSql);
            selectStmt.setInt(1, orderId);
            rs = selectStmt.executeQuery();

            if (!rs.next()) {
                throw new SQLException("Order_ID " + orderId + " not found.");
            }

            String partId = rs.getString("Part_ID");
            int count = rs.getInt("Count");

            updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setInt(1, count);
            updateStmt.setString(2, partId);
            updateStmt.executeUpdate();

            conn.commit();
        } catch (SQLException ex) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw ex;
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException ignored) {}
            if (selectStmt != null) try { selectStmt.close(); } catch (SQLException ignored) {}
            if (updateStmt != null) try { updateStmt.close(); } catch (SQLException ignored) {}
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
        }
    }
}

package com.smarthomes.csp584.servlets;

import com.smarthomes.csp584.models.Transaction;
import com.smarthomes.csp584.utils.MySQLDataStoreUtilities;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(name = "TransactionServlet", value = "/transactions")
public class TransactionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
        JSONObject requestBodyJson = new JSONObject(requestBody);

        int orderId = requestBodyJson.getInt("orderId");
        int userId = requestBodyJson.getInt("userId");
        String customerName = requestBodyJson.getString("customerName");
        String shippingAddress = requestBodyJson.getString("shippingAddress");
        String creditCardNumber = requestBodyJson.getString("creditCardNumber");
        String transactionDate = requestBodyJson.getString("transactionDate");
        double transactionAmount = requestBodyJson.getDouble("transactionAmount");
        String paymentStatus = requestBodyJson.getString("paymentStatus");
        int productId = requestBodyJson.getInt("productId");
        String category = requestBodyJson.getString("category");
        int quantity = requestBodyJson.getInt("quantity");
        double shippingCost = requestBodyJson.optDouble("shippingCost", 0.0);
        double discount = requestBodyJson.optDouble("discount", 0.0);
        String storeAddress = requestBodyJson.getString("storeAddress");

        Connection conn = null;
        try {
            conn = MySQLDataStoreUtilities.getConnection();

            String insertQuery = "INSERT INTO transactions (orderId, userId, customerName, shippingAddress, creditCardNumber, transactionDate, transactionAmount, paymentStatus, productId, category, quantity, shippingCost, discount, storeAddress) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(insertQuery);
            pst.setInt(1, orderId);
            pst.setInt(2, userId);
            pst.setString(3, customerName);
            pst.setString(4, shippingAddress);
            pst.setString(5, creditCardNumber);
            pst.setString(6, transactionDate);
            pst.setDouble(7, transactionAmount);
            pst.setString(8, paymentStatus);
            pst.setInt(9, productId);
            pst.setString(10, category);
            pst.setInt(11, quantity);
            pst.setDouble(12, shippingCost);
            pst.setDouble(13, discount);
            pst.setString(14, storeAddress);

            int result = pst.executeUpdate();

            if (result > 0) {
                if (paymentStatus.equalsIgnoreCase("completed")) {
                    // Query to reduce the availableItems in the products table
                    String updateProductQuery = "UPDATE products SET availableItems = availableItems - ? WHERE id = ?";
                    PreparedStatement updatePst = conn.prepareStatement(updateProductQuery);
                    updatePst.setInt(1, quantity);
                    updatePst.setInt(2, productId);
                    int updateResult = updatePst.executeUpdate();

                    if (updateResult > 0) {
                        out.println(new JSONObject().put("status", "success").put("message", "Transaction recorded and product stock updated successfully"));
                    } else {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        out.println(new JSONObject().put("status", "error").put("message", "Failed to update product stock"));
                    }
                    updatePst.close();
                } else {
                    out.println(new JSONObject().put("status", "success").put("message", "Transaction recorded successfully"));
                }
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.println(new JSONObject().put("status", "error").put("message", "Failed to record transaction"));
            }
            pst.close();
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(new JSONObject().put("status", "error").put("message", e.getMessage()));
            e.printStackTrace();
        } finally {
            MySQLDataStoreUtilities.closeConnection(conn);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String userIdParam = request.getParameter("userId");
        Connection conn = null;

        try {
            conn = MySQLDataStoreUtilities.getConnection();
            String query = "SELECT * FROM transactions WHERE userId = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, Integer.parseInt(userIdParam));
            ResultSet rs = pst.executeQuery();

            JSONArray transactionsArray = new JSONArray();
            while (rs.next()) {
                JSONObject transactionJson = new JSONObject();
                transactionJson.put("transactionId", rs.getInt("transactionId"));
                transactionJson.put("orderId", rs.getInt("orderId"));
                transactionJson.put("userId", rs.getInt("userId"));
                transactionJson.put("customerName", rs.getString("customerName"));
                transactionJson.put("shippingAddress", rs.getString("shippingAddress"));
                transactionJson.put("creditCardNumber", rs.getString("creditCardNumber"));
                transactionJson.put("transactionDate", rs.getString("transactionDate"));
                transactionJson.put("transactionAmount", rs.getDouble("transactionAmount"));
                transactionJson.put("paymentStatus", rs.getString("paymentStatus"));
                transactionJson.put("productId", rs.getInt("productId"));
                transactionJson.put("category", rs.getString("category"));
                transactionJson.put("quantity", rs.getInt("quantity"));
                transactionJson.put("shippingCost", rs.getDouble("shippingCost"));
                transactionJson.put("discount", rs.getDouble("discount"));
                transactionJson.put("totalSales", rs.getDouble("totalSales"));
                transactionJson.put("storeAddress", rs.getString("storeAddress"));

                transactionsArray.put(transactionJson);
            }

            out.println(new JSONObject().put("status", "success").put("transactions", transactionsArray));

            rs.close();
            pst.close();
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(new JSONObject().put("status", "error").put("message", e.getMessage()));
            e.printStackTrace();
        } finally {
            MySQLDataStoreUtilities.closeConnection(conn);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
        JSONObject requestBodyJson = new JSONObject(requestBody);

        int transactionId = requestBodyJson.getInt("transactionId");
        String newStatus = requestBodyJson.getString("paymentStatus");

        Connection conn = null;
        try {
            conn = MySQLDataStoreUtilities.getConnection();

            String query = "UPDATE transactions SET paymentStatus = ? WHERE transactionId = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, newStatus);
            pst.setInt(2, transactionId);

            int result = pst.executeUpdate();

            if (result > 0) {
                out.println(new JSONObject().put("status", "success").put("message", "Transaction status updated successfully"));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.println(new JSONObject().put("status", "error").put("message", "Failed to update transaction status"));
            }
            pst.close();
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(new JSONObject().put("status", "error").put("message", e.getMessage()));
            e.printStackTrace();
        } finally {
            MySQLDataStoreUtilities.closeConnection(conn);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String transactionIdParam = request.getParameter("transactionId");

        if (transactionIdParam == null || transactionIdParam.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println(new JSONObject().put("status", "error").put("message", "transactionId is required"));
            return;
        }

        int transactionId = Integer.parseInt(transactionIdParam);

        Connection conn = null;
        try {
            conn = MySQLDataStoreUtilities.getConnection();

            String query = "DELETE FROM transactions WHERE transactionId = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, transactionId);

            int result = pst.executeUpdate();

            if (result > 0) {
                out.println(new JSONObject().put("status", "success").put("message", "Transaction deleted successfully"));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.println(new JSONObject().put("status", "error").put("message", "Transaction not found"));
            }

            pst.close();
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(new JSONObject().put("status", "error").put("message", e.getMessage()));
            e.printStackTrace();
        } finally {
            MySQLDataStoreUtilities.closeConnection(conn);
        }
    }
}

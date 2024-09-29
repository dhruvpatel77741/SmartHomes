package com.smarthomes.csp584.servlets;

import com.smarthomes.csp584.utils.MySQLDataStoreUtilities;
import org.json.JSONArray;
import org.json.JSONObject;

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
import java.util.UUID;

@WebServlet(name = "ordersServlet", value = "/orders")
public class OrdersServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
        JSONObject requestBodyJson = new JSONObject(requestBody);

        int userId = requestBodyJson.getInt("userId");
        int productId = requestBodyJson.getInt("productId");
        int quantity = requestBodyJson.getInt("quantity");
        double price = requestBodyJson.getDouble("price");
        double shippingCost = requestBodyJson.optDouble("shippingCost", 0.0);
        String orderDate = requestBodyJson.getString("orderDate");
        String shipDate = requestBodyJson.getString("shipDate");
        String deliveryMethod = requestBodyJson.getString("deliveryMethod");
        int storeId = requestBodyJson.getInt("storeId");
        String shippingAddress = requestBodyJson.getString("shippingAddress");
        String status = requestBodyJson.getString("status");
        double discount = requestBodyJson.optDouble("discount", 0.0);

        Connection conn = null;
        try {
            conn = MySQLDataStoreUtilities.getConnection();

            String query = "INSERT INTO orders (userId, productId, quantity, price, shippingCost, orderDate, shipDate, deliveryMethod, storeId, shippingAddress, status, discount) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            pst.setInt(1, userId);
            pst.setInt(2, productId);
            pst.setInt(3, quantity);
            pst.setDouble(4, price);
            pst.setDouble(5, shippingCost);
            pst.setString(6, orderDate);
            pst.setString(7, shipDate);
            pst.setString(8, deliveryMethod);
            pst.setInt(9, storeId);
            pst.setString(10, shippingAddress);
            pst.setString(11, status);
            pst.setDouble(12, discount);

            int result = pst.executeUpdate();

            if (result > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    int orderId = rs.getInt(1);

                    query = "SELECT * FROM orders WHERE orderId = ?";
                    pst = conn.prepareStatement(query);
                    pst.setInt(1, orderId);

                    rs = pst.executeQuery();

                    if (rs.next()) {
                        JSONObject orderJson = new JSONObject();
                        orderJson.put("orderId", rs.getInt("orderId"));
                        orderJson.put("userId", rs.getInt("userId"));
                        orderJson.put("productId", rs.getInt("productId"));
                        orderJson.put("quantity", rs.getInt("quantity"));
                        orderJson.put("price", rs.getDouble("price"));
                        orderJson.put("shippingCost", rs.getDouble("shippingCost"));
                        orderJson.put("total", rs.getDouble("total"));
                        orderJson.put("orderDate", rs.getString("orderDate"));
                        orderJson.put("shipDate", rs.getString("shipDate"));
                        orderJson.put("deliveryMethod", rs.getString("deliveryMethod"));
                        orderJson.put("storeId", rs.getInt("storeId"));
                        orderJson.put("shippingAddress", rs.getString("shippingAddress"));
                        orderJson.put("status", rs.getString("status"));
                        orderJson.put("discount", rs.getDouble("discount"));

                        out.println(new JSONObject().put("status", "success").put("order", orderJson));
                    }
                }
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.println(new JSONObject().put("status", "error").put("message", "Failed to place order"));
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String userIdParam = request.getParameter("userId");
        Connection conn = null;

        try {
            conn = MySQLDataStoreUtilities.getConnection();
            String query;
            PreparedStatement pst;
            if (userIdParam != null) {
                int userId = Integer.parseInt(userIdParam);
                query = "SELECT * FROM orders WHERE userId = ?";
                pst = conn.prepareStatement(query);
                pst.setInt(1, userId);
            } else {
                query = "SELECT * FROM orders";
                pst = conn.prepareStatement(query);
            }

            ResultSet rs = pst.executeQuery();
            JSONArray ordersArray = new JSONArray();

            while (rs.next()) {
                JSONObject orderJson = new JSONObject();
                orderJson.put("orderId", rs.getString("orderId"));
                orderJson.put("userId", rs.getInt("userId"));
                orderJson.put("productId", rs.getInt("productId"));
                orderJson.put("quantity", rs.getInt("quantity"));
                orderJson.put("price", rs.getDouble("price"));
                orderJson.put("shippingCost", rs.getDouble("shippingCost"));
                orderJson.put("total", rs.getDouble("total"));
                orderJson.put("orderDate", rs.getString("orderDate"));
                orderJson.put("shipDate", rs.getString("shipDate"));
                orderJson.put("deliveryMethod", rs.getString("deliveryMethod"));
                orderJson.put("storeId", rs.getInt("storeId"));
                orderJson.put("shippingAddress", rs.getString("shippingAddress"));
                orderJson.put("status", rs.getString("status"));
                orderJson.put("discount", rs.getDouble("discount"));
                ordersArray.put(orderJson);
            }

            out.println(new JSONObject().put("status", "success").put("orders", ordersArray));
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
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
        JSONObject requestBodyJson = new JSONObject(requestBody);

        String orderId = requestBodyJson.getString("orderId");
        String newStatus = requestBodyJson.getString("status");

        Connection conn = null;
        try {
            conn = MySQLDataStoreUtilities.getConnection();
            String query = "UPDATE orders SET status = ? WHERE orderId = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, newStatus);
            pst.setString(2, orderId);

            int result = pst.executeUpdate();

            if (result > 0) {
                out.println(new JSONObject().put("status", "success").put("message", "Order status updated successfully"));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.println(new JSONObject().put("status", "error").put("message", "Order not found"));
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
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String orderId = request.getParameter("orderId");

        Connection conn = null;
        try {
            conn = MySQLDataStoreUtilities.getConnection();
            String query = "DELETE FROM orders WHERE orderId = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, orderId);

            int result = pst.executeUpdate();

            if (result > 0) {
                out.println(new JSONObject().put("status", "success").put("message", "Order deleted successfully"));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.println(new JSONObject().put("status", "error").put("message", "Order not found"));
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

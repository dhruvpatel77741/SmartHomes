package com.smarthomes.csp584.servlets;

import com.smarthomes.csp584.models.Cart;
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

@WebServlet(name = "CartManagementServlet", urlPatterns = {"/cart"})
public class CartManagementServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
        JSONObject requestBodyJson = new JSONObject(requestBody);

        String action = requestBodyJson.getString("action");
        int userId = requestBodyJson.getInt("userId");

        try {
            switch (action) {
                case "addToCart":
                    handleAddToCart(requestBodyJson, userId, out);
                    break;
                case "removeFromCart":
                    handleRemoveFromCart(requestBodyJson, userId, out);
                    break;
                case "clearCart":
                    handleClearCart(userId, out);
                    break;
                default:
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println(new JSONObject().put("status", "error").put("message", "Invalid action"));
                    break;
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(new JSONObject().put("status", "error").put("message", e.getMessage()));
            e.printStackTrace();
        }
    }

    private void handleAddToCart(JSONObject jsonBody, int userId, PrintWriter out) throws SQLException {
        String productId = jsonBody.optString("productId", null);
        String productName = jsonBody.optString("productName", null);
        if (productId == null || productName == null) {
            out.println(new JSONObject().put("status", "error").put("message", "productId and productName are required"));
            return;
        }

        double productPrice = jsonBody.optDouble("productPrice", 0.0);
        int quantity = jsonBody.optInt("quantity", 1);
        boolean warrantyAdded = jsonBody.optBoolean("warrantyAdded", false);
        double warrantyPrice = warrantyAdded ? jsonBody.optDouble("warrantyPrice", 0.0) : 0.0;

        JSONArray accessoriesArray = jsonBody.optJSONArray("accessories");
        double accessoriesPrice = 0.0;
        for (int i = 0; i < accessoriesArray.length(); i++) {
            JSONObject accessory = accessoriesArray.optJSONObject(i);
            accessoriesPrice += accessory != null ? accessory.optDouble("price", 0.0) : 0.0;
        }

        double totalPrice = (productPrice * quantity) + warrantyPrice + accessoriesPrice;

        Connection conn = null;
        try {
            conn = MySQLDataStoreUtilities.getConnection();

            boolean productExists = false;
            String selectQuery = "SELECT * FROM cart WHERE userId = ? AND productId = ?";
            PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
            selectStmt.setInt(1, userId);
            selectStmt.setString(2, productId);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                productExists = true;
            }
            rs.close();
            selectStmt.close();

            if (productExists) {
                String updateQuery = "UPDATE cart SET quantity = quantity + ?, totalPrice = (productPrice * quantity) + warrantyPrice + accessoriesPrice WHERE userId = ? AND productId = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setInt(1, quantity);
                updateStmt.setInt(2, userId);
                updateStmt.setString(3, productId);
                updateStmt.executeUpdate();
                updateStmt.close();
            } else {
                String insertQuery = "INSERT INTO cart (userId, productId, productName, productPrice, quantity, warrantyAdded, warrantyPrice, accessoriesPrice, totalPrice) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setInt(1, userId);
                insertStmt.setString(2, productId);
                insertStmt.setString(3, productName);
                insertStmt.setDouble(4, productPrice);
                insertStmt.setInt(5, quantity);
                insertStmt.setBoolean(6, warrantyAdded);
                insertStmt.setDouble(7, warrantyPrice);
                insertStmt.setDouble(8, accessoriesPrice);
                insertStmt.setDouble(9, totalPrice);
                insertStmt.executeUpdate();
                insertStmt.close();
            }

            out.println(new JSONObject().put("status", "success").put("message", "Item added to cart"));

        } finally {
            MySQLDataStoreUtilities.closeConnection(conn);
        }
    }

    private void handleRemoveFromCart(JSONObject jsonBody, int userId, PrintWriter out) throws SQLException {
        String productId = jsonBody.getString("productId");

        Connection conn = null;
        try {
            conn = MySQLDataStoreUtilities.getConnection();
            String query = "DELETE FROM cart WHERE userId = ? AND productId = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setString(2, productId);
            stmt.executeUpdate();
            stmt.close();

            out.println(new JSONObject().put("status", "success").put("message", "Item removed from cart"));
        } finally {
            MySQLDataStoreUtilities.closeConnection(conn);
        }
    }

    private void handleClearCart(int userId, PrintWriter out) throws SQLException {
        Connection conn = null;
        try {
            conn = MySQLDataStoreUtilities.getConnection();
            String query = "DELETE FROM cart WHERE userId = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            stmt.close();

            out.println(new JSONObject().put("status", "success").put("message", "Cart cleared"));
        } finally {
            MySQLDataStoreUtilities.closeConnection(conn);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        int userId = Integer.parseInt(request.getParameter("userId"));

        Connection conn = null;
        try {
            conn = MySQLDataStoreUtilities.getConnection();
            String query = "SELECT * FROM cart WHERE userId = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            JSONArray userCart = new JSONArray();
            while (rs.next()) {
                JSONObject item = new JSONObject();
                item.put("userId", rs.getInt("userId"));
                item.put("productId", rs.getString("productId"));
                item.put("productName", rs.getString("productName"));
                item.put("productPrice", rs.getDouble("productPrice"));
                item.put("quantity", rs.getInt("quantity"));
                item.put("warrantyAdded", rs.getBoolean("warrantyAdded"));
                item.put("warrantyPrice", rs.getDouble("warrantyPrice"));
                item.put("accessoriesPrice", rs.getDouble("accessoriesPrice"));
                item.put("totalPrice", rs.getDouble("totalPrice"));

                userCart.put(item);
            }

            out.println(userCart.toString());

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(new JSONObject().put("status", "error").put("message", e.getMessage()));
            e.printStackTrace();
        } finally {
            MySQLDataStoreUtilities.closeConnection(conn);
        }
    }
}

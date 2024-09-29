package com.smarthomes.csp584.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.json.JSONObject;
import com.smarthomes.csp584.utils.MySQLDataStoreUtilities;

@WebServlet(name = "updateUserServlet", value = "/updateUser")
public class UpdateUserServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String requestBody = request.getReader().lines().reduce("", String::concat);
        JSONObject requestBodyJson = new JSONObject(requestBody);

        int userId = requestBodyJson.getInt("id");
        String phone = requestBodyJson.optString("phone", null);
        JSONObject address = requestBodyJson.optJSONObject("address");
        JSONObject creditCard = requestBodyJson.optJSONObject("creditCard");

        if (updateUserInDatabase(userId, phone, address, creditCard)) {
            out.println(new JSONObject().put("status", "success").put("message", "User updated successfully"));
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.println(new JSONObject().put("status", "error").put("message", "User not found or update failed"));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String userIdParam = request.getParameter("id");
        if (userIdParam == null || userIdParam.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println(new JSONObject().put("status", "error").put("message", "User ID is required"));
            return;
        }

        int userId = Integer.parseInt(userIdParam);
        if (deleteUserFromDatabase(userId)) {
            out.println(new JSONObject().put("status", "success").put("message", "User deleted successfully"));
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.println(new JSONObject().put("status", "error").put("message", "User not found or deletion failed"));
        }
    }

    private boolean updateUserInDatabase(int userId, String phone, JSONObject address, JSONObject creditCard) {
        try (Connection conn = MySQLDataStoreUtilities.getConnection();
             PreparedStatement pst = conn.prepareStatement("UPDATE Users SET phone = ?, addressLine1 = ?, addressLine2 = ?, city = ?, state = ?, zipCode = ?, creditCardNumber = ?, expiryDate = ?, cvv = ? WHERE id = ?")) {
            pst.setString(1, phone);
            setPreparedStatementFromJson(pst, address, 2, "addressLine1", "addressLine2", "city", "state", "zipCode");
            setPreparedStatementFromJson(pst, creditCard, 7, "creditCardNumber", "expiryDate", "cvv");
            pst.setInt(10, userId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean deleteUserFromDatabase(int userId) {
        try (Connection conn = MySQLDataStoreUtilities.getConnection();
             PreparedStatement pst = conn.prepareStatement("DELETE FROM Users WHERE id = ?")) {
            pst.setInt(1, userId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void setPreparedStatementFromJson(PreparedStatement pst, JSONObject json, int startIndex, String... fields) throws SQLException {
        for (int i = 0; i < fields.length; i++) {
            pst.setString(startIndex + i, json != null && json.has(fields[i]) ? json.getString(fields[i]) : null);
        }
    }
}

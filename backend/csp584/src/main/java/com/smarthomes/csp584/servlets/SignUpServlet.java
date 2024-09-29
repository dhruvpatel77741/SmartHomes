package com.smarthomes.csp584.servlets;

import com.smarthomes.csp584.utils.MySQLDataStoreUtilities;
import org.json.JSONObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(name = "signUpServlet", value = "/signup")
public class SignUpServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String requestBody = request.getReader().lines().reduce("", String::concat);
        JSONObject requestBodyJson = new JSONObject(requestBody);

        String name = requestBodyJson.getString("name");
        String username = requestBodyJson.getString("username");
        String password = requestBodyJson.getString("password");
        String userType = requestBodyJson.getString("userType");

        if (doesUserExist(username)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println(new JSONObject().put("status", "error").put("message", "Username already exists"));
        } else {
            boolean userCreated = createUserInDatabase(name, username, password, userType);
            if (userCreated) {
                out.println(new JSONObject().put("status", "success").put("message", "User registered successfully"));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.println(new JSONObject().put("status", "error").put("message", "Failed to register user"));
            }
        }
    }

    private boolean doesUserExist(String username) {
        try (Connection conn = MySQLDataStoreUtilities.getConnection();
             PreparedStatement pst = conn.prepareStatement("SELECT id FROM Users WHERE username = ?")) {
            pst.setString(1, username);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean createUserInDatabase(String name, String username, String password, String userType) {
        try (Connection conn = MySQLDataStoreUtilities.getConnection();
             PreparedStatement pst = conn.prepareStatement("INSERT INTO Users (name, username, password, userType) VALUES (?, ?, ?, ?)")) {
            pst.setString(1, name);
            pst.setString(2, username);
            pst.setString(3, password);
            pst.setString(4, userType);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

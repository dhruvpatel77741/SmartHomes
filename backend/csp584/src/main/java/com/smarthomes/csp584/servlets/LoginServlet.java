package com.smarthomes.csp584.servlets;

import com.smarthomes.csp584.models.User;
import com.smarthomes.csp584.utils.MySQLDataStoreUtilities;
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

@WebServlet(name = "loginServlet", value = "/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
        JSONObject requestBodyJson = new JSONObject(requestBody);

        String username = requestBodyJson.getString("username");
        String password = requestBodyJson.getString("password");
        String userType = requestBodyJson.getString("userType");

        User authenticatedUser = authenticateUser(username, password, userType);

        if (authenticatedUser != null) {
            response.setStatus(HttpServletResponse.SC_OK);
            JSONObject userJson = new JSONObject();
            userJson.put("id", authenticatedUser.getId());
            userJson.put("username", authenticatedUser.getUsername());
            userJson.put("name", authenticatedUser.getName());
            userJson.put("userType", authenticatedUser.getUserType());

            out.println(new JSONObject().put("status", "success").put("user", userJson));
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.println(new JSONObject().put("status", "error").put("message", "Invalid username, password, or userType"));
        }
    }

    private User authenticateUser(String username, String password, String userType) {
        Connection conn = null;

        try {
            conn = MySQLDataStoreUtilities.getConnection();
            String query = "SELECT id, username, name, userType FROM Users WHERE username = ? AND password = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String dbUserType = rs.getString("userType");
                if (dbUserType.equals(userType)) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            "",
                            rs.getString("name"),
                            rs.getString("userType")
                    );
                }
            }

            rs.close();
            pst.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            MySQLDataStoreUtilities.closeConnection(conn);
        }

        return null;
    }
}

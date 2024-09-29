package com.smarthomes.csp584.servlets;

import com.smarthomes.csp584.models.User;
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
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "userServlet", value = "/users")
public class UserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        List<User> userList = getUsersFromDatabase();
        JSONArray usersJsonArray = new JSONArray();
        for (User user : userList) {
            JSONObject userJson = new JSONObject();
            userJson.put("id", user.getId());
            userJson.put("username", user.getUsername());
            userJson.put("name", user.getName());
            userJson.put("userType", user.getUserType());
            usersJsonArray.put(userJson);
        }

        out.println(usersJsonArray.toString());
    }

    private List<User> getUsersFromDatabase() {
        List<User> users = new ArrayList<>();
        try (Connection conn = MySQLDataStoreUtilities.getConnection();
             PreparedStatement pst = conn.prepareStatement("SELECT id, username, name, userType FROM Users");
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                users.add(new User(rs.getInt("id"), rs.getString("username"), "", rs.getString("name"), rs.getString("userType")));
            }
        } catch (Exception e) {
            System.err.println("Error fetching users: " + e.getMessage());
        }
        return users;
    }
}

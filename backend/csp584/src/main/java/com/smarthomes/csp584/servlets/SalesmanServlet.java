package com.smarthomes.csp584.servlets;

import com.smarthomes.csp584.models.User;
import com.smarthomes.csp584.utils.MySQLDataStoreUtilities;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "salesmanServlet", value = "/salesman")
public class SalesmanServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            List<User> salesmen = getSalesmenFromDatabase();
            JSONArray salesmenJsonArray = new JSONArray();
            for (User salesman : salesmen) {
                JSONObject salesmanJson = new JSONObject();
                salesmanJson.put("id", salesman.getId());
                salesmanJson.put("username", salesman.getUsername());
                salesmanJson.put("name", salesman.getName());
                salesmanJson.put("userType", salesman.getUserType());
                salesmenJsonArray.put(salesmanJson);
            }
            out.println(salesmenJsonArray.toString());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(new JSONObject().put("error", "Error fetching salesmen data"));
            e.printStackTrace();
        }
    }

    private List<User> getSalesmenFromDatabase() throws Exception {
        List<User> salesmen = new ArrayList<>();
        try (Connection conn = MySQLDataStoreUtilities.getConnection();
             PreparedStatement pst = conn.prepareStatement("SELECT id, username, name, userType FROM Users WHERE userType = ?")) {
            pst.setString(1, "Salesman");
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    User user = new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            null,
                            rs.getString("name"),
                            rs.getString("userType")
                    );
                    salesmen.add(user);
                }
            }
        }
        return salesmen;
    }
}

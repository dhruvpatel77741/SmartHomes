package com.smarthomes.csp584.servlets;

import com.smarthomes.csp584.utils.MySQLDataStoreUtilities;
import org.json.JSONArray;

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

@WebServlet("/autocomplete")
public class AjaxUtility extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String searchTerm = request.getParameter("term");

        try (Connection conn = MySQLDataStoreUtilities.getConnection()) {
            String query = "SELECT name FROM Products WHERE name LIKE ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, "%" + searchTerm + "%");
            ResultSet rs = pst.executeQuery();

            JSONArray productNames = new JSONArray();
            while (rs.next()) {
                productNames.put(rs.getString("name"));
            }

            out.println(productNames.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

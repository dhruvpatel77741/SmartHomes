package com.smarthomes.csp584.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.smarthomes.csp584.utils.MySQLDataStoreUtilities;

@WebServlet("/inventoryReport")
public class InventoryReportServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            Connection conn = MySQLDataStoreUtilities.getConnection();

            String query = "SELECT * FROM products";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            StringBuilder jsonResult = new StringBuilder();
            jsonResult.append("[");

            while (rs.next()) {
                // Retrieve all columns from the result set
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                String category = rs.getString("category");
                double price = rs.getDouble("price");
                int specialDiscount = rs.getInt("specialDiscount");
                double discountPrice = rs.getDouble("discountPrice");
                int manufacturerRebate = rs.getInt("manufacturerRebate");
                double rebatePrice = rs.getDouble("rebatePrice");
                int warranty = rs.getInt("warranty");
                double warrantyPrice = rs.getDouble("warrantyPrice");
                int likes = rs.getInt("likes");
                int availableItems = rs.getInt("availableItems");

                jsonResult.append("{")
                        .append("\"id\":").append(id).append(",")
                        .append("\"name\":\"").append(name).append("\",")
                        .append("\"description\":\"").append(description).append("\",")
                        .append("\"category\":\"").append(category).append("\",")
                        .append("\"price\":").append(price).append(",")
                        .append("\"specialDiscount\":").append(specialDiscount).append(",")
                        .append("\"discountPrice\":").append(discountPrice).append(",")
                        .append("\"manufacturerRebate\":").append(manufacturerRebate).append(",")
                        .append("\"rebatePrice\":").append(rebatePrice).append(",")
                        .append("\"warranty\":").append(warranty).append(",")
                        .append("\"warrantyPrice\":").append(warrantyPrice).append(",")
                        .append("\"likes\":").append(likes).append(",")
                        .append("\"availableItems\":").append(availableItems)
                        .append("},");
            }

            jsonResult.deleteCharAt(jsonResult.length() - 1);
            jsonResult.append("]");

            out.print(jsonResult.toString());

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

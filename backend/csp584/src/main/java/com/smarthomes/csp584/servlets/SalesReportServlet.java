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

@WebServlet("/salesReport")
public class SalesReportServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            Connection conn = MySQLDataStoreUtilities.getConnection();

            String productSalesQuery = "SELECT p.name, p.price, SUM(t.quantity) AS totalQuantity, " +
                    "SUM(t.totalSales) AS totalSales " +
                    "FROM transactions t " +
                    "JOIN products p ON t.productId = p.id " +
                    "GROUP BY p.name, p.price";
            PreparedStatement psProductSales = conn.prepareStatement(productSalesQuery);
            ResultSet rsProductSales = psProductSales.executeQuery();

            StringBuilder jsonResult = new StringBuilder();
            jsonResult.append("{ \"productSales\": [");

            while (rsProductSales.next()) {
                String name = rsProductSales.getString("name");
                double price = rsProductSales.getDouble("price");
                int totalQuantity = rsProductSales.getInt("totalQuantity");
                double totalSales = rsProductSales.getDouble("totalSales");

                jsonResult.append("{")
                        .append("\"name\":\"").append(name).append("\",")
                        .append("\"price\":").append(price).append(",")
                        .append("\"totalQuantity\":").append(totalQuantity).append(",")
                        .append("\"totalSales\":").append(totalSales)
                        .append("},");
            }

            jsonResult.deleteCharAt(jsonResult.length() - 1);
            jsonResult.append("],");

            String dailySalesQuery = "SELECT transactionDate, SUM(totalSales) AS dailyTotalSales " +
                    "FROM transactions " +
                    "GROUP BY transactionDate";
            PreparedStatement psDailySales = conn.prepareStatement(dailySalesQuery);
            ResultSet rsDailySales = psDailySales.executeQuery();

            jsonResult.append("\"dailySales\": [");

            while (rsDailySales.next()) {
                String date = rsDailySales.getString("transactionDate");
                double dailyTotalSales = rsDailySales.getDouble("dailyTotalSales");

                jsonResult.append("{")
                        .append("\"date\":\"").append(date).append("\",")
                        .append("\"dailyTotalSales\":").append(dailyTotalSales)
                        .append("},");
            }

            jsonResult.deleteCharAt(jsonResult.length() - 1);
            jsonResult.append("]}");

            out.print(jsonResult.toString());

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

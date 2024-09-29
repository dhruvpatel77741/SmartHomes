package com.smarthomes.csp584.servlets;

import com.smarthomes.csp584.utils.MySQLDataStoreUtilities;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/trending")
public class TrendingServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Connection conn = null;
        List<Map<String, Object>> topLikedProducts = new ArrayList<>();
        List<Map<String, Object>> topSoldProducts = new ArrayList<>();
        List<Map<String, Object>> topZipProducts = new ArrayList<>();

        try {
            conn = MySQLDataStoreUtilities.getConnection();

            if (conn == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\": \"Database connection failed\"}");
                return;
            }

            String topLikedQuery = "SELECT id, likes FROM products ORDER BY likes DESC LIMIT 5";
            PreparedStatement pstLiked = conn.prepareStatement(topLikedQuery);
            ResultSet rsLiked = pstLiked.executeQuery();
            while (rsLiked.next()) {
                Map<String, Object> productData = new HashMap<>();
                productData.put("productId", rsLiked.getInt("id"));
                productData.put("likes", rsLiked.getInt("likes"));
                topLikedProducts.add(productData);
            }
            rsLiked.close();
            pstLiked.close();

            String topSoldQuery = "SELECT productId, SUM(quantity) as totalSold FROM orders GROUP BY productId ORDER BY totalSold DESC LIMIT 5";
            PreparedStatement pstSold = conn.prepareStatement(topSoldQuery);
            ResultSet rsSold = pstSold.executeQuery();
            while (rsSold.next()) {
                Map<String, Object> productData = new HashMap<>();
                productData.put("productId", rsSold.getInt("productId"));
                productData.put("totalSold", rsSold.getInt("totalSold"));
                topSoldProducts.add(productData);
            }
            rsSold.close();
            pstSold.close();

            String topZipQuery = "SELECT SUBSTRING_INDEX(storeAddress, ' ', -1) as zipCode, SUM(quantity) as totalSold " +
                    "FROM transactions " +
                    "GROUP BY zipCode " +
                    "ORDER BY totalSold DESC LIMIT 5";
            PreparedStatement pstZip = conn.prepareStatement(topZipQuery);
            ResultSet rsZip = pstZip.executeQuery();
            while (rsZip.next()) {
                Map<String, Object> zipData = new HashMap<>();
                zipData.put("zipCode", rsZip.getString("zipCode"));
                zipData.put("totalSold", rsZip.getInt("totalSold"));
                topZipProducts.add(zipData);
            }
            rsZip.close();
            pstZip.close();

            StringBuilder jsonResponse = new StringBuilder();
            jsonResponse.append("{");

            jsonResponse.append("\"topLikedProducts\": [");
            for (int i = 0; i < topLikedProducts.size(); i++) {
                Map<String, Object> product = topLikedProducts.get(i);
                jsonResponse.append("{\"productId\": ")
                        .append(product.get("productId"))
                        .append(", \"likes\": ")
                        .append(product.get("likes"))
                        .append("}");
                if (i < topLikedProducts.size() - 1) {
                    jsonResponse.append(",");
                }
            }
            jsonResponse.append("],");

            jsonResponse.append("\"topSoldProducts\": [");
            for (int i = 0; i < topSoldProducts.size(); i++) {
                Map<String, Object> product = topSoldProducts.get(i);
                jsonResponse.append("{\"productId\": ")
                        .append(product.get("productId"))
                        .append(", \"totalSold\": ")
                        .append(product.get("totalSold"))
                        .append("}");
                if (i < topSoldProducts.size() - 1) {
                    jsonResponse.append(",");
                }
            }
            jsonResponse.append("],");

            jsonResponse.append("\"topZipProducts\": [");
            for (int i = 0; i < topZipProducts.size(); i++) {
                Map<String, Object> zip = topZipProducts.get(i);
                jsonResponse.append("{\"zipCode\": \"")
                        .append(zip.get("zipCode"))
                        .append("\", \"totalSold\": ")
                        .append(zip.get("totalSold"))
                        .append("}");
                if (i < topZipProducts.size() - 1) {
                    jsonResponse.append(",");
                }
            }
            jsonResponse.append("]");

            jsonResponse.append("}");

            response.setContentType("application/json");
            response.getWriter().write(jsonResponse.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Error fetching trending data: " + e.getMessage() + "\"}");
        } finally {
            MySQLDataStoreUtilities.closeConnection(conn);
        }
    }
}

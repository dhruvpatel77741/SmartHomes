package com.smarthomes.csp584.servlets;

import com.smarthomes.csp584.models.Accessory;
import com.smarthomes.csp584.models.Product;
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
import java.util.stream.Collectors;

@WebServlet(name = "productManagementServlet", value = "/manageProducts")
public class ProductManagementServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        List<Product> products = getAllProducts();
        JSONArray productArray = new JSONArray();

        for (Product product : products) {
            JSONObject productJson = new JSONObject();
            productJson.put("id", product.getId());
            productJson.put("name", product.getName());
            productJson.put("description", product.getDescription());
            productJson.put("category", product.getCategory());
            productJson.put("price", product.getPrice());
            productJson.put("specialDiscount", product.isSpecialDiscount());
            productJson.put("discountPrice", product.getDiscountPrice());
            productJson.put("manufacturerRebate", product.isManufacturerRebate());
            productJson.put("rebatePrice", product.getRebatePrice());
            productJson.put("warranty", product.isWarranty());
            productJson.put("warrantyPrice", product.getWarrantyPrice());
            productJson.put("likes", product.getLikes());

            JSONArray accessoriesArray = new JSONArray();
            for (Accessory accessory : product.getAccessories()) {
                JSONObject accessoryJson = new JSONObject();
                accessoryJson.put("id", accessory.getId());
                accessoryJson.put("name", accessory.getName());
                accessoryJson.put("price", accessory.getPrice());
                accessoriesArray.put(accessoryJson);
            }
            productJson.put("accessories", accessoriesArray);

            productArray.put(productJson);
        }

        out.println(productArray.toString());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        JSONObject requestJson = new JSONObject(request.getReader().lines().reduce("", (acc, actual) -> acc + actual));

        try (Connection conn = MySQLDataStoreUtilities.getConnection()) {
            String query = "INSERT INTO Products (name, description, category, price, specialDiscount, discountPrice, manufacturerRebate, rebatePrice, warranty, warrantyPrice, likes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, requestJson.getString("name"));
            pst.setString(2, requestJson.getString("description"));
            pst.setString(3, requestJson.getString("category"));
            pst.setDouble(4, requestJson.getDouble("price"));
            pst.setBoolean(5, requestJson.getBoolean("specialDiscount"));
            pst.setDouble(6, requestJson.getDouble("discountPrice"));
            pst.setBoolean(7, requestJson.getBoolean("manufacturerRebate"));
            pst.setDouble(8, requestJson.getDouble("rebatePrice"));
            pst.setBoolean(9, requestJson.getBoolean("warranty"));
            pst.setDouble(10, requestJson.getDouble("warrantyPrice"));
            pst.setInt(11, requestJson.getInt("likes"));

            int result = pst.executeUpdate();
            if (result > 0) {
                out.println("{\"status\": \"success\", \"message\": \"Product added successfully\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.println("{\"status\": \"error\", \"message\": \"Failed to add product\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"status\": \"error\", \"message\": \"Error while adding product: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        JSONObject requestJson = new JSONObject(request.getReader().lines().collect(Collectors.joining()));

        try (Connection conn = MySQLDataStoreUtilities.getConnection()) {
            String query = "UPDATE Products SET name = ?, description = ?, category = ?, price = ?, specialDiscount = ?, discountPrice = ?, manufacturerRebate = ?, rebatePrice = ?, warranty = ?, warrantyPrice = ?, likes = ? WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, requestJson.getString("name"));
            pst.setString(2, requestJson.getString("description"));
            pst.setString(3, requestJson.getString("category"));
            pst.setDouble(4, requestJson.getDouble("price"));
            pst.setBoolean(5, requestJson.getBoolean("specialDiscount"));
            pst.setDouble(6, requestJson.getDouble("discountPrice"));
            pst.setBoolean(7, requestJson.getBoolean("manufacturerRebate"));
            pst.setDouble(8, requestJson.getDouble("rebatePrice"));
            pst.setBoolean(9, requestJson.getBoolean("warranty"));
            pst.setDouble(10, requestJson.getDouble("warrantyPrice"));
            pst.setInt(11, requestJson.getInt("likes"));
            pst.setInt(12, requestJson.getInt("id"));

            int result = pst.executeUpdate();
            if (result > 0) {
                out.println("{\"status\": \"success\", \"message\": \"Product updated successfully\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.println("{\"status\": \"error\", \"message\": \"Product not found or update failed\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"status\": \"error\", \"message\": \"Error while updating product: " + e.getMessage() + "\"}");
        }
    }


    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String productIdParam = request.getParameter("id");
        if (productIdParam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"status\": \"error\", \"message\": \"Product ID is required\"}");
            return;
        }

        int productId;
        try {
            productId = Integer.parseInt(productIdParam);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{\"status\": \"error\", \"message\": \"Invalid product ID format\"}");
            return;
        }

        try (Connection conn = MySQLDataStoreUtilities.getConnection()) {
            String query = "DELETE FROM Products WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, productId);

            int result = pst.executeUpdate();
            if (result > 0) {
                out.println("{\"status\": \"success\", \"message\": \"Product deleted successfully\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.println("{\"status\": \"error\", \"message\": \"Product not found or deletion failed\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"status\": \"error\", \"message\": \"Error while deleting product: " + e.getMessage() + "\"}");
            e.printStackTrace(out);
        }
    }

    private List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM Products";

        try (Connection conn = MySQLDataStoreUtilities.getConnection();
             PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getBoolean("specialDiscount"),
                        rs.getDouble("discountPrice"),
                        rs.getBoolean("manufacturerRebate"),
                        rs.getDouble("rebatePrice"),
                        rs.getBoolean("warranty"),
                        rs.getDouble("warrantyPrice"),
                        rs.getInt("likes"),
                        getAccessoriesByProductId(rs.getInt("id"))
                );
                products.add(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }


    private List<Accessory> getAccessoriesByProductId(int productId) {
        List<Accessory> accessories = new ArrayList<>();
        Connection conn = null;

        try {
            conn = MySQLDataStoreUtilities.getConnection();
            String query = "SELECT * FROM Accessories WHERE product_id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, productId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Accessory accessory = new Accessory(
                        rs.getInt("id"),
                        rs.getInt("product_id"),
                        rs.getString("accessory_name"),
                        rs.getDouble("accessory_price")
                );
                accessories.add(accessory);
            }

            pst.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            MySQLDataStoreUtilities.closeConnection(conn);
        }

        return accessories;
    }
}

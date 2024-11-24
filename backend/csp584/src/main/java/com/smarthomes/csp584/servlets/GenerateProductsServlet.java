package com.smarthomes.csp584.servlets;

import com.smarthomes.csp584.utils.MySQLDataStoreUtilities;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

@WebServlet(name = "GenerateProductsServlet", value = "/generateProducts")
public class GenerateProductsServlet extends HttpServlet {

    private static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");

    private static final String[] CATEGORIES = {
            "Smart Doorbells", "Smart Doorlocks", "Smart Speakers",
            "Smart Lighting", "Smart Thermostats"
    };

    private int getHighestId() throws SQLException {
        Connection connection = null;
        try {
            connection = MySQLDataStoreUtilities.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT MAX(id) AS max_id FROM products");

            if (resultSet.next()) {
                return resultSet.getInt("max_id");
            }
        } finally {
            MySQLDataStoreUtilities.closeConnection(connection);
        }
        return 0;
    }

    private String generateProductName(String category) throws IOException {
        String requestUrl = "https://api.openai.com/v1/chat/completions";
        URL url = new URL(requestUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + OPENAI_API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        JSONObject payload = new JSONObject();
        payload.put("model", "gpt-3.5-turbo");
        JSONArray messages = new JSONArray();

        // Add detailed instructions for generating the product name
        messages.put(new JSONObject().put("role", "system")
                .put("content", "You are an assistant that generates creative and descriptive product names."));
        messages.put(new JSONObject().put("role", "user")
                .put("content", "Generate a unique and descriptive product name for a product in the category '"
                        + category + "'. The name should be concise and engaging."));

        payload.put("messages", messages);

        // Allow enough tokens for generating a concise name
        payload.put("max_tokens", 20);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.toString().getBytes());
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        JSONObject jsonResponse = new JSONObject(response.toString());
        return jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content").trim();
    }

    private String generateProductDescription(String category) throws IOException {
        String requestUrl = "https://api.openai.com/v1/chat/completions";
        URL url = new URL(requestUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + OPENAI_API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        JSONObject payload = new JSONObject();
        payload.put("model", "gpt-3.5-turbo");
        JSONArray messages = new JSONArray();

        // Update instructions to request a description of approximately 100 words
        messages.put(new JSONObject().put("role", "system")
                .put("content", "You are an assistant that generates detailed product descriptions."));
        messages.put(new JSONObject().put("role", "user")
                .put("content", "Generate a detailed and engaging product description for a product in the category '"
                        + category + "'. The description should be approximately 100 words long."));

        payload.put("messages", messages);

        // Increase max_tokens to ensure enough space for a 100-word response
        payload.put("max_tokens", 150);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.toString().getBytes());
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        JSONObject jsonResponse = new JSONObject(response.toString());
        return jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content").trim();
    }

    private JSONObject generateProduct(int productId, String category) throws IOException {
        String productName = generateProductName(category);
        String description = generateProductDescription(category);
        Random random = new Random();

        JSONObject product = new JSONObject();
        product.put("id", productId);
        product.put("name", productName);
        product.put("description", description);
        product.put("category", category);
        product.put("price", 50 + productId * 10);
        product.put("specialDiscount", 0);
        product.put("discountPrice", JSONObject.NULL);
        product.put("manufacturerRebate", productId % 2 == 0 ? 1 : 0);
        product.put("rebatePrice", productId % 2 == 0 ? 10 + productId * 5 : JSONObject.NULL);
        product.put("warranty", 1);
        product.put("warrantyPrice", 9.99 + productId * 2);
        product.put("likes", random.nextInt(451) + 50);
        product.put("availableItems", random.nextInt(91) + 10);

        return product;
    }


    private void storeProductsInDb(JSONArray products) throws SQLException {
        String insertQuery = "INSERT INTO products (id, name, description, category, price, specialDiscount, " +
                "discountPrice, manufacturerRebate, rebatePrice, warranty, warrantyPrice, likes, availableItems) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection connection = null;
        try {
            connection = MySQLDataStoreUtilities.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

            for (int i = 0; i < products.length(); i++) {
                JSONObject product = products.getJSONObject(i);
                preparedStatement.setInt(1, product.getInt("id"));
                preparedStatement.setString(2, product.getString("name"));
                preparedStatement.setString(3, product.getString("description"));
                preparedStatement.setString(4, product.getString("category"));
                preparedStatement.setDouble(5, product.getDouble("price"));
                preparedStatement.setInt(6, product.getInt("specialDiscount"));
                preparedStatement.setObject(7, product.isNull("discountPrice") ? null : product.getDouble("discountPrice"));
                preparedStatement.setInt(8, product.getInt("manufacturerRebate"));
                preparedStatement.setObject(9, product.isNull("rebatePrice") ? null : product.getDouble("rebatePrice"));
                preparedStatement.setInt(10, product.getInt("warranty"));
                preparedStatement.setDouble(11, product.getDouble("warrantyPrice"));
                preparedStatement.setInt(12, product.getInt("likes"));
                preparedStatement.setInt(13, product.getInt("availableItems"));

                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
        } finally {
            MySQLDataStoreUtilities.closeConnection(connection);
        }
    }

    private void saveProductsToFile(JSONArray products, String filePath) throws IOException {
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(products.toString(4));
            file.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int numProducts = 10;
        JSONArray products = new JSONArray();

        try {
            int startingId = getHighestId() + 1;

            for (int i = 0; i < numProducts; i++) {
                String category = CATEGORIES[i % CATEGORIES.length];
                products.put(generateProduct(startingId + i, category));
            }

            storeProductsInDb(products);

            String productsFilePath = System.getProperty("user.home") + "/Desktop/Assignment5/OpenAI/products.json";

            File file = new File(productsFilePath);
            file.getParentFile().mkdirs();

            saveProductsToFile(products, productsFilePath);

            response.setContentType("application/json");
            response.getWriter().println(new JSONObject().put("status", "success").put("products", products));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println(new JSONObject().put("status", "error").put("message", e.getMessage()));
        }
    }

}

package com.smarthomes.csp584.servlets;

import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;

@WebServlet(name = "productManagementServlet", value = "/manageProducts")
public class ProductManagementServlet extends HttpServlet {

    private JSONArray products;
    private String filePath;

    public void init() throws ServletException {
        // Load the Products.json file during servlet initialization
        try {
            filePath = getServletContext().getRealPath("/WEB-INF/Products.json");
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            products = new JSONArray(content);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServletException("Failed to load products");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        handleRequest(request, response, "POST");
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        handleRequest(request, response, "PUT");
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        handleRequest(request, response, "DELETE");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        response.setStatus(HttpServletResponse.SC_OK); // 200 OK
        out.println(products.toString());
    }

    private void handleRequest(HttpServletRequest request, HttpServletResponse response, String method) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);

            // Validate request body
            if (requestBody == null || requestBody.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
                out.println(new JSONObject().put("status", "error").put("message", "Request body is missing"));
                return;
            }

            JSONObject requestBodyJson = new JSONObject(requestBody);

            switch (method) {
                case "POST":
                    addProduct(requestBodyJson, response, out);
                    break;
                case "PUT":
                    updateProduct(requestBodyJson, response, out);
                    break;
                case "DELETE":
                    deleteProduct(requestBodyJson, response, out);
                    break;
                default:
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
                    out.println(new JSONObject().put("status", "error").put("message", "Invalid request method"));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
            out.println(new JSONObject().put("status", "error").put("message", "An error occurred while processing the request"));
        }
    }

    private void addProduct(JSONObject product, HttpServletResponse response, PrintWriter out) throws IOException {
        // Automatically assign a new ID by finding the max ID and adding 1
        int newId = getNextProductId();

        // Assign the new ID to the product
        product.put("id", newId);

        // Add new product to the products array
        products.put(product);
        saveProductsToFile();

        response.setStatus(HttpServletResponse.SC_CREATED); // 201 Created
        out.println(new JSONObject().put("status", "success").put("message", "Product added successfully"));
    }

    private int getNextProductId() {
        int maxId = 0;

        // Find the max ID in the products array
        for (int i = 0; i < products.length(); i++) {
            JSONObject existingProduct = products.getJSONObject(i);
            if (existingProduct.has("id")) {
                int currentId = existingProduct.getInt("id");
                if (currentId > maxId) {
                    maxId = currentId;
                }
            }
        }

        // Return the next ID by adding 1 to the max ID
        return maxId + 1;
    }

    private void updateProduct(JSONObject product, HttpServletResponse response, PrintWriter out) throws IOException {
        boolean productFound = false;

        // Search for product by ID and update it
        for (int i = 0; i < products.length(); i++) {
            JSONObject existingProduct = products.getJSONObject(i);
            if (existingProduct.getInt("id") == product.getInt("id")) {
                products.put(i, product);
                productFound = true;
                break;
            }
        }

        if (productFound) {
            saveProductsToFile();
            response.setStatus(HttpServletResponse.SC_OK); // 200 OK
            out.println(new JSONObject().put("status", "success").put("message", "Product updated successfully"));
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404 Not Found
            out.println(new JSONObject().put("status", "error").put("message", "Product not found"));
        }
    }

    private void deleteProduct(JSONObject product, HttpServletResponse response, PrintWriter out) throws IOException {
        boolean productFound = false;

        // Search for product by ID and remove it
        for (int i = 0; i < products.length(); i++) {
            JSONObject existingProduct = products.getJSONObject(i);
            if (existingProduct.getInt("id") == product.getInt("id")) {
                products.remove(i);
                productFound = true;
                break;
            }
        }

        if (productFound) {
            saveProductsToFile();
            response.setStatus(HttpServletResponse.SC_OK); // 200 OK
            out.println(new JSONObject().put("status", "success").put("message", "Product deleted successfully"));
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404 Not Found
            out.println(new JSONObject().put("status", "error").put("message", "Product not found"));
        }
    }

    private void saveProductsToFile() throws IOException {
        // Write updated products array back to the Products.json file
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(products.toString(4)); // Indent by 4 spaces for readability
        }
    }

    public void destroy() {
        // Cleanup code (if needed)
    }
}

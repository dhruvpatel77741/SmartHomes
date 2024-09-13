package com.smarthomes.csp584.servlets;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet(name = "productManagementServlet", value = "/manageProducts")
public class ProductManagementServlet extends HttpServlet {

    private JSONArray products;
    private String filePath;

    public void init() throws ServletException {
        try {
            // Adjust file path to use the resources folder instead of WEB-INF
            filePath = getServletContext().getRealPath("/resources/Products.json");
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
        response.setStatus(HttpServletResponse.SC_OK);
        out.println(products.toString());
    }

    private void handleRequest(HttpServletRequest request, HttpServletResponse response, String method) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);

        if (requestBody == null || requestBody.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println(new JSONObject().put("status", "error").put("message", "Invalid request method"));
                break;
        }
    }

    private void addProduct(JSONObject product, HttpServletResponse response, PrintWriter out) throws IOException {
        int newId = getNextProductId();
        product.put("id", newId);
        products.put(product);
        saveProductsToFile();

        response.setStatus(HttpServletResponse.SC_CREATED);
        out.println(new JSONObject().put("status", "success").put("message", "Product added successfully"));
    }

    private int getNextProductId() {
        int maxId = 0;

        for (int i = 0; i < products.length(); i++) {
            JSONObject existingProduct = products.getJSONObject(i);
            if (existingProduct.has("id")) {
                int currentId = existingProduct.getInt("id");
                if (currentId > maxId) {
                    maxId = currentId;
                }
            }
        }

        return maxId + 1;
    }

    private void updateProduct(JSONObject product, HttpServletResponse response, PrintWriter out) throws IOException {
        boolean productFound = false;

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
            response.setStatus(HttpServletResponse.SC_OK);
            out.println(new JSONObject().put("status", "success").put("message", "Product updated successfully"));
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.println(new JSONObject().put("status", "error").put("message", "Product not found"));
        }
    }

    private void deleteProduct(JSONObject product, HttpServletResponse response, PrintWriter out) throws IOException {
        boolean productFound = false;

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
            response.setStatus(HttpServletResponse.SC_OK);
            out.println(new JSONObject().put("status", "success").put("message", "Product deleted successfully"));
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.println(new JSONObject().put("status", "error").put("message", "Product not found"));
        }
    }

    private void saveProductsToFile() throws IOException {
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(products.toString(4));
        }
    }
}

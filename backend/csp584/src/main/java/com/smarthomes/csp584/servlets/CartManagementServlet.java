package com.smarthomes.csp584.servlets;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet(name = "CartManagementServlet", urlPatterns = {"/cart"})
public class CartManagementServlet extends HttpServlet {

    private JSONArray cart;
    private String cartFilePath;

    public void init() {
        // Adjust the file path to point to the resources folder
        cartFilePath = getServletContext().getRealPath("/resources/Cart.json");
        loadCart();
    }

    private void loadCart() {
        try {
            String cartContent = new String(Files.readAllBytes(Paths.get(cartFilePath)));
            cart = new JSONArray(cartContent);
        } catch (IOException e) {
            e.printStackTrace();
            cart = new JSONArray(); // Initialize as empty if file read fails
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            sb.append(line);
        }
        JSONObject jsonBody = new JSONObject(sb.toString());

        String action = jsonBody.getString("action");
        String userId = jsonBody.getString("userId");

        if (userId == null || userId.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println(new JSONObject().put("status", "error").put("message", "userId is required"));
            return;
        }

        switch (action) {
            case "addToCart":
                handleAddToCart(jsonBody, response, out, userId);
                break;
            case "removeFromCart":
                handleRemoveFromCart(jsonBody, response, out, userId);
                break;
            case "clearCart":
                handleClearCart(response, out, userId);
                break;
            default:
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println(new JSONObject().put("status", "error").put("message", "Invalid action"));
                break;
        }
    }

    private void handleAddToCart(JSONObject jsonBody, HttpServletResponse response, PrintWriter out, String userId) throws IOException {
        try {
            String productId = jsonBody.optString("productId", null);
            String productName = jsonBody.optString("productName", null);
            if (productId == null || productName == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println(new JSONObject().put("status", "error").put("message", "productId and productName are required"));
                return;
            }

            double productPrice = jsonBody.optDouble("productPrice", 0.0);
            int quantity = jsonBody.optInt("quantity", 1);
            boolean warrantyAdded = jsonBody.optBoolean("warrantyAdded", false);
            double warrantyPrice = warrantyAdded ? jsonBody.optDouble("warrantyPrice", 0.0) : 0.0;

            JSONArray accessoriesArray = jsonBody.optJSONArray("accessories");
            if (accessoriesArray == null) {
                accessoriesArray = new JSONArray();
            }

            double accessoriesPrice = 0.0;
            for (int i = 0; i < accessoriesArray.length(); i++) {
                JSONObject accessory = accessoriesArray.optJSONObject(i);
                if (accessory != null) {
                    accessoriesPrice += accessory.optDouble("price", 0.0);
                }
            }

            double totalPrice = (productPrice * quantity) + warrantyPrice + accessoriesPrice;

            JSONObject newItem = new JSONObject();
            newItem.put("userId", userId);
            newItem.put("productId", productId);
            newItem.put("productName", productName);
            newItem.put("quantity", quantity);
            newItem.put("productPrice", productPrice);
            newItem.put("warrantyAdded", warrantyAdded);
            newItem.put("warrantyPrice", warrantyPrice);
            newItem.put("accessories", accessoriesArray);
            newItem.put("accessoriesPrice", accessoriesPrice);
            newItem.put("totalPrice", totalPrice);

            boolean productExists = false;
            for (int i = 0; i < cart.length(); i++) {
                JSONObject item = cart.getJSONObject(i);
                if (item.getString("userId").equals(userId) && item.getString("productId").equals(productId)) {
                    item.put("quantity", item.getInt("quantity") + quantity);
                    item.put("totalPrice", (item.getDouble("productPrice") * item.getInt("quantity")) + item.getDouble("warrantyPrice") + item.getDouble("accessoriesPrice"));
                    productExists = true;
                    break;
                }
            }

            if (!productExists) {
                cart.put(newItem);
            }

            saveCart();
            out.println(new JSONObject().put("status", "success").put("message", "Item added to cart"));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(new JSONObject().put("status", "error").put("message", e.getMessage()));
        }
    }

    private void handleRemoveFromCart(JSONObject jsonBody, HttpServletResponse response, PrintWriter out, String userId) throws IOException {
        String productId = jsonBody.getString("productId");

        for (int i = 0; i < cart.length(); i++) {
            JSONObject item = cart.getJSONObject(i);
            if (item.getString("userId").equals(userId) && item.getString("productId").equals(productId)) {
                cart.remove(i);
                break;
            }
        }

        saveCart();
        out.println(new JSONObject().put("status", "success").put("message", "Item removed from cart"));
    }

    private void handleClearCart(HttpServletResponse response, PrintWriter out, String userId) throws IOException {
        JSONArray updatedCart = new JSONArray();
        for (int i = 0; i < cart.length(); i++) {
            JSONObject item = cart.getJSONObject(i);
            if (!item.getString("userId").equals(userId)) {
                updatedCart.put(item);
            }
        }
        cart = updatedCart;

        saveCart();
        out.println(new JSONObject().put("status", "success").put("message", "Cart cleared for user " + userId));
    }

    private void saveCart() throws IOException {
        try (FileWriter file = new FileWriter(cartFilePath)) {
            file.write(cart.toString());
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String userId = request.getParameter("userId");
        if (userId == null || userId.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println(new JSONObject().put("status", "error").put("message", "userId is required"));
            return;
        }

        JSONArray userCart = new JSONArray();
        for (int i = 0; i < cart.length(); i++) {
            JSONObject item = cart.getJSONObject(i);
            if (item.getString("userId").equals(userId)) {
                userCart.put(item);
            }
        }

        out.println(userCart.toString());
    }
}

package com.smarthomes.csp584.servlets;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;

@WebServlet(name = "CartManagementServlet", urlPatterns = {"/cart"})
public class CartManagementServlet extends HttpServlet {

    private JSONArray cart; // This holds the cart data

    public void init() {
        // Load the Cart.json file during servlet initialization
        try {
            String cartFilePath = getServletContext().getRealPath("/WEB-INF/Cart.json");
            String cartContent = new String(Files.readAllBytes(Paths.get(cartFilePath)));
            cart = new JSONArray(cartContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Parse the request body as JSON
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            sb.append(line);
        }
        JSONObject jsonBody = new JSONObject(sb.toString());

        // Extract action and userId from the JSON body
        String action = jsonBody.getString("action");
        String userId = jsonBody.getString("userId");

        if (userId == null || userId.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println(new JSONObject().put("status", "error").put("message", "userId is required"));
            return;
        }

        // Perform action based on the type (addToCart, removeFromCart, clearCart)
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
            // Read request parameters from JSON body
            String productId = jsonBody.optString("productId", null);
            String productName = jsonBody.optString("productName", null); // New field for product name
            if (productId == null || productName == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println(new JSONObject().put("status", "error").put("message", "productId and productName are required"));
                return;
            }

            double productPrice = jsonBody.optDouble("productPrice", 0.0);
            int quantity = jsonBody.optInt("quantity", 1); // Default to 1 if not provided
            boolean warrantyAdded = jsonBody.optBoolean("warrantyAdded", false);
            double warrantyPrice = warrantyAdded ? jsonBody.optDouble("warrantyPrice", 0.0) : 0.0;

            // Log the inputs
            System.out.println("ProductId: " + productId);
            System.out.println("ProductName: " + productName); // Log the product name
            System.out.println("ProductPrice: " + productPrice);
            System.out.println("Quantity: " + quantity);
            System.out.println("WarrantyAdded: " + warrantyAdded);
            System.out.println("WarrantyPrice: " + warrantyPrice);

            // Check if accessories array exists and is not null
            JSONArray accessoriesArray = jsonBody.optJSONArray("accessories");
            if (accessoriesArray == null) {
                accessoriesArray = new JSONArray(); // Initialize as empty if null
            }

            // Log the accessories array
            System.out.println("Accessories: " + accessoriesArray.toString());

            // Parse accessories prices and calculate the total accessories price
            double accessoriesPrice = 0.0;
            for (int i = 0; i < accessoriesArray.length(); i++) {
                JSONObject accessory = accessoriesArray.optJSONObject(i);
                if (accessory != null) {
                    accessoriesPrice += accessory.optDouble("price", 0.0);
                } else {
                    System.out.println("Accessory at index " + i + " is null.");
                }
            }

            // Log the calculated prices
            System.out.println("AccessoriesPrice: " + accessoriesPrice);

            // Calculate total price for this cart item
            double totalPrice = (productPrice * quantity) + warrantyPrice + accessoriesPrice;

            // Log the total price
            System.out.println("TotalPrice: " + totalPrice);

            // Create a new cart item
            JSONObject newItem = new JSONObject();
            newItem.put("userId", userId);
            newItem.put("productId", productId);
            newItem.put("productName", productName); // Add product name to the item
            newItem.put("quantity", quantity);
            newItem.put("productPrice", productPrice);
            newItem.put("warrantyAdded", warrantyAdded);
            newItem.put("warrantyPrice", warrantyPrice);
            newItem.put("accessories", accessoriesArray); // Store the accessories details
            newItem.put("accessoriesPrice", accessoriesPrice);
            newItem.put("totalPrice", totalPrice); // Store the total price

            // Log the new item details
            System.out.println("New Item: " + newItem.toString());

            // Check if the user already has this product in the cart, if so, update it
            boolean productExists = false;
            for (int i = 0; i < cart.length(); i++) {
                JSONObject item = cart.getJSONObject(i);
                if (item.getString("userId").equals(userId) && item.getString("productId").equals(productId)) {
                    // Update quantity and recalculate total price
                    item.put("quantity", item.getInt("quantity") + quantity);
                    item.put("totalPrice", (item.getDouble("productPrice") * item.getInt("quantity")) + item.getDouble("warrantyPrice") + item.getDouble("accessoriesPrice"));
                    productExists = true;
                    break;
                }
            }

            // If the product is new, add it to the cart
            if (!productExists) {
                cart.put(newItem);
            }

            // Save cart to Cart.json
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

        // Remove the product with the specified ID for the given userId
        for (int i = 0; i < cart.length(); i++) {
            JSONObject item = cart.getJSONObject(i);
            if (item.getString("userId").equals(userId) && item.getString("productId").equals(productId)) {
                cart.remove(i);
                break;
            }
        }

        // Save cart to Cart.json
        saveCart();

        out.println(new JSONObject().put("status", "success").put("message", "Item removed from cart"));
    }

    private void handleClearCart(HttpServletResponse response, PrintWriter out, String userId) throws IOException {
        // Remove all items for the given userId
        JSONArray updatedCart = new JSONArray();
        for (int i = 0; i < cart.length(); i++) {
            JSONObject item = cart.getJSONObject(i);
            if (!item.getString("userId").equals(userId)) {
                updatedCart.put(item); // Keep items that don't match the userId
            }
        }
        cart = updatedCart;

        // Save the updated cart to Cart.json
        saveCart();

        out.println(new JSONObject().put("status", "success").put("message", "Cart cleared for user " + userId));
    }

    private void saveCart() throws IOException {
        String cartFilePath = getServletContext().getRealPath("/WEB-INF/Cart.json");
        System.out.println("Saving cart to: " + cartFilePath);  // Add this logging
        try (FileWriter file = new FileWriter(cartFilePath)) {
            file.write(cart.toString());
        } catch (IOException e) {
            System.out.println("Failed to save cart: " + e.getMessage());  // Log if there’s an issue saving
            throw e;
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

        // Filter the cart items based on the userId
        JSONArray userCart = new JSONArray();
        for (int i = 0; i < cart.length(); i++) {
            JSONObject item = cart.getJSONObject(i);
            if (item.getString("userId").equals(userId)) {
                userCart.put(item);
            }
        }

        out.println(userCart.toString());
    }

    public void destroy() {
        // Any cleanup code goes here (if necessary)
    }
}

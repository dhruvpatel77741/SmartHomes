package com.smarthomes.csp584.servlets;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@WebServlet(name = "ordersServlet", value = "/orders")
public class OrdersServlet extends HttpServlet {

    private JSONArray orders;
    private JSONArray users;

    public void init() {
        loadOrders();
        loadUsers();
    }

    private void loadOrders() {
        try {
            String filePath = getServletContext().getRealPath("/WEB-INF/Orders.json");
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            orders = new JSONArray(content);
        } catch (IOException e) {
            e.printStackTrace();
            orders = new JSONArray();
        }
    }

    private void loadUsers() {
        try {
            String filePath = getServletContext().getRealPath("/WEB-INF/Users.json");
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            users = new JSONArray(content);
        } catch (IOException e) {
            e.printStackTrace();
            users = new JSONArray(); // Fallback to empty array on failure
        }
    }

    private String getUsername(int userId) {
        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            if (user.getInt("_id") == userId) {
                return user.getString("name");
            }
        }
        return "Unknown"; // Return "Unknown" if user not found
    }

    // Add a new order for a user
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
        JSONObject requestBodyJson = new JSONObject(requestBody);

        int userId = requestBodyJson.getInt("userId");
        JSONObject orderData = requestBodyJson.getJSONObject("orderData");
        String checkout = requestBodyJson.getString("checkout"); // HomeDelivery or PickUp
        String paymentMode = requestBodyJson.getString("paymentMode"); // cod or creditCard
        JSONObject paymentDetails = requestBodyJson.getJSONObject("paymentDetails"); // Credit card or COD details
        JSONObject address = requestBodyJson.getJSONObject("address"); // Delivery Address
        String status = "Order Placed";

        // Generate a random orderId
        String orderId = UUID.randomUUID().toString();

        // Create a new order object
        JSONObject newOrder = new JSONObject();
        newOrder.put("orderId", orderId);
        newOrder.put("userId", userId);
        newOrder.put("orderData", orderData);
        newOrder.put("checkout", checkout);
        newOrder.put("paymentMode", paymentMode);
        newOrder.put("paymentDetails", paymentDetails);
        newOrder.put("address", address);
        newOrder.put("status", status);

        // Add the new order to the orders array
        orders.put(newOrder);

        // Save the updated orders to the Orders.json file
        try {
            String filePath = getServletContext().getRealPath("/WEB-INF/Orders.json");
            Files.write(Paths.get(filePath), orders.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(new JSONObject().put("status", "error").put("message", "Failed to place order"));
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        out.println(new JSONObject().put("status", "success").put("message", "Order placed successfully").put("orderId", orderId));
    }

    // Get all orders for a particular user by userId
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String userIdParam = request.getParameter("userId");

        if (userIdParam != null) {
            int userId = Integer.parseInt(userIdParam);
            JSONArray userOrders = new JSONArray();

            for (int i = 0; i < orders.length(); i++) {
                JSONObject order = orders.getJSONObject(i);
                if (order.getInt("userId") == userId) {
                    order.put("username", getUsername(userId));
                    userOrders.put(order);
                }
            }

            if (userOrders.length() > 0) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.println(new JSONObject().put("status", "success").put("orders", userOrders));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.println(new JSONObject().put("status", "error").put("message", "No orders found for the user"));
            }
        } else {
            for (int i = 0; i < orders.length(); i++) {
                JSONObject order = orders.getJSONObject(i);
                int userId = order.getInt("userId");
                order.put("username", getUsername(userId));
            }

            response.setStatus(HttpServletResponse.SC_OK);
            out.println(new JSONObject().put("status", "success").put("orders", orders));
        }
    }

    // Update the status of an existing order (e.g., Order Placed, In-Transit, Delivered)
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
        JSONObject requestBodyJson = new JSONObject(requestBody);

        String orderId = requestBodyJson.getString("orderId");
        String newStatus = requestBodyJson.getString("status");

        boolean orderFound = false;

        // Find the order by orderId and update its status
        for (int i = 0; i < orders.length(); i++) {
            JSONObject order = orders.getJSONObject(i);
            if (order.getString("orderId").equals(orderId)) {
                order.put("status", newStatus); // Update status of the order
                orderFound = true;
                break;
            }
        }

        if (orderFound) {
            // Save the updated orders to the Orders.json file
            try {
                String filePath = getServletContext().getRealPath("/WEB-INF/Orders.json");
                Files.write(Paths.get(filePath), orders.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.println(new JSONObject().put("status", "error").put("message", "Failed to update order status"));
                return;
            }

            response.setStatus(HttpServletResponse.SC_OK);
            out.println(new JSONObject().put("status", "success").put("message", "Order status updated successfully"));
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.println(new JSONObject().put("status", "error").put("message", "Order not found"));
        }
    }

    // Cancel an existing order by deleting it
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String orderId = request.getParameter("orderId");
        boolean orderFound = false;

        // Search for the order by orderId and remove it if found
        for (int i = 0; i < orders.length(); i++) {
            JSONObject order = orders.getJSONObject(i);
            if (order.getString("orderId").equals(orderId)) {
                orders.remove(i);  // Remove the order from the array
                orderFound = true;
                break;
            }
        }

        if (orderFound) {
            // Save the updated orders to the Orders.json file
            try {
                String filePath = getServletContext().getRealPath("/WEB-INF/Orders.json");
                Files.write(Paths.get(filePath), orders.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.println(new JSONObject().put("status", "error").put("message", "Failed to cancel order"));
                return;
            }

            response.setStatus(HttpServletResponse.SC_OK);
            out.println(new JSONObject().put("status", "success").put("message", "Order canceled successfully"));
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.println(new JSONObject().put("status", "error").put("message", "Order not found"));
        }
    }

    public void destroy() {
        // Cleanup code if needed
    }
}

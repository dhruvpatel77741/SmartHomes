package com.smarthomes.csp584.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

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
            users = new JSONArray();
        }
    }

    private String getUsername(int userId) {
        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            if (user.getInt("_id") == userId) {
                return user.getString("name");
            }
        }
        return "Unknown";
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
        JSONObject requestBodyJson = new JSONObject(requestBody);

        int userId = requestBodyJson.getInt("userId");
        JSONObject orderData = requestBodyJson.getJSONObject("orderData");
        String checkout = requestBodyJson.getString("checkout");
        String paymentMode = requestBodyJson.getString("paymentMode");
        JSONObject paymentDetails = requestBodyJson.getJSONObject("paymentDetails");
        JSONObject address = requestBodyJson.getJSONObject("address");
        String status = "Order Placed";

        String orderId = UUID.randomUUID().toString();

        JSONObject newOrder = new JSONObject();
        newOrder.put("orderId", orderId);
        newOrder.put("userId", userId);
        newOrder.put("orderData", orderData);
        newOrder.put("checkout", checkout);
        newOrder.put("paymentMode", paymentMode);
        newOrder.put("paymentDetails", paymentDetails);
        newOrder.put("address", address);
        newOrder.put("status", status);

        orders.put(newOrder);

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

    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
        JSONObject requestBodyJson = new JSONObject(requestBody);

        String orderId = requestBodyJson.getString("orderId");
        String newStatus = requestBodyJson.getString("status");

        boolean orderFound = false;

        for (int i = 0; i < orders.length(); i++) {
            JSONObject order = orders.getJSONObject(i);
            if (order.getString("orderId").equals(orderId)) {
                order.put("status", newStatus);
                orderFound = true;
                break;
            }
        }

        if (orderFound) {
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

    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String orderId = request.getParameter("orderId");
        boolean orderFound = false;

        for (int i = 0; i < orders.length(); i++) {
            JSONObject order = orders.getJSONObject(i);
            if (order.getString("orderId").equals(orderId)) {
                orders.remove(i);
                orderFound = true;
                break;
            }
        }

        if (orderFound) {
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
}

package com.smarthomes.csp584.servlets;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;

@WebServlet(name = "updateUserServlet", value = "/updateUser")
public class UpdateUserServlet extends HttpServlet {

    private JSONArray users;

    public void init() {
        // Load the Users.json file during servlet initialization
        try {
            String filePath = getServletContext().getRealPath("/WEB-INF/Users.json");
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            users = new JSONArray(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Get request body (id, address, credit card details)
        String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
        JSONObject requestBodyJson = new JSONObject(requestBody);

        int userId = requestBodyJson.getInt("id");

        // Check if phone, address, and credit card exist, otherwise keep existing values
        String phone = requestBodyJson.has("phone") ? requestBodyJson.getString("phone") : null;
        JSONObject address = requestBodyJson.optJSONObject("address");
        JSONObject creditCard = requestBodyJson.optJSONObject("creditCard");

        boolean userFound = false;

        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);

            if (user.getInt("_id") == userId) {
                // Only update the fields that are provided
                if (phone != null) {
                    user.put("phone", phone);
                }

                if (address != null) {
                    if (address.has("addressLine1")) user.put("addressLine1", address.getString("addressLine1"));
                    if (address.has("addressLine2")) user.put("addressLine2", address.getString("addressLine2"));
                    if (address.has("city")) user.put("city", address.getString("city"));
                    if (address.has("state")) user.put("state", address.getString("state"));
                    if (address.has("zipCode")) user.put("zipCode", address.getString("zipCode"));
                }

                if (creditCard != null) {
                    if (creditCard.has("creditCardNumber")) user.put("creditCardNumber", creditCard.getString("creditCardNumber"));
                    if (creditCard.has("expiryDate")) user.put("expiryDate", creditCard.getString("expiryDate"));
                    if (creditCard.has("cvv")) user.put("cvv", creditCard.getString("cvv"));
                }

                userFound = true;
                break;
            }
        }

        if (userFound) {
            // Save the updated JSON array back to the file
            try {
                String filePath = getServletContext().getRealPath("/WEB-INF/Users.json");
                Files.write(Paths.get(filePath), users.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.println(new JSONObject().put("status", "error").put("message", "Failed to update user"));
                return;
            }

            response.setStatus(HttpServletResponse.SC_OK);
            out.println(new JSONObject().put("status", "success").put("message", "User updated successfully"));
        } else {
            // Set response status to 404 Not Found if user is not found
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.println(new JSONObject().put("status", "error").put("message", "User not found"));
        }
    }

    // Add delete functionality
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Get the userId from the request
        String userIdParam = request.getParameter("id");
        if (userIdParam == null || userIdParam.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println(new JSONObject().put("status", "error").put("message", "User ID is required"));
            return;
        }

        int userId = Integer.parseInt(userIdParam);

        boolean userFound = false;

        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);

            if (user.getInt("_id") == userId) {
                users.remove(i);
                userFound = true;
                break;
            }
        }

        if (userFound) {
            // Save the updated JSON array back to the file
            try {
                String filePath = getServletContext().getRealPath("/WEB-INF/Users.json");
                Files.write(Paths.get(filePath), users.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.println(new JSONObject().put("status", "error").put("message", "Failed to delete user"));
                return;
            }

            response.setStatus(HttpServletResponse.SC_OK);
            out.println(new JSONObject().put("status", "success").put("message", "User deleted successfully"));
        } else {
            // Set response status to 404 Not Found if user is not found
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.println(new JSONObject().put("status", "error").put("message", "User not found"));
        }
    }

    public void destroy() {
        // Cleanup code (if needed)
    }
}

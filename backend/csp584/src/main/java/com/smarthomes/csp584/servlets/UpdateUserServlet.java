package com.smarthomes.csp584.servlets;

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

@WebServlet(name = "updateUserServlet", value = "/updateUser")
public class UpdateUserServlet extends HttpServlet {

    private JSONArray users;

    public void init() {
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

        String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
        JSONObject requestBodyJson = new JSONObject(requestBody);

        int userId = requestBodyJson.getInt("id");
        String phone = requestBodyJson.optString("phone", null);
        JSONObject address = requestBodyJson.optJSONObject("address");
        JSONObject creditCard = requestBodyJson.optJSONObject("creditCard");

        boolean userFound = false;

        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);

            if (user.getInt("_id") == userId) {
                if (phone != null) {
                    user.put("phone", phone);
                }

                if (address != null) {
                    if (address.has("addressLine1"))
                        user.put("addressLine1", address.getString("addressLine1"));
                    if (address.has("addressLine2"))
                        user.put("addressLine2", address.getString("addressLine2"));
                    if (address.has("city"))
                        user.put("city", address.getString("city"));
                    if (address.has("state"))
                        user.put("state", address.getString("state"));
                    if (address.has("zipCode"))
                        user.put("zipCode", address.getString("zipCode"));
                }

                if (creditCard != null) {
                    if (creditCard.has("creditCardNumber"))
                        user.put("creditCardNumber", creditCard.getString("creditCardNumber"));
                    if (creditCard.has("expiryDate"))
                        user.put("expiryDate", creditCard.getString("expiryDate"));
                    if (creditCard.has("cvv"))
                        user.put("cvv", creditCard.getString("cvv"));
                }

                userFound = true;
                break;
            }
        }

        if (userFound) {
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
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.println(new JSONObject().put("status", "error").put("message", "User not found"));
        }
    }

    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

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
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.println(new JSONObject().put("status", "error").put("message", "User not found"));
        }
    }
}

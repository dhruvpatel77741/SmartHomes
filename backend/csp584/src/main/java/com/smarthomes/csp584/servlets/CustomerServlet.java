package com.smarthomes.csp584.servlets;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;

@WebServlet(name = "customerServlet", value = "/customers")
public class CustomerServlet extends HttpServlet {

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

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Create a JSONArray to hold customers
        JSONArray customers = new JSONArray();

        // Filter users with userType "Customer"
        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            if ("Customer".equals(user.getString("userType"))) {
                customers.put(user);
            }
        }

        // Send the response
        out.println(customers.toString());
    }

    public void destroy() {
        // Cleanup code (if needed)
    }
}

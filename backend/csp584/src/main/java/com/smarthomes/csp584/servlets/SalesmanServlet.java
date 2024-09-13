package com.smarthomes.csp584.servlets;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;

@WebServlet(name = "salesmanServlet", value = "/salesman")
public class SalesmanServlet extends HttpServlet {

    private String filePath;

    public void init() {
        // Store the file path in init() but load the content in doGet()
        filePath = getServletContext().getRealPath("/resources/Users.json");
    }

    // Method to load users from the file
    private JSONArray loadUsers() throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        return new JSONArray(content);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Reload users every time a request is made
        JSONArray users = loadUsers();
        JSONArray salesmans = new JSONArray();

        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            if ("Salesman".equals(user.getString("userType"))) {
                salesmans.put(user);
            }
        }

        out.println(salesmans.toString());
    }
}

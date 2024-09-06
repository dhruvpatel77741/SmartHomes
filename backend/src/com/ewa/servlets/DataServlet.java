package com.ewa.servlets;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.json.JSONArray;

public class DataServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set response type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Get the parameter to determine which JSON file to serve
        String fileType = request.getParameter("type");

        // Default to Users.json if no parameter is provided
        String jsonFileName = "Users.json";
        if ("products".equalsIgnoreCase(fileType)) {
            jsonFileName = "Products.json";
        }

        // Read the appropriate JSON file from the resources folder
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(jsonFileName);
        if (inputStream == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Data file not found.");
            return;
        }

        // Convert InputStream to String
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        reader.close();

        // Convert string to JSON Array and return it in the response
        JSONArray jsonArray = new JSONArray(stringBuilder.toString());
        PrintWriter out = response.getWriter();
        out.print(jsonArray.toString());
        out.flush();
    }
}

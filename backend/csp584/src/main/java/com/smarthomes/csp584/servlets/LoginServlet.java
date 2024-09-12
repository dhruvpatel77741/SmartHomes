package com.smarthomes.csp584.servlets;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;

@WebServlet(name = "loginServlet", value = "/login")
public class LoginServlet extends HttpServlet {

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

        // Get request body (username, password, usertype)
        String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
        JSONObject requestBodyJson = new JSONObject(requestBody);

        String username = requestBodyJson.getString("username");
        String password = requestBodyJson.getString("password");
        String userType = requestBodyJson.getString("userType");

        // Check the credentials in the JSON file
        JSONObject matchedUser = null;
        boolean typeMismatch = false;

        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            if (user.getString("username").equals(username) && user.getString("password").equals(password)) {
                matchedUser = user;
                if (!user.getString("userType").equals(userType)) {
                    typeMismatch = true;
                }
                break;
            }
        }

        // Return appropriate response with correct status codes
        if (matchedUser != null && !typeMismatch) {
            // User found and userType matches
            response.setStatus(HttpServletResponse.SC_OK); // 200 OK
            out.println(new JSONObject().put("status", "success").put("user", matchedUser));
        } else if (matchedUser != null && typeMismatch) {
            // User found, but userType doesn't match
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            out.println(new JSONObject().put("status", "error").put("message", "Usertype does not match"));
        } else {
            // Invalid username or password
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
            out.println(new JSONObject().put("status", "error").put("message", "Invalid username or password"));
        }
    }


    public void destroy() {
        // Cleanup code (if needed)
    }
}

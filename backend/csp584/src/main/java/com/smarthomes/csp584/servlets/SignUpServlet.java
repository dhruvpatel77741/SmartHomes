package com.smarthomes.csp584.servlets;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;

@WebServlet(name = "signUpServlet", value = "/signup")
public class SignUpServlet extends HttpServlet {

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

        // Get request body (name, username, password)
        String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
        JSONObject requestBodyJson = new JSONObject(requestBody);

        String name = requestBodyJson.getString("name");
        String username = requestBodyJson.getString("username");
        String password = requestBodyJson.getString("password");

        // Check if the username already exists
        boolean userExists = false;
        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            if (user.getString("username").equals(username)) {
                userExists = true;
                break;
            }
        }

        if (userExists) {
            // Username already exists
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println(new JSONObject().put("status", "error").put("message", "Username already exists"));
        } else {
            // Create new user
            int newId = 1;
            if (users.length() > 0) {
                newId = users.getJSONObject(users.length() - 1).getInt("_id") + 1;
            }

            JSONObject newUser = new JSONObject()
                    .put("_id", newId)
                    .put("name", name)
                    .put("username", username)
                    .put("password", password)
                    .put("userType", "Customer");

            // Add new user to JSON array
            users.put(newUser);

            // Save updated JSON array to file
            try (FileWriter fileWriter = new FileWriter(getServletContext().getRealPath("/WEB-INF/Users.json"), false)) {
                fileWriter.write(users.toString());
                fileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
                out.println(new JSONObject().put("status", "error").put("message", "Failed to save user"));
                return;
            }

            // Respond with success
            out.println(new JSONObject().put("status", "success").put("message", "User registered successfully"));
        }
    }


    public void destroy() {
        // Cleanup code (if needed)
    }
}

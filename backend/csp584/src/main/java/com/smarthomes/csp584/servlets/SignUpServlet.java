package com.smarthomes.csp584.servlets;

import java.io.FileWriter;
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

@WebServlet(name = "signUpServlet", value = "/signup")
public class SignUpServlet extends HttpServlet {

    private JSONArray users;

    public void init() {
        try {
            String filePath = getServletContext().getRealPath("/resources/Users.json");
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

        String name = requestBodyJson.getString("name");
        String username = requestBodyJson.getString("username");
        String password = requestBodyJson.getString("password");
        String userType = requestBodyJson.getString("userType");

        boolean userExists = false;
        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            if (user.getString("username").equals(username)) {
                userExists = true;
                break;
            }
        }

        if (userExists) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println(new JSONObject().put("status", "error").put("message", "Username already exists"));
        } else {
            int newId = (users.length() > 0) ? users.getJSONObject(users.length() - 1).getInt("_id") + 1 : 1;

            JSONObject newUser = new JSONObject()
                    .put("_id", newId)
                    .put("name", name)
                    .put("username", username)
                    .put("password", password)
                    .put("userType", userType);

            users.put(newUser);

            try (FileWriter fileWriter = new FileWriter(getServletContext().getRealPath("/resources/Users.json"), false)) {
                fileWriter.write(users.toString());
            } catch (IOException e) {
                e.printStackTrace();
                out.println(new JSONObject().put("status", "error").put("message", "Failed to save user"));
                return;
            }

            out.println(new JSONObject().put("status", "success").put("message", "User registered successfully"));
        }
    }
}

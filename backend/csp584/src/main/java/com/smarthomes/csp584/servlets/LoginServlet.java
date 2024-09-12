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

@WebServlet(name = "loginServlet", value = "/login")
public class LoginServlet extends HttpServlet {

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

        String username = requestBodyJson.getString("username");
        String password = requestBodyJson.getString("password");
        String userType = requestBodyJson.getString("userType");

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

        if (matchedUser != null && !typeMismatch) {
            response.setStatus(HttpServletResponse.SC_OK);
            out.println(new JSONObject().put("status", "success").put("user", matchedUser));
        } else if (matchedUser != null && typeMismatch) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.println(new JSONObject().put("status", "error").put("message", "Usertype does not match"));
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println(new JSONObject().put("status", "error").put("message", "Invalid username or password"));
        }
    }
}

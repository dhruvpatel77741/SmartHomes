package com.smarthomes.csp584.servlets;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import org.json.JSONArray;
import java.nio.file.Files;
import java.nio.file.Paths;

@WebServlet(name = "userServlet", value = "/users")
public class UserServlet extends HttpServlet {

    private String filePath;

    public void init() {
        filePath = getServletContext().getRealPath("/resources/Users.json");
    }

    private JSONArray loadUsers() throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        return new JSONArray(content);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONArray users = loadUsers();
        out.println(users.toString());
    }
}

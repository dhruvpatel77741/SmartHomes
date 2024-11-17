package com.smarthomes.csp584.servlets;

import com.smarthomes.csp584.utils.MySQLDataStoreUtilities;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Random;

@WebServlet("/ticket")
@MultipartConfig
public class TicketServlet extends HttpServlet {

    private static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");

    private static final String instructionPrompt = "You are a customer service assistant for a delivery service, equipped to analyze images of packages. " +
            "If a package appears damaged in the image, automatically process a refund according to policy. " +
            "If the package looks wet, initiate a replacement. " +
            "If the package appears normal and not damaged, escalate to agent. " +
            "For any other issues or unclear images, escalate to agent. You must always use tools!";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String orderId = req.getParameter("orderId");
        String description = req.getParameter("description");
        String userIdString = req.getParameter("userId");
        Part imagePart = req.getPart("image");

        try (PrintWriter out = resp.getWriter()) {

            if (!isOrderDelivered(orderId)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(new JSONObject().put("status", "error").put("message", "Order not delivered yet").toString());
                return;
            }

            // Validate userId
            if (userIdString == null || userIdString.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(new JSONObject().put("status", "error").put("message", "User ID is required").toString());
                return;
            }

            int userId;
            try {
                userId = Integer.parseInt(userIdString);
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(new JSONObject().put("status", "error").put("message", "Invalid User ID format").toString());
                return;
            }

            InputStream inputStream = imagePart.getInputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            String imageBase64 = Base64.getEncoder().encodeToString(outputStream.toByteArray());

            String decision = callOpenAiApi(imageBase64);
            int ticketNumber = createTicket(orderId, description, decision, userId); // Pass userId to createTicket

            resp.setStatus(HttpServletResponse.SC_CREATED);
            JSONObject responseJson = new JSONObject();
            responseJson.put("status", "success");
            responseJson.put("ticketNumber", ticketNumber);
            responseJson.put("decision", decision);
            out.write(responseJson.toString());
            out.flush();
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(new JSONObject().put("status", "error").put("message", "Error processing request").toString());
            e.printStackTrace();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(new JSONObject().put("status", "error").put("message", "An unexpected error occurred").toString());
            e.printStackTrace();
        }
    }

    private boolean isOrderDelivered(String orderId) throws SQLException {
        String query = "SELECT status FROM orders WHERE orderId = ?";
        try (Connection conn = MySQLDataStoreUtilities.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String status = rs.getString("status");
                return "delivered".equalsIgnoreCase(status);
            }
        }
        return false;
    }

    private String callOpenAiApi(String imageBase64) throws IOException {
        URL url = new URL("https://api.openai.com/v1/chat/completions");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + OPENAI_API_KEY);
        connection.setDoOutput(true);

        JSONObject payload = new JSONObject();
        payload.put("model", "gpt-4o-mini");
        payload.put("messages", new JSONArray()
                .put(new JSONObject().put("role", "system").put("content", instructionPrompt))
                .put(new JSONObject().put("role", "user").put("content", "Analyze the following image and make a decision."))
                .put(new JSONObject().put("role", "user").put("content", new JSONArray()
                        .put(new JSONObject().put("type", "image_url").put("image_url",
                                new JSONObject().put("url", "data:image/jpeg;base64," + imageBase64))))));

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = payload.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder responseBuilder = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                responseBuilder.append(responseLine.trim());
            }
            JSONObject responseJson = new JSONObject(responseBuilder.toString());
            return responseJson.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
        }
    }

    private int createTicket(String orderId, String description, String decision, int userId) throws SQLException {
        int ticketNumber = generateRandomTicketNumber();

        if (decision.length() > 255) {
            decision = decision.substring(0, 255);
        }

        String query = "INSERT INTO tickets (ticketId, orderId, description, decision, userId) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = MySQLDataStoreUtilities.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, ticketNumber);
            ps.setString(2, orderId);
            ps.setString(3, description);
            ps.setString(4, decision);
            ps.setInt(5, userId);
            ps.executeUpdate();
        }
        return ticketNumber;
    }

    private int generateRandomTicketNumber() {
        Random random = new Random();
        return 100000 + random.nextInt(900000);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String ticketId = req.getParameter("ticketId");

        try (PrintWriter out = resp.getWriter()) {
            // Validate ticket ID
            if (ticketId == null || ticketId.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(new JSONObject().put("status", "error").put("message", "Ticket ID is required").toString());
                return;
            }

            JSONObject ticketDetails = getTicketDetails(ticketId);

            if (ticketDetails == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write(new JSONObject().put("status", "error").put("message", "Ticket not found").toString());
            } else {
                resp.setStatus(HttpServletResponse.SC_OK);
                out.write(ticketDetails.toString());
            }
            out.flush();
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(new JSONObject().put("status", "error").put("message", "Error retrieving ticket data").toString());
            e.printStackTrace();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(new JSONObject().put("status", "error").put("message", "An unexpected error occurred").toString());
            e.printStackTrace();
        }
    }

    private JSONObject getTicketDetails(String ticketId) throws SQLException {
        String query = "SELECT * FROM tickets WHERE ticketId = ?";
        try (Connection conn = MySQLDataStoreUtilities.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, ticketId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                JSONObject ticketDetails = new JSONObject();
                ticketDetails.put("ticketId", rs.getInt("ticketId"));
                ticketDetails.put("orderId", rs.getString("orderId"));
                ticketDetails.put("description", rs.getString("description"));
                ticketDetails.put("decision", rs.getString("decision"));
                ticketDetails.put("userId", rs.getInt("userId"));
                ticketDetails.put("ticketDate", rs.getTimestamp("ticketDate"));
                return ticketDetails;
            }
        }
        return null;
    }

}

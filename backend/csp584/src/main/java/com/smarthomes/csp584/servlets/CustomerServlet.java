package com.smarthomes.csp584.servlets;

import com.smarthomes.csp584.models.User;
import com.smarthomes.csp584.utils.MySQLDataStoreUtilities;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "customerServlet", value = "/customers")
public class CustomerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            List<User> customers = getCustomersFromDatabase();
            JSONArray customersJsonArray = new JSONArray();
            for (User customer : customers) {
                JSONObject customerJson = new JSONObject();
                customerJson.put("id", customer.getId());
                customerJson.put("username", customer.getUsername());
                customerJson.put("name", customer.getName());
                customerJson.put("userType", customer.getUserType());
                customerJson.put("phone", customer.getPhone());
                customerJson.put("addressLine1", customer.getAddressLine1());
                customerJson.put("addressLine2", customer.getAddressLine2());
                customerJson.put("city", customer.getCity());
                customerJson.put("state", customer.getState());
                customerJson.put("zipCode", customer.getZipCode());
                customerJson.put("creditCardNumber", customer.getCreditCardNumber());
                customerJson.put("expiryDate", customer.getExpiryDate());
                customerJson.put("cvv", customer.getCvv());
                customersJsonArray.put(customerJson);
            }
            out.println(customersJsonArray.toString());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(new JSONObject().put("error", "Unable to retrieve customer data"));
            e.printStackTrace();
        }
    }

    private List<User> getCustomersFromDatabase() throws Exception {
        List<User> customers = new ArrayList<>();
        try (Connection conn = MySQLDataStoreUtilities.getConnection();
             PreparedStatement pst = conn.prepareStatement("SELECT id, username, name, userType, phone, addressLine1, addressLine2, city, state, zipCode, creditCardNumber, expiryDate, cvv FROM Users WHERE userType = 'Customer'")) {
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    User user = new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            null,
                            rs.getString("name"),
                            rs.getString("userType"),
                            rs.getString("phone"),
                            rs.getString("addressLine1"),
                            rs.getString("addressLine2"),
                            rs.getString("city"),
                            rs.getString("state"),
                            rs.getString("zipCode"),
                            rs.getString("creditCardNumber"),
                            rs.getString("expiryDate"),
                            rs.getString("cvv")
                    );
                    customers.add(user);
                }
            }
        }
        return customers;
    }
}

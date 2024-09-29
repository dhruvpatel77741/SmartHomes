package com.smarthomes.csp584.servlets;

import com.smarthomes.csp584.models.Store;
import com.smarthomes.csp584.utils.MySQLDataStoreUtilities;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "storeServlet", value = "/stores")
public class StoreServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            List<Store> stores = getStoresFromDatabase();
            JSONArray storesJsonArray = new JSONArray();
            for (Store store : stores) {
                JSONObject storeJson = new JSONObject();
                storeJson.put("storeId", store.getStoreId());
                storeJson.put("street", store.getStreet());
                storeJson.put("city", store.getCity());
                storeJson.put("state", store.getState());
                storeJson.put("zipCode", store.getZipCode());
                storesJsonArray.put(storeJson);
            }
            out.println(storesJsonArray.toString());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(new JSONObject().put("error", "Error fetching stores data"));
            e.printStackTrace();
        }
    }

    private List<Store> getStoresFromDatabase() throws Exception {
        List<Store> stores = new ArrayList<>();
        try (Connection conn = MySQLDataStoreUtilities.getConnection();
             PreparedStatement pst = conn.prepareStatement("SELECT storeId, street, city, state, zipCode FROM Stores")) {
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Store store = new Store(
                            rs.getInt("storeId"),
                            rs.getString("street"),
                            rs.getString("city"),
                            rs.getString("state"),
                            rs.getString("zipCode")
                    );
                    stores.add(store);
                }
            }
        }
        return stores;
    }
}

package com.smarthomes.csp584.servlets;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@WebServlet(name = "ProductSearchServlet", value = "/productSearch")
public class ProductSearchServlet extends HttpServlet {

    private static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");
    private static final String ELASTICSEARCH_URL = "http://localhost:9200/products_index/_search";

    private String getEmbedding(String query) throws IOException {
        String requestUrl = "https://api.openai.com/v1/embeddings";
        URL url = new URL(requestUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + OPENAI_API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        JSONObject payload = new JSONObject();
        payload.put("model", "text-embedding-3-small");
        payload.put("input", query);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.toString().getBytes());
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new java.io.InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        JSONObject jsonResponse = new JSONObject(response.toString());
        JSONArray embeddingArray = jsonResponse.getJSONArray("data").getJSONObject(0).getJSONArray("embedding");

        return embeddingArray.toString();
    }

    private JSONArray knnSearch(String embedding) throws IOException {
        URL url = new URL(ELASTICSEARCH_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        JSONObject query = new JSONObject();
        JSONObject knn = new JSONObject();
        knn.put("field", "product_vector");
        knn.put("query_vector", new JSONArray(embedding));
        knn.put("k", 5); // Fixed to 5
        knn.put("num_candidates", 100);

        query.put("knn", knn);

        // Specify only required fields
        query.put("_source", new JSONArray()
                .put("id")
                .put("name")
                .put("description")
                .put("category")
                .put("price")
                .put("specialDiscount")
                .put("discountPrice")
                .put("manufacturerRebate")
                .put("rebatePrice")
                .put("warranty")
                .put("warrantyPrice")
                .put("likes")
                .put("availableItems"));

        try (OutputStream os = conn.getOutputStream()) {
            os.write(query.toString().getBytes());
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new java.io.InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        JSONObject jsonResponse = new JSONObject(response.toString());
        return jsonResponse.getJSONObject("hits").getJSONArray("hits");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("query");

        if (query == null || query.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println(new JSONObject().put("status", "error").put("message", "Query parameter is required"));
            return;
        }

        String embedding = getEmbedding(query);
        JSONArray results = knnSearch(embedding);

        response.setContentType("application/json");
        response.getWriter().println(new JSONObject().put("status", "success").put("results", results));
    }
}

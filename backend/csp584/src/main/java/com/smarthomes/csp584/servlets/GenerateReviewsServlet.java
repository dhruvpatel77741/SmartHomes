package com.smarthomes.csp584.servlets;

import com.smarthomes.csp584.utils.MongoDBDataStoreUtilities;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

@WebServlet(name = "GenerateReviewsServlet", value = "/generateReviews")
public class GenerateReviewsServlet extends HttpServlet {

    private static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");
    private static final String[] POSITIVE_KEYWORDS = {"amazing", "reliable", "affordable", "convenient", "secure"};
    private static final String[] NEGATIVE_KEYWORDS = {"poor quality", "expensive", "unreliable", "difficult to use", "glitchy"};

    private static final String CUSTOM_PRODUCTS_FILE_PATH = System.getProperty("user.home") + "/Desktop/Assignment5/OpenAI/products.json";

    private JSONArray loadProducts() throws IOException {
        File productsFile = new File(CUSTOM_PRODUCTS_FILE_PATH);

        if (!productsFile.exists()) {
            throw new FileNotFoundException("products.json file not found at: " + CUSTOM_PRODUCTS_FILE_PATH);
        }

        try (InputStream is = new FileInputStream(productsFile)) {
            StringBuilder jsonContent = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = br.readLine()) != null) {
                    jsonContent.append(line);
                }
            }
            return new JSONArray(jsonContent.toString());
        }
    }

    private String generateReviewText(String productName, String category, String sentiment, String[] keywords) throws IOException {
        String requestUrl = "https://api.openai.com/v1/chat/completions";
        URL url = new URL(requestUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + OPENAI_API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        JSONObject payload = new JSONObject();
        payload.put("model", "gpt-3.5-turbo");
        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "system").put("content", "You are an assistant that writes product reviews."));
        String prompt = String.format(
                "Write a %s review for the product '%s' in the category '%s'. Include the following keywords: %s.",
                sentiment, productName, category, String.join(", ", keywords)
        );
        messages.put(new JSONObject().put("role", "user").put("content", prompt));
        payload.put("messages", messages);
        payload.put("max_tokens", 100);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.toString().getBytes());
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        JSONObject jsonResponse = new JSONObject(response.toString());
        return jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content").trim();
    }

    private JSONObject generateReview(JSONObject product, String sentiment) throws IOException {
        Random random = new Random();
        String[] keywords = sentiment.equals("positive") ? POSITIVE_KEYWORDS : NEGATIVE_KEYWORDS;

        boolean manufacturerRebate = product.optInt("manufacturerRebate", 0) == 1;

        JSONObject review = new JSONObject();
        review.put("productModelName", product.getString("name"));
        review.put("productCategory", product.getString("category"));
        review.put("productPrice", product.getDouble("price"));
        review.put("storeID", String.valueOf(random.nextInt(10) + 1));
        review.put("storeZip", "12345");
        review.put("storeCity", "Sample City");
        review.put("storeState", "Sample State");
        review.put("productOnSale", random.nextBoolean());
        review.put("manufacturerName", "Manufacturer " + (random.nextInt(5) + 1));
        review.put("manufacturerRebate", manufacturerRebate);
        review.put("userID", String.valueOf(random.nextInt(25) + 1));
        review.put("userAge", random.nextInt(48) + 18);
        review.put("userGender", random.nextBoolean() ? "Male" : "Female");
        review.put("userOccupation", new String[]{"Engineer", "Designer", "Teacher", "Mechanic"}[random.nextInt(4)]);
        review.put("reviewRating", sentiment.equals("positive") ? random.nextInt(2) + 4 : random.nextInt(3) + 1); // 4-5 for positive, 1-3 for negative
        review.put("reviewDate", "2024-10-01");
        review.put("reviewText", generateReviewText(product.getString("name"), product.getString("category"), sentiment, keywords));

        return review;
    }


    private void storeReviewsInMongoDB(JSONArray reviews) {
        MongoDatabase database = MongoDBDataStoreUtilities.getConnection();
        MongoCollection<Document> collection = database.getCollection("ProductReviews");

        for (int i = 0; i < reviews.length(); i++) {
            JSONObject review = reviews.getJSONObject(i);
            collection.insertOne(Document.parse(review.toString()));
        }

        MongoDBDataStoreUtilities.closeConnection();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            JSONArray products = loadProducts();
            JSONArray reviews = new JSONArray();

            for (int i = 0; i < products.length(); i++) {
                JSONObject product = products.getJSONObject(i);
                for (int j = 0; j < 5; j++) {
                    String sentiment = new Random().nextBoolean() ? "positive" : "negative";
                    reviews.put(generateReview(product, sentiment));
                }
            }

            storeReviewsInMongoDB(reviews);

            response.setContentType("application/json");
            response.getWriter().println(new JSONObject().put("status", "success").put("reviews", reviews));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println(new JSONObject().put("status", "error").put("message", e.getMessage()));
        }
    }
}

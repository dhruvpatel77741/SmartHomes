package com.smarthomes.csp584.servlets;

import com.smarthomes.csp584.models.ProductReview;
import com.smarthomes.csp584.utils.MongoDBDataStoreUtilities;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "ReviewServlet", value = "/submitReview")
public class ReviewServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Parse the JSON request body
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                requestBody.append(line);
            }
            JSONObject jsonBody = new JSONObject(requestBody.toString());

            // Extract data from JSON
            ProductReview review = new ProductReview(
                    jsonBody.getString("productModelName"),
                    jsonBody.getString("productCategory"),
                    jsonBody.getDouble("productPrice"),
                    jsonBody.getString("storeID"),
                    jsonBody.getString("storeZip"),
                    jsonBody.getString("storeCity"),
                    jsonBody.getString("storeState"),
                    jsonBody.getBoolean("productOnSale"),
                    jsonBody.getString("manufacturerName"),
                    jsonBody.getBoolean("manufacturerRebate"),
                    jsonBody.getString("userID"),
                    jsonBody.getInt("userAge"),
                    jsonBody.getString("userGender"),
                    jsonBody.getString("userOccupation"),
                    jsonBody.getInt("reviewRating"),
                    jsonBody.getString("reviewDate"),
                    jsonBody.getString("reviewText")
            );

            // Connect to MongoDB
            MongoDatabase db = MongoDBDataStoreUtilities.getConnection();
            if (db == null) {
                throw new Exception("Failed to connect to MongoDB");
            }

            // Insert review into MongoDB collection
            MongoCollection<Document> reviewCollection = db.getCollection("ProductReviews");
            Document reviewDoc = new Document("productModelName", review.getProductModelName())
                    .append("productCategory", review.getProductCategory())
                    .append("productPrice", review.getProductPrice())
                    .append("storeID", review.getStoreID())
                    .append("storeZip", review.getStoreZip())
                    .append("storeCity", review.getStoreCity())
                    .append("storeState", review.getStoreState())
                    .append("productOnSale", review.isProductOnSale())
                    .append("manufacturerName", review.getManufacturerName())
                    .append("manufacturerRebate", review.isManufacturerRebate())
                    .append("userID", review.getUserID())
                    .append("userAge", review.getUserAge())
                    .append("userGender", review.getUserGender())
                    .append("userOccupation", review.getUserOccupation())
                    .append("reviewRating", review.getReviewRating())
                    .append("reviewDate", review.getReviewDate())
                    .append("reviewText", review.getReviewText());

            reviewCollection.insertOne(reviewDoc);

            // Respond with success
            JSONObject responseJson = new JSONObject()
                    .put("status", "success")
                    .put("message", "Review submitted successfully.");
            out.println(responseJson.toString());

        } catch (Exception e) {
            // Handle exceptions
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject errorJson = new JSONObject()
                    .put("status", "error")
                    .put("message", e.getMessage());
            out.println(errorJson.toString());
        }
    }
}

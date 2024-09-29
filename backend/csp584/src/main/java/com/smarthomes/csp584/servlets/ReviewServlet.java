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

        String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
        JSONObject jsonBody = new JSONObject(requestBody);

        String productModelName = jsonBody.getString("productModelName");
        String productCategory = jsonBody.getString("productCategory");
        double productPrice = jsonBody.getDouble("productPrice");
        String storeID = jsonBody.getString("storeID");
        String storeZip = jsonBody.getString("storeZip");
        String storeCity = jsonBody.getString("storeCity");
        String storeState = jsonBody.getString("storeState");
        boolean productOnSale = jsonBody.getBoolean("productOnSale");
        String manufacturerName = jsonBody.getString("manufacturerName");
        boolean manufacturerRebate = jsonBody.getBoolean("manufacturerRebate");
        String userID = jsonBody.getString("userID");
        int userAge = jsonBody.getInt("userAge");
        String userGender = jsonBody.getString("userGender");
        String userOccupation = jsonBody.getString("userOccupation");
        int reviewRating = jsonBody.getInt("reviewRating");
        String reviewDate = jsonBody.getString("reviewDate");
        String reviewText = jsonBody.getString("reviewText");

        ProductReview review = new ProductReview(productModelName, productCategory, productPrice, storeID, storeZip,
                storeCity, storeState, productOnSale, manufacturerName,
                manufacturerRebate, userID, userAge, userGender, userOccupation,
                reviewRating, reviewDate, reviewText);

        MongoDatabase db = MongoDBDataStoreUtilities.getConnection();
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

        out.println(new JSONObject().put("status", "success").put("message", "Review submitted successfully."));
    }
}

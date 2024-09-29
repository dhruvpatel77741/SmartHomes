package com.smarthomes.csp584.servlets;

import com.smarthomes.csp584.utils.MongoDBDataStoreUtilities;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "ReviewGetServlet", value = "/getAllReviews")
public class ReviewGetServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        MongoDatabase db = MongoDBDataStoreUtilities.getConnection();
        MongoCollection<Document> reviewCollection = db.getCollection("ProductReviews");

        MongoCursor<Document> cursor = reviewCollection.find().iterator();

        JSONArray reviewsArray = new JSONArray();

        try {
            while (cursor.hasNext()) {
                Document doc = cursor.next();

                JSONObject reviewJson = new JSONObject();
                reviewJson.put("productModelName", doc.getString("productModelName"));
                reviewJson.put("productCategory", doc.getString("productCategory"));
                reviewJson.put("productPrice", doc.getDouble("productPrice"));
                reviewJson.put("storeID", doc.getString("storeID"));
                reviewJson.put("storeZip", doc.getString("storeZip"));
                reviewJson.put("storeCity", doc.getString("storeCity"));
                reviewJson.put("storeState", doc.getString("storeState"));
                reviewJson.put("productOnSale", doc.getBoolean("productOnSale"));
                reviewJson.put("manufacturerName", doc.getString("manufacturerName"));
                reviewJson.put("manufacturerRebate", doc.getBoolean("manufacturerRebate"));
                reviewJson.put("userID", doc.getString("userID"));
                reviewJson.put("userAge", doc.getInteger("userAge"));
                reviewJson.put("userGender", doc.getString("userGender"));
                reviewJson.put("userOccupation", doc.getString("userOccupation"));
                reviewJson.put("reviewRating", doc.getInteger("reviewRating"));
                reviewJson.put("reviewDate", doc.getString("reviewDate"));
                reviewJson.put("reviewText", doc.getString("reviewText"));

                reviewsArray.put(reviewJson);
            }
        } finally {
            cursor.close();
        }

        JSONObject responseJson = new JSONObject();
        responseJson.put("status", "success");
        responseJson.put("reviews", reviewsArray);

        out.println(responseJson.toString());
    }
}

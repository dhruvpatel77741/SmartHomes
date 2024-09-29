package com.smarthomes.csp584.utils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.MongoException;

public class MongoDBDataStoreUtilities {

    private static final String MONGO_URI = "mongodb://localhost:27017";
    private static final String DB_NAME = "SmartHomesDB";

    private static MongoClient mongoClient = null;

    public static MongoDatabase getConnection() {
        try {
            if (mongoClient == null) {
                mongoClient = MongoClients.create(MONGO_URI);
            }
            return mongoClient.getDatabase(DB_NAME);
        } catch (MongoException e) {
            System.err.println("Error connecting to MongoDB: " + e.getMessage());
            throw e;
        }
    }

    public static void closeConnection() {
        try {
            if (mongoClient != null) {
                mongoClient.close();
                mongoClient = null;
            }
        } catch (MongoException e) {
            System.err.println("Error closing MongoDB connection: " + e.getMessage());
        }
    }
}

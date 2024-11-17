package com.smarthomes.csp584.utils;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.util.concurrent.TimeUnit;

public class MongoDBDataStoreUtilities {

    private static final String MONGO_URI = "mongodb://localhost:27017";
    private static final String DB_NAME = "SmartHomesDB";
    private static MongoClient mongoClient = null;

    /**
     * Get the MongoDatabase connection
     *
     * @return MongoDatabase instance
     */
    public static synchronized MongoDatabase getConnection() {
        try {
            if (mongoClient == null) {
                // Build MongoClient with settings for robustness
                MongoClientSettings settings = MongoClientSettings.builder()
                        .applyToClusterSettings(builder ->
                                builder.hosts(java.util.Collections.singletonList(new ServerAddress("localhost", 27017))))
                        .applyToConnectionPoolSettings(builder ->
                                builder.maxConnectionIdleTime(10, TimeUnit.MINUTES))
                        .build();
                mongoClient = MongoClients.create(settings);
            }
            return mongoClient.getDatabase(DB_NAME);
        } catch (MongoException e) {
            System.err.println("Error connecting to MongoDB: " + e.getMessage());
            throw e; // Rethrow exception for handling upstream
        }
    }

    /**
     * Close the MongoDB connection
     */
    public static synchronized void closeConnection() {
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

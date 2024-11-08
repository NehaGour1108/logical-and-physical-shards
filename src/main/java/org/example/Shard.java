package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Shard {

    // Define the URLs for multiple physical shards (in-memory H2 databases)
    private static final String[] shardUrls = {
            "jdbc:h2:mem:shard1;DB_CLOSE_DELAY=-1;MODE=MySQL", // Physical Shard 1
            "jdbc:h2:mem:shard2;DB_CLOSE_DELAY=-1;MODE=MySQL"  // Physical Shard 2
    };

    public static void main(String[] args) {
        // Step 1: Setup Shards (Create tables)
        setupShards();

        // Step 2: Insert Users (Split by ID Logic: Odd or Even)
        List<int[]> users = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            users.add(new int[]{i, 20 + i}); // {ID, Age}
        }
        insertUsersIntoShards(users);

        // Step 3: Query Data from Each Shard
        queryDataFromShards();
    }

    // Method to Setup Shards (Create tables in each physical shard)
    private static void setupShards() {
        System.out.println("Setting up shards...");
        for (int i = 0; i < shardUrls.length; i++) {
            try (Connection conn = DriverManager.getConnection(shardUrls[i], "sa", "")) {
                Statement stmt = conn.createStatement();
                stmt.execute("CREATE TABLE IF NOT EXISTS users (ID INT PRIMARY KEY, Age INT)");
                System.out.println("Shard " + (i + 1) + " setup complete.");
            } catch (Exception e) {
                System.err.println("Error setting up Shard " + (i + 1) + ": " + e.getMessage());
            }
        }
    }

    // Method to Insert Users into Shards (Distribute users by odd/even IDs)
    private static void insertUsersIntoShards(List<int[]> users) {
        System.out.println("Inserting data into shards...");
        for (int[] user : users) {
            int userId = user[0];
            int age = user[1];
            String shardUrl = (userId % 2 == 0) ? shardUrls[1] : shardUrls[0]; // Even IDs go to shard2, Odd to shard1
            try (Connection conn = DriverManager.getConnection(shardUrl, "sa", "")) {
                Statement stmt = conn.createStatement();
                stmt.execute("INSERT INTO users (ID, Age) VALUES (" + userId + ", " + age + ")");
                System.out.println("Inserted User " + userId + " into Shard " + (userId % 2 == 0 ? 2 : 1));
            } catch (Exception e) {
                System.err.println("Error inserting User " + userId + ": " + e.getMessage());
            }
        }
    }

    // Method to Query Data from All Shards
    private static void queryDataFromShards() {
        System.out.println("Querying data from all shards...");
        for (int i = 0; i < shardUrls.length; i++) {
            try (Connection conn = DriverManager.getConnection(shardUrls[i], "sa", "")) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM users");
                System.out.println("Shard " + (i + 1) + " data:");
                while (rs.next()) {
                    System.out.println("User ID: " + rs.getInt("ID") + ", Age: " + rs.getInt("Age"));
                }
            } catch (Exception e) {
                System.err.println("Error querying Shard " + (i + 1) + ": " + e.getMessage());
            }
        }
    }
}

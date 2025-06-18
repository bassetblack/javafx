package com.example.demo3;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseUtil {
    private static final String DB_URL = "jdbc:sqlite:src/main/resources/db/market.db";


    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        String createUserTableSQL = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                role TEXT NOT NULL
            );
        """;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(createUserTableSQL)) {
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static Optional<User> authenticateUser(String username, String password) {
        String query = "SELECT id, username, role FROM users WHERE username = ? AND password = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String role = rs.getString("role");
                return Optional.of(new User(id, username, role));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


    public static boolean insertUser(String username, String password, String role) {
        String insertSQL = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String barcodesString = rs.getString("barcodes");

                // Deserialize the barcodes string into a List<String>
                List<String> barcodes = barcodesString == null || barcodesString.isEmpty()
                        ? List.of()
                        : List.of(barcodesString.split(","));

                String name = rs.getString("name");
                int quantity = rs.getInt("quantity");
                double buyingPrice = rs.getDouble("buying_price");


                String expiryDateString = rs.getString("expiry_date");
                LocalDate expiryDate = expiryDateString == null || expiryDateString.isEmpty()
                        ? null
                        : LocalDate.parse(expiryDateString);

                double unitPrice = rs.getDouble("unit_price");
                double price2 = rs.getDouble("price2");
                double price3 = rs.getDouble("price3");


                products.add(new Product(id, barcodes, name, quantity, buyingPrice, expiryDate, unitPrice, price2, price3));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }
    public static boolean deleteProduct(int productId) {
        String deleteSQL = "DELETE FROM products WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {


            pstmt.setInt(1, productId);


            int rowsAffected = pstmt.executeUpdate();


            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static int insertProduct(Product product) {
        String insertSQL = """
        INSERT INTO products (barcodes, name, quantity, buying_price, expiry_date, unit_price, price2, price3)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?);
    """;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {


            String barcodesString = String.join(",", product.getBarcodes());


            pstmt.setString(1, barcodesString);
            pstmt.setString(2, product.getName());
            pstmt.setInt(3, product.getQuantity());
            pstmt.setDouble(4, product.getBuyingPrice());
            pstmt.setString(5, product.getExpiryDate().toString());
            pstmt.setDouble(6, product.getUnitPrice());
            pstmt.setDouble(7, product.getPrice2());
            pstmt.setDouble(8, product.getPrice3());

            int rowsAffected = pstmt.executeUpdate();


            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // Return -1 if insertion fails or no ID is generated
    }

    public static boolean updateProduct(Product product) {
        String updateSQL = """
        UPDATE products
        SET barcodes = ?, name = ?, quantity = ?, buying_price = ?, expiry_date = ?, unit_price = ?, price2 = ?, price3 = ?
        WHERE id = ?;
    """;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {


            String barcodesString = String.join(",", product.getBarcodes());


            pstmt.setString(1, barcodesString);
            pstmt.setString(2, product.getName());
            pstmt.setInt(3, product.getQuantity());
            pstmt.setDouble(4, product.getBuyingPrice());
            pstmt.setString(5, product.getExpiryDate().toString());
            pstmt.setDouble(6, product.getUnitPrice());
            pstmt.setDouble(7, product.getPrice2());
            pstmt.setDouble(8, product.getPrice3());
            pstmt.setInt(9, product.getId());


            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
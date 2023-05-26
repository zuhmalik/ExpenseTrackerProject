/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package expensetracker;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class ExpenseTracker {

    private static final String DB_USERNAME = "zuhmalik";
    private static final String DB_PASSWORD = "Barshapool_296";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/expense_tracker";

    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            createTable(connection);
            displayMenu(connection);
            connection.close();
        } catch (SQLException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private static void createTable(Connection connection) throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS expenses ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "date DATE,"
                + "amount DOUBLE,"
                + "category VARCHAR(255),"
                + "description VARCHAR(255))";

        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
        statement.close();
    }

    private static void displayMenu(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Expense Tracker");
            System.out.println("1. Add Expense");
            System.out.println("2. Generate Report");
            System.out.println("3. Delete Expense");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addExpense(connection, scanner);
                    break;
                case 2:
                    generateReport(connection);
                    break;
                case 3:
                    deleteExpense(connection, scanner);
                    break;
                case 4:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void addExpense(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter date (MM/DD/YYYY): ");
        String dateString = scanner.nextLine();
        System.out.print("Enter amount: $");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter category: ");
        String category = scanner.nextLine();
        System.out.print("Enter description: ");
        String description = scanner.nextLine();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        java.util.Date date;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please try again.");
            return;
        }

        String query = "INSERT INTO expenses (date, amount, category, description) VALUES (?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setDate(1, new java.sql.Date(date.getTime()));
        statement.setDouble(2, amount);
        statement.setString(3, category);
        statement.setString(4, description);
        statement.executeUpdate();
        statement.close();

        System.out.println("Expense added successfully!");
    }

    private static void generateReport(Connection connection) throws SQLException {
        String query = "SELECT * FROM expenses";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        System.out.println("Expense Report");
        System.out.println("ID   Date        Amount      Category               Description");
        System.out.println("---------------------------------------------------------------");

        double totalAmount = 0.0;

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            Date date = resultSet.getDate("date");
            double amount = resultSet.getDouble("amount");
            String category = resultSet.getString("category");
            String description = resultSet.getString("description");

            totalAmount += amount;

            System.out.printf("%-4d %-11s $%-10.2f %-20s %s\n", id, date, amount, category, description);
        }

        resultSet.close();
        statement.close();

        System.out.println("---------------------------------------------------------------");
        System.out.printf("Total: $%.2f\n", totalAmount);
    }

    private static void deleteExpense(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter the ID of the expense to delete: ");
        int expenseId = scanner.nextInt();
        scanner.nextLine();

        String query = "DELETE FROM expenses WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, expenseId);
        int rowsDeleted = statement.executeUpdate();
        statement.close();

        if (rowsDeleted > 0) {
            System.out.println("Expense deleted successfully!");
        } else {
            System.out.println("No expense found with the provided ID.");
        }
    }
}

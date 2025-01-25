import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SimpleForm {
    private JFrame frame;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField bookBroughtNumberField;
    private JTextField registrationNumberField;
    private JComboBox<String> genderDropdown;
    private JTextField collegeNameField;
    private JButton submitButton;
    private JButton cancelButton;

    // JDBC variables
    private Connection connection;

    public SimpleForm() {
        // Create the frame
        frame = new JFrame("Simple Form");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(8, 2, 5, 5));

        // Create and add form components
        frame.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        frame.add(firstNameField);

        frame.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        frame.add(lastNameField);

        frame.add(new JLabel("Book Brought Number:"));
        bookBroughtNumberField = new JTextField();
        frame.add(bookBroughtNumberField);

        frame.add(new JLabel("Registration Number:"));
        registrationNumberField = new JTextField();
        frame.add(registrationNumberField);

        frame.add(new JLabel("Gender:"));
        String[] genders = { "Select Gender", "Male", "Female", "Other" };
        genderDropdown = new JComboBox<>(genders);
        frame.add(genderDropdown);

        frame.add(new JLabel("College Name:"));
        collegeNameField = new JTextField();
        frame.add(collegeNameField);

        // Submit and Cancel buttons
        submitButton = new JButton("Submit");
        frame.add(submitButton);

        cancelButton = new JButton("Cancel");
        frame.add(cancelButton);

        // Add action listeners
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                submitForm();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelForm();
            }
        });

        // Initialize MySQL connection
        initializeDatabaseConnection();

        // Show the frame
        frame.setVisible(true);
    }

    private void initializeDatabaseConnection() {
        String url = "jdbc:mysql://localhost:3306/adddatabaseuusing"; // Replace with your database URL
        String user = "root"; // Replace with your MySQL username
        String password = ""; // Replace with your MySQL password

        try {
            connection = DriverManager.getConnection(url, user, password);
            JOptionPane.showMessageDialog(frame, "Connected to database successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error connecting to database: " + e.getMessage());
        }
    }

    private void submitForm() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String bookNumber = bookBroughtNumberField.getText();
        String registrationNumber = registrationNumberField.getText();
        String gender = (String) genderDropdown.getSelectedItem();
        String collegeName = collegeNameField.getText();

        // Simple validation
        if (firstName.isEmpty() || lastName.isEmpty() || bookNumber.isEmpty() || registrationNumber.isEmpty()
                || gender.equals("Select Gender") || collegeName.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill out all fields!");
        } else {
            try {
                int bookNumberInt = Integer.parseInt(bookNumber);
                int registrationNumberInt = Integer.parseInt(registrationNumber);

                // Save data to MySQL
                saveToDatabase(firstName, lastName, bookNumberInt, registrationNumberInt, gender, collegeName);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame,
                        "Please enter valid numbers for Book Brought Number and Registration Number.");
            }
        }
    }

    private void saveToDatabase(String firstName, String lastName, int bookNumber, int registrationNumber,
            String gender,
            String collegeName) {
        String query = "INSERT INTO students (first_name, last_name, book_brought_number, registration_number, gender, college_name) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setInt(3, bookNumber);
            preparedStatement.setInt(4, registrationNumber);
            preparedStatement.setString(5, gender);
            preparedStatement.setString(6, collegeName);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(frame, "Form Submitted and Data Saved!");
                clearFields(); // Clear fields after successful submission
            } else {
                JOptionPane.showMessageDialog(frame, "Error saving data!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error saving data: " + e.getMessage());
        }
    }

    private void cancelForm() {
        clearFields();
    }

    private void clearFields() {
        firstNameField.setText("");
        lastNameField.setText("");
        bookBroughtNumberField.setText("");
        registrationNumberField.setText("");
        genderDropdown.setSelectedIndex(0);
        collegeNameField.setText("");
    }

    // Main method to run the program
    public static void main(String[] args) {
        new SimpleForm();
    }
}

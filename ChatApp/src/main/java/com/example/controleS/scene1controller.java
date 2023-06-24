package com.example.controleS;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class scene1controller {
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;

    private List<User> users;

    public scene1controller() {

        users = new ArrayList<>();
        loadUsersFromFile();
    }

    @FXML
    protected void login() throws IOException {
        if (authenticateUser(username.getText(), password.getText())) {
            Stage s = (Stage) username.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("scene2.fxml"));
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);
            s.setScene(scene);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Authentication Error");
            alert.setHeaderText("Username or password is incorrect!");
            alert.setContentText("You can retry by changing your information or sign up!");
            alert.show();
        }
    }

    @FXML
    protected void signUp() throws IOException {
        String newUsername = username.getText();
        String newPassword = password.getText();

        if (newUsername.isEmpty() || newPassword.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Sign Up Error");
            alert.setHeaderText("Username or password cannot be empty!");
            alert.setContentText("Please enter a valid username and password.");
            alert.show();
        } else {
            User newUser = new User(newUsername, newPassword);
            users.add(newUser);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true))) {
                writer.write(newUser.getUsername() + "," + newUser.getPassword());
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sign Up Successful");
            alert.setHeaderText(null);
            alert.setContentText("New user has been created successfully! You can now sign in.");
            alert.show();
        }
    }

    private boolean authenticateUser(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    private void loadUsersFromFile() {
        try (Scanner scanner = new Scanner(new File("users.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                StringTokenizer tokenizer = new StringTokenizer(line, ",");
                if (tokenizer.countTokens() == 2) {
                    String username = tokenizer.nextToken();
                    String password = tokenizer.nextToken();
                    User user = new User(username, password);
                    users.add(user);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

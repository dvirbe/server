package com.ashcollege.utils;

import com.ashcollege.entities.User;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@Component
public class DbUtils {

    private  Connection connection = null;

    @PostConstruct
    public Connection createConnection () {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/SocialNetwork", Constants.DB_USERNAME, Constants.DB_PASSWORD);
            System.out.println("Connection success");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }


    public boolean registerUser (User user) {
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(
                            "INSERT INTO usersdb (username, password) VALUE (?, ?)");
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;

    }

    public boolean signIn (String username, String password) {
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("SELECT * FROM usersdb where username=? and password=?;");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean usernameAvailable (String username) {
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(
                            "SELECT usersdb.username " +
                                    "FROM usersdb WHERE username = ? ");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            return !resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public List<User> getAllUsers () {
        List<User> allUsers = null;
        try {
            allUsers = new ArrayList<>();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT id, username, password FROM usersdb"
            );
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                allUsers.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return allUsers;
    }

}

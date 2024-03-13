package com.ashcollege.utils;

import com.ashcollege.entities.Post;
import com.ashcollege.entities.User;
import com.ashcollege.responses.UsersResponse;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@Component
public class DbUtils {

    private Connection connection = null;

    @PostConstruct
    public Connection createConnection() {
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


    public boolean uploadAvatar(String id , String path) {
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(
                            "Update usersdb set avatar = ? where id=?");
            preparedStatement.setString(1, path);
            preparedStatement.setString(2, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }


    public boolean registerUser(User user) {
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

    public List<Post> postsList(String userId) {
        List<Post> post = new ArrayList<>();
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("SELECT * FROM posts WHERE userId=?");
            preparedStatement.setString(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String idPosts = resultSet.getString("idPosts");
                String postUserId = resultSet.getString("userId");
                String username = resultSet.getString("username");
                String text = resultSet.getString("text");
                Post post1 = new Post(idPosts, postUserId, username, text);
                post.add(post1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return post;
    }
    public String getAvatar(String userId) {
        String avatar ="";
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("SELECT avatar FROM usersdb WHERE id=?");
            preparedStatement.setString(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
               avatar = resultSet.getString("avatar");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return avatar;
    }

    public List<String> usernameList(String username) {
        List<String> usernames = new ArrayList<>();
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("SELECT username FROM usersdb WHERE username LIKE ?");
            preparedStatement.setString(1, "%" + username + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String foundUsername = resultSet.getString("username");
                usernames.add(foundUsername);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return usernames;
    }


    public boolean signIn(String username, String password) {
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

    public String getUsername(String userId) {
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(
                            "SELECT username FROM usersdb WHERE id= ?");
            preparedStatement.setString(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return resultSet.getString("username");
            }else {
                return "";
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean usernameAvailable(String username) {
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



    public List<User> follow(String id) {
        List<User> allUsers = null;
        try {
            allUsers = new ArrayList<>();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT  id_follow_dest  FROM followdb where id_follow_origin=?"
            );
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println(resultSet.getFetchSize());
            while (resultSet.next()) {
                String userId = resultSet.getString("id_follow_dest");
               String name = getUsername(userId);
               allUsers.add(new User(Integer.parseInt(userId),name,null));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return allUsers;
    }

    public List<User> getAllUsers() {
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

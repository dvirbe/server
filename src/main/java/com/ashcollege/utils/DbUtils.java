package com.ashcollege.utils;

import com.ashcollege.entities.Post;
import com.ashcollege.entities.User;
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

    public Integer registerUser(User user) {
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(
                            "INSERT INTO usersdb (username, password) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Failed to retrieve the generated ID.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public int signIn(String username, String password) {
        try {
            int id =0;
            PreparedStatement preparedStatement =
                    connection.prepareStatement("SELECT * FROM usersdb where username=? and password=?;");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getInt("id");
            }
            return id;
        } catch (SQLException e) {
            System.out.println("error");
            throw new RuntimeException(e);
        }
    }

    public void changeToken(Integer token, String username, String password) {
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(
                            "Update usersdb set token = ? where username=? and password=?;");
            preparedStatement.setInt(1, token);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, password);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Post> postsList(String userId) {
        List<Post> postList = new ArrayList<>();
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
                Post post = new Post(idPosts, postUserId, username, text);
                postList.add(post);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return postList;
    }

    public List<Post> feedPostList(String id) {
        List<Post> postList = new ArrayList<>();
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("SELECT posts.idPosts ,posts.userId, posts.username, posts.text FROM posts JOIN followdb ON posts.userId = followdb.id_follow_dest WHERE followdb.id_follow_origin = ?  ORDER BY posts.idPosts DESC  LIMIT 20;");
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String idPosts = resultSet.getString("idPosts");
                String postUserId = resultSet.getString("userId");
                String username = resultSet.getString("username");
                String text = resultSet.getString("text");
                Post post = new Post(idPosts, postUserId, username, text);
                System.out.println(text);
                postList.add(post);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return postList;
    }

    public boolean uploadPost(String token, String text) {
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(
                            "INSERT INTO posts (userId, username ,text )\n" +
                                    "SELECT id, username , ?\n" +
                                    "FROM usersdb \n" +
                                    "WHERE token = ?;");
            preparedStatement.setString(1, text);
            preparedStatement.setString(2, token);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
//            throw new RuntimeException(e);
            return false;
        }
        return true;
    }


    public boolean uploadAvatar(String token, String path) {
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(
                            "Update usersdb set avatar = ? where token=?");
            preparedStatement.setString(1, path);
            preparedStatement.setString(2, token);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }


    public String getAvatar(String userId) {
        String avatar = "";
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

    public List<User> usernameList(String username) {
        List<User> usernameList = new ArrayList<>();
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("SELECT id,username FROM usersdb WHERE username LIKE ?");
            preparedStatement.setString(1, "%" + username + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String newUsername = resultSet.getString("username");
                usernameList.add(new User(id,newUsername,null));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return usernameList;
    }


    public String getUsername(String userId) {
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(
                            "SELECT username FROM usersdb WHERE id= ?");
            preparedStatement.setString(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("username");
            } else {
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

    public boolean startFollow(String token,String id) {
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(
                            "INSERT INTO followdb (id_follow_origin, id_follow_dest )\n" +
                                    "SELECT id ,?\n" +
                                    "FROM usersdb  \n" +
                                    "WHERE token = ? AND NOT EXISTS (\n" +
                                    "    SELECT 1\n" +
                                    "    FROM followdb\n" +
                                    "    WHERE id_follow_origin = usersdb.id\n" +
                                    "    AND id_follow_dest = ?\n" +
                                    ");");
            preparedStatement.setString(1, id);
            preparedStatement.setString(2, token);
            preparedStatement.setString(3, id);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public List<User> checkFollow(String id) {
        List<User> allUsers = null;
        try {
            allUsers = new ArrayList<>();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT  id_follow_dest  FROM followdb where id_follow_origin=?"
            );
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String userId = resultSet.getString("id_follow_dest");
                String name = getUsername(userId);
                allUsers.add(new User(Integer.parseInt(userId), name, null));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return allUsers;
    }

    public boolean unFollow(String token, String id) {
        System.out.println(token);
        System.out.println(id);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM followdb\n" +
                            "WHERE id_follow_origin = (SELECT id FROM usersdb WHERE token = ?) \n" +
                            "and id_follow_dest = ? "
            );
            preparedStatement.setString(1, token);
            preparedStatement.setString(2, id);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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




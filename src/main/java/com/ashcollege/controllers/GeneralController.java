package com.ashcollege.controllers;

import com.ashcollege.entities.Post;
import com.ashcollege.entities.User;
import com.ashcollege.responses.*;
import com.ashcollege.utils.DbUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.ashcollege.utils.Errors.*;

@RestController
public class GeneralController {

    @Autowired
    private DbUtils dbUtils;


    /*@CookieValue(value = "token", defaultValue = "no token") String token,*/
    private Integer randomToken() {
        int token;
        Random random = new Random();
        token = random.nextInt(1000000000);
        return token;
    }

    @RequestMapping("/sign-in")
    public LoginResponse checkUser(String username, String password, HttpServletResponse response) {
        System.out.println(username + " " + password);
        boolean success = false;
        int id = 0;
        Integer errorCode = null;

        id = dbUtils.signIn(username, password);
        if (id != 0) {
            success=true;
            Integer token = randomToken();
            dbUtils.changeToken(token, username, password);
            Cookie tokenCookie = new Cookie("token", token.toString());
            tokenCookie.setMaxAge(1000 * 60 * 24 * 365);
            response.addCookie(tokenCookie);
            Cookie idCookie = new Cookie("id", String.valueOf(id));
            idCookie.setMaxAge(1000 * 60 * 24 * 365);
            response.addCookie(idCookie);
        }
        return new LoginResponse(success);
    }

    @RequestMapping("/get-post-of-user")
    public PostResponse getPostOfUser(String userId) {
        List<Post> postsList;
        Integer errorCode = null;
        postsList = dbUtils.postsList(userId);
        System.out.println(postsList.toString());
        return new PostResponse(true, errorCode, postsList);
    }

    @RequestMapping("/usernameList")
    public UsersResponse usernameList(String username) {
        List<String> usernameList;
        Integer errorCode = null;
        List<User> userList = dbUtils.usernameList(username);
//        List<User> userList = usernameList.stream().map(User::new).toList();
        return new UsersResponse(true, errorCode, userList);
    }


    @RequestMapping("/get-avatar")
    public StringResponse getAvatar(String id) {
        String avatar = "";
        Integer errorCode = null;
        avatar = dbUtils.getAvatar(id);
        return new StringResponse(true, errorCode, avatar);
    }


    @RequestMapping("/uploadAvatar")
    public BasicResponse follow(String id, String path, HttpServletResponse response) {

        boolean success = false;
        Integer errorCode = null;
        success = dbUtils.uploadAvatar(id, path);
        return new BasicResponse(success, errorCode);
    }


    @RequestMapping("/follow")
    public UsersResponse follow(String id) {
        List<User> followList;
        Integer errorCode = null;
        followList = dbUtils.follow(id);
        return new UsersResponse(true, errorCode, followList);
    }

    @RequestMapping("/register")
    public RegisterResponse register(String username, String password, String repeat) {
        boolean success = false;
        Integer errorCode = null;
        Integer id = null;
        if (username != null) {
            if (password != null) {
                if (password.equals(repeat)) {
                    if (usernameAvailable(username).isAvailable()) {
                        User user = new User();
                        user.setUsername(username);
                        user.setPassword(password);
                        success = dbUtils.registerUser(user);
                        id = user.getId();
                    } else {
                        errorCode = ERROR_USERNAME_NOT_AVAILABLE;
                    }
                } else {
                    errorCode = ERROR_PASSWORDS_DONT_MATCH;
                }
            } else {
                errorCode = ERROR_MISSING_PASSWORD;
            }
        } else {
            errorCode = ERROR_MISSING_USERNAME;
        }
        return new RegisterResponse(success, errorCode, id);
    }

    @RequestMapping("/username-available")
    public UsernameAvailableResponse usernameAvailable(String username) {
        boolean success = false;
        Integer errorCode = null;
        boolean available = false;
        if (username != null) {
            available = dbUtils.usernameAvailable(username);
            success = true;
        } else {
            errorCode = ERROR_MISSING_USERNAME;
        }
        return new UsernameAvailableResponse(success, errorCode, available);

    }

    @RequestMapping("/get-username")
    public UsersResponse getUsername(String userId) {
        boolean success = false;
        Integer errorCode = null;
        String name = "";
        if (userId != null) {
            name = dbUtils.getUsername(userId);
            success = true;
        } else {
            errorCode = ERROR_MISSING_USERNAME;
        }
        return new UsersResponse(success, errorCode, List.of(new User(name, null)));

    }


    @RequestMapping("get-all-users")
    public UsersResponse getAllUsers() {
        List<User> allUsers = dbUtils.getAllUsers();
        return new UsersResponse(allUsers);
    }
}

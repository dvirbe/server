package com.ashcollege.controllers;

import com.ashcollege.entities.Post;
import com.ashcollege.entities.User;
import com.ashcollege.responses.*;
import com.ashcollege.utils.DbUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import static com.ashcollege.utils.Errors.*;

@RestController
public class GeneralController {

    @Autowired
    private DbUtils dbUtils;
    private final String REGEX = "^[a-zA-Z0-9]{4,30}$";
    Pattern pattern = Pattern.compile(REGEX);

    /*@CookieValue(value = "token", defaultValue = "no token") String token,*/
    private Integer randomToken() {
        int token;
        Random random = new Random();
        token = random.nextInt(1000000000);
        return token;
    }

    @RequestMapping("/register")
    public RegisterResponse register(String username, String password, String repeat) {
        if (username == null) {
            return new RegisterResponse(false, ERROR_MISSING_USERNAME, null);
        }
        if (password == null) {
            return new RegisterResponse(false, ERROR_MISSING_PASSWORD, null);
        }
        if (!password.equals(repeat)) {
            return new RegisterResponse(false, ERROR_PASSWORDS_DONT_MATCH, null);
        }
        if (!pattern.matcher(password).matches()) {
            return new RegisterResponse(false, ERROR_PASSWORD_NOT_STRONG, null);
        }
        if (!pattern.matcher(username).matches()) {
            return new RegisterResponse(false, ERROR_USERNAME_TOO_SHORT, null);
        }
        if (!usernameAvailable(username).isAvailable()) {
            return new RegisterResponse(false, ERROR_USERNAME_NOT_AVAILABLE, null);
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        Integer id = dbUtils.registerUser(user);
        return new RegisterResponse(true, null, id);
    }

    @RequestMapping("/sign-in")
    public BasicResponse checkUser(String username, String password, HttpServletResponse response) {
        boolean success = false;
        int id = 0;
        Integer errorCode = null;

        id = dbUtils.signIn(username, password);

        if (id != 0) {
            success = true;
            Integer token = randomToken();
            dbUtils.changeToken(token, username, password);
            Cookie tokenCookie = new Cookie("token", token.toString());
            tokenCookie.setMaxAge(1000 * 60 * 24 * 365);
            response.addCookie(tokenCookie);
            Cookie idCookie = new Cookie("id", String.valueOf(id));
            idCookie.setMaxAge(1000 * 60 * 24 * 365);
            response.addCookie(idCookie);
        } else {
            errorCode = ERROR_USER_DO_NOT_EXIST;
        }

        return new BasicResponse(success, errorCode);
    }


    @RequestMapping("/get-feed")
    public PostResponse getFeed(
            @CookieValue(value = "id", defaultValue = "1") String id) {
        List<Post> postsList;
        Integer errorCode = null;
        postsList = dbUtils.feedPostList(id);
        return new PostResponse(true, errorCode, postsList);
    }

    @RequestMapping("/get-post-of-user")
    public PostResponse getPostOfUser(String userId) {
        List<Post> postsList;
        Integer errorCode = null;
        postsList = dbUtils.postsList(userId);
        return new PostResponse(true, errorCode, postsList);
    }


    @RequestMapping("/upload-post")
    public BasicResponse uploadPost(
            @CookieValue(value = "token", defaultValue = "no token") String token,
            String text
    ) {
        Integer errorCode = null;
        boolean success = false;
        if (!text.isEmpty() && text.length()<=500 ){
            success = dbUtils.uploadPost(token,text);
        }else {
            errorCode=ERROR_TEXT_LENGTH;
        }
return new BasicResponse(success,errorCode);
    }


    @RequestMapping("/get-avatar")
    public StringResponse getAvatar(String id) {
        String avatar;
        Integer errorCode = null;
        avatar = dbUtils.getAvatar(id);
        return new StringResponse(true, errorCode, avatar);
    }

    @RequestMapping("/uploadAvatar")
    public BasicResponse uploadAvatar(
            @CookieValue(value = "token", defaultValue = "no token") String token,
            String text
    ){
        boolean success = false;
        Integer errorCode = null;
        success = dbUtils.uploadAvatar(token ,text );
        return new BasicResponse(success, errorCode);
    }

    @RequestMapping("/usernameList")
    public UsersResponse usernameList(String username) {
        Integer errorCode = null;
        List<User> userList = dbUtils.usernameList(username);
        return new UsersResponse(true, errorCode, userList);
    }

    @RequestMapping("/startFollow")
    public BasicResponse follow(
            @CookieValue(value = "token", defaultValue = "no token") String token,
            String id
    ) {
        boolean success = false;
        Integer errorCode = null;
        success = dbUtils.startFollow(token,id);
        return new BasicResponse(success, errorCode);
    }

    @RequestMapping("/unFollow")
    public BasicResponse unFollow(
            @CookieValue(value = "token", defaultValue = "no token") String token,
            String id
    ) {
        boolean success = false;
        Integer errorCode = null;
        success = dbUtils.unFollow(token,id);
        return new BasicResponse(success, errorCode);
    }

    @RequestMapping("/get-followers")
    public UsersResponse doesFollow(
            @CookieValue(value = "id", defaultValue = "id") String id
    ) {
        boolean success = false;
        Integer errorCode = null;
        List<User> usersList = dbUtils.checkFollow(id);
        if (usersList.size()>0){
            success=true;
        }
        return new UsersResponse(success, errorCode,usersList);
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

}

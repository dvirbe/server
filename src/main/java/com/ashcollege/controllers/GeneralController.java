package com.ashcollege.controllers;

import com.ashcollege.entities.User;
import com.ashcollege.responses.LoginResponse;
import com.ashcollege.responses.RegisterResponse;
import com.ashcollege.responses.UsernameAvailableResponse;
import com.ashcollege.responses.UsersResponse;
import com.ashcollege.utils.DbUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static com.ashcollege.utils.Errors.*;

@RestController
public class GeneralController {


    @Autowired
    private DbUtils dbUtils;



    @RequestMapping("/")
    public String test () {
        return "Hello From Server";
    }

    @RequestMapping("/sign-in")
    public LoginResponse checkUser (String username, String password) {
        System.out.println(username +" "+password);
        boolean success = false;
        Integer errorCode = null;
        success = dbUtils.signIn(username, password);
        return new LoginResponse(success);
    }

    @RequestMapping("/register")
    public RegisterResponse register (String username, String password, String repeat) {
        boolean success =false;
        Integer errorCode = null;
        Integer id = null;
        if (username != null) {
            if (password != null) {
                if (password.equals(repeat)) {
                    if (usernameAvailable(username).isAvailable()) {
                        User user = new User();
                        user.setUsername(username);
                        user.setPassword(password);
                        success=dbUtils.registerUser(user);
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
    public UsernameAvailableResponse usernameAvailable (String username) {
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

    @RequestMapping("get-all-users")
    public UsersResponse getAllUsers () {
        List<User> allUsers = dbUtils.getAllUsers();
        return new UsersResponse(allUsers);
    }
}

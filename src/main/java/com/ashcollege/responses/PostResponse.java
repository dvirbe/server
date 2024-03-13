package com.ashcollege.responses;

import com.ashcollege.entities.Post;

import java.util.List;

public class PostResponse extends BasicResponse {
    private List<Post> allPost;

    public PostResponse(List<Post> allPosts) {
        this.allPost = allPosts;
    }

    public PostResponse(boolean success, Integer errorCode, List<Post> allPosts) {
        super(success, errorCode);
        this.allPost = allPosts;
    }

    public List<Post> getAllPosts() {
        return allPost;
    }

    public void setAllPosts(List<Post> allPosts) {
        this.allPost = allPosts;
    }
}

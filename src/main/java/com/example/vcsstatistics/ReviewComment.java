package com.example.vcsstatistics;

public class ReviewComment {
    private final String userEmail;
    private final String comment;

    public ReviewComment(String userEmail, String comment) {
        this.userEmail = userEmail;
        this.comment = comment;
    }

    public String getUserEmail() { return userEmail; }
    public String getComment() { return comment; }
}

package com.example.vcsstatistics;

public class CommitInfo {
    private final String userEmail;
    private final int changedLines;
    private final String message;

    public CommitInfo(String userEmail, int changedLines, String message) {
        this.userEmail = userEmail;
        this.changedLines = changedLines;
        this.message = message;
    }

    public String getUserEmail() { return userEmail; }
    public int getChangedLines() { return changedLines; }
    public String getMessage() { return message; }
}

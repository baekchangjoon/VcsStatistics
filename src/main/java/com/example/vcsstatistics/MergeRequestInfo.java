package com.example.vcsstatistics;

public class MergeRequestInfo {
    private final String userEmail;
    private final int mrId;
    private final int changedLines;

    public MergeRequestInfo(String userEmail, int mrId, int changedLines) {
        this.userEmail = userEmail;
        this.mrId = mrId;
        this.changedLines = changedLines;
    }

    public String getUserEmail() { return userEmail; }
    public int getMrId() { return mrId; }
    public int getChangedLines() { return changedLines; }
}

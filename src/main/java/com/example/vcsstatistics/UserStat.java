package com.example.vcsstatistics;

public class UserStat {

    private final String userEmail;
    private int commitCount;
    private int totalChangedLines;
    private int mrCount;
    private int reviewCommentCount;
    private int reviewCommentChars;

    public UserStat(String userEmail) {
        this.userEmail = userEmail;
    }

    public void incrementCommitCount() { commitCount++; }
    public void addLines(int lines) { totalChangedLines += lines; }
    public void incrementMrCount() { mrCount++; }
    public void incrementReviewCommentCount() { reviewCommentCount++; }
    public void addReviewCommentChars(int length) { reviewCommentChars += length; }

    public String getUserEmail() { return userEmail; }
    public int getCommitCount() { return commitCount; }
    public int getMrCount() { return mrCount; }
    public int getReviewCommentCount() { return reviewCommentCount; }
    public int getReviewCommentChars() { return reviewCommentChars; }
    public int getTotalChangedLines() { return totalChangedLines; }

    @Override
    public String toString() {
        return "UserStat{user=" + userEmail
             + ", commits=" + commitCount
             + ", lines=" + totalChangedLines
             + ", MRs=" + mrCount
             + ", reviewComments=" + reviewCommentCount
             + ", reviewCommentChars=" + reviewCommentChars
             + "}";
    }
}

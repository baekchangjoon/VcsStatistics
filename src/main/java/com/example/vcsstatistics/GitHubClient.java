package com.example.vcsstatistics;

import java.util.ArrayList;
import java.util.List;

public class GitHubClient implements VcsClient {

    private final String gitHubToken;
    private final String gitHubApiUrl;

    public GitHubClient(String token, String apiUrl) {
        this.gitHubToken = token;
        this.gitHubApiUrl = apiUrl;
    }

    @Override
    public List<CommitInfo> fetchCommits(String projectId,
                                         String since,
                                         String until) {
        List<CommitInfo> commitList = new ArrayList<>();
        // GitHub API 호출 (예시 Stub)
        commitList.add(new CommitInfo("user1@github.com", 120, "GH commit 1"));
        commitList.add(new CommitInfo("user2@github.com", 80, "GH commit 2"));
        return commitList;
    }

    @Override
    public List<MergeRequestInfo> fetchMergeRequests(String projectId,
                                                     String since,
                                                     String until) {
        List<MergeRequestInfo> mrList = new ArrayList<>();
        // GitHub API 호출 (Pull Requests)
        mrList.add(new MergeRequestInfo("user1@github.com", 101, 50));
        mrList.add(new MergeRequestInfo("user2@github.com", 102, 100));
        return mrList;
    }

    @Override
    public List<ReviewComment> fetchReviewComments(String projectId,
                                                   String mrId) {
        List<ReviewComment> comments = new ArrayList<>();
        // GitHub API 호출 (PR Review Comments)
        comments.add(new ReviewComment("user2@github.com", "Use more descriptive names"));
        comments.add(new ReviewComment("user3@github.com", "Check edge cases"));
        return comments;
    }
}

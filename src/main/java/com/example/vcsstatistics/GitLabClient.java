package com.example.vcsstatistics;

import java.util.List;
import java.util.ArrayList;

public class GitLabClient implements VcsClient {

    private final String gitLabToken;
    private final String gitLabApiUrl;

    public GitLabClient(String token, String apiUrl) {
        this.gitLabToken = token;
        this.gitLabApiUrl = apiUrl;
    }

    @Override
    public List<CommitInfo> fetchCommits(String projectId,
                                         String since,
                                         String until) {
        List<CommitInfo> commitList = new ArrayList<>();
        // GitLab API 호출 (예시 Stub)
        commitList.add(new CommitInfo("user1@example.com", 100, "GL commit 1"));
        commitList.add(new CommitInfo("user2@example.com", 150, "GL commit 2"));
        return commitList;
    }

    @Override
    public List<MergeRequestInfo> fetchMergeRequests(String projectId,
                                                     String since,
                                                     String until) {
        List<MergeRequestInfo> mrList = new ArrayList<>();
        // GitLab API 호출 (예시 Stub)
        mrList.add(new MergeRequestInfo("user1@example.com", 11, 15));
        mrList.add(new MergeRequestInfo("user2@example.com", 12, 10));
        return mrList;
    }

    @Override
    public List<ReviewComment> fetchReviewComments(String projectId,
                                                   String mrId) {
        List<ReviewComment> comments = new ArrayList<>();
        // GitLab API 호출 (예시 Stub)
        comments.add(new ReviewComment("user3@example.com","Nice job"));
        return comments;
    }
}

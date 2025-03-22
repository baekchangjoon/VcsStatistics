package com.example.vcsstatistics;

import java.util.List;

public interface VcsClient {

    List<CommitInfo> fetchCommits(String projectId, String since, String until);

    List<MergeRequestInfo> fetchMergeRequests(String projectId, String since, String until);

    List<ReviewComment> fetchReviewComments(String projectId, String mrId);
}

package com.example.vcsstatistics;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

public class GitHubClientTest {

    @Test
    public void testFetchCommits() {
        GitHubClient client = new GitHubClient("TOKEN", "https://api.github.com");
        List<CommitInfo> commits = client.fetchCommits("repoId", "2023-01-01", "2023-12-31");
        assertNotNull(commits);
        assertFalse(commits.isEmpty());
    }

    @Test
    public void testFetchMergeRequests() {
        GitHubClient client = new GitHubClient("TOKEN", "https://api.github.com");
        List<MergeRequestInfo> prs = client.fetchMergeRequests("repoId", "2023-01-01", "2023-12-31");
        assertNotNull(prs);
        assertFalse(prs.isEmpty());
    }

    @Test
    public void testFetchReviewComments() {
        GitHubClient client = new GitHubClient("TOKEN", "https://api.github.com");
        List<ReviewComment> comments = client.fetchReviewComments("repoId", "100");
        assertNotNull(comments);
        assertFalse(comments.isEmpty());
    }
}

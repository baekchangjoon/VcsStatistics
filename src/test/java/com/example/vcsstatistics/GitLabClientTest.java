package com.example.vcsstatistics;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

public class GitLabClientTest {

    @Test
    public void testFetchCommits() {
        GitLabClient client = new GitLabClient("TOKEN", "URL");
        List<CommitInfo> commits = client.fetchCommits("123", "2023-01-01", "2023-12-31");
        assertNotNull(commits);
        assertTrue(commits.size() > 0);
    }

    @Test
    public void testFetchMergeRequests() {
        GitLabClient client = new GitLabClient("TOKEN", "URL");
        List<MergeRequestInfo> mrs = client.fetchMergeRequests("123", "2023-01-01", "2023-12-31");
        assertNotNull(mrs);
        assertTrue(mrs.size() > 0);
    }

    @Test
    public void testFetchReviewComments() {
        GitLabClient client = new GitLabClient("TOKEN", "URL");
        List<ReviewComment> comments = client.fetchReviewComments("123", "1");
        assertNotNull(comments);
        assertTrue(comments.size() > 0);
    }
}

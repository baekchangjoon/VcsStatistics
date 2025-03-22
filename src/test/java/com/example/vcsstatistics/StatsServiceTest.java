package com.example.vcsstatistics;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class StatsServiceTest {

    private GitLabClient mockClient;
    private StatsService statsService;

    @Before
    public void setUp() {
        mockClient = mock(GitLabClient.class);
        statsService = new StatsService(mockClient);
    }

    @Test
    public void testGenerateStats() {
        when(mockClient.fetchCommits(anyString(), anyString(), anyString()))
            .thenReturn(Arrays.asList(new CommitInfo("user1@example.com", 100, "test")));
        when(mockClient.fetchMergeRequests(anyString(), anyString(), anyString()))
            .thenReturn(Arrays.asList(new MergeRequestInfo("user1@example.com", 1, 50)));
        when(mockClient.fetchReviewComments(anyString(), anyString()))
            .thenReturn(Arrays.asList(new ReviewComment("user1@example.com", "Nice code")));

        List<String> users = Arrays.asList("user1@example.com", "user2@example.com");
        Map<String, UserStat> stats = statsService.generateStats("123", "2023-01-01", "2023-12-31", users);
        UserStat user1Stat = stats.get("user1@example.com");

        assertEquals(1, user1Stat.getCommitCount());
        assertEquals(100, user1Stat.getTotalChangedLines());
        assertEquals(1, user1Stat.getMrCount());
        assertEquals(1, user1Stat.getReviewCommentCount());
        assertTrue(user1Stat.getReviewCommentChars() > 0);
    }

    @Test
    public void testGenerateStats_NoData() {
        when(mockClient.fetchCommits(anyString(), anyString(), anyString()))
            .thenReturn(Arrays.asList());
        when(mockClient.fetchMergeRequests(anyString(), anyString(), anyString()))
            .thenReturn(Arrays.asList());

        List<String> users = Arrays.asList("user1@example.com");
        Map<String, UserStat> stats = statsService.generateStats("123", "2023-01-01", "2023-12-31", users);
        UserStat user1Stat = stats.get("user1@example.com");
        assertEquals(0, user1Stat.getCommitCount());
        assertEquals(0, user1Stat.getMrCount());
    }
}

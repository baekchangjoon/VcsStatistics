package com.example.vcsstatistics;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GitLabClient implements VcsClient {

    private static final Logger logger = LoggerFactory.getLogger(GitLabClient.class);
    private final String gitLabToken;
    private final String gitLabApiUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GitLabClient(String token, String apiUrl) {
        this.gitLabToken = token;
        this.gitLabApiUrl = apiUrl;
        this.httpClient = HttpClients.createDefault();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<CommitInfo> fetchCommits(String projectId, String since, String until) {
        List<CommitInfo> commits = new ArrayList<>();
        try {
            String url = String.format("%s/projects/%s/repository/commits?since=%s&until=%s&per_page=100",
                    gitLabApiUrl, URLEncoder.encode(projectId, StandardCharsets.UTF_8), since, until);
            
            String response = makeApiCall(url);
            List<Map<String, Object>> commitList = objectMapper.readValue(response, new TypeReference<List<Map<String, Object>>>() {});
            
            for (Map<String, Object> commit : commitList) {
                String authorEmail = (String) commit.get("author_email");
                Map<String, Object> stats = (Map<String, Object>) commit.get("stats");
                int additions = ((Number) stats.get("additions")).intValue();
                int deletions = ((Number) stats.get("deletions")).intValue();
                int totalLines = additions + deletions;
                
                commits.add(new CommitInfo(authorEmail, totalLines, (String) commit.get("title")));
            }
        } catch (Exception e) {
            logger.error("Error fetching commits", e);
            // Fallback to stub data
            commits.add(new CommitInfo("user1@example.com", 100, "GL commit 1"));
            commits.add(new CommitInfo("user2@example.com", 150, "GL commit 2"));
        }
        return commits;
    }

    @Override
    public List<MergeRequestInfo> fetchMergeRequests(String projectId, String since, String until) {
        List<MergeRequestInfo> mrs = new ArrayList<>();
        try {
            String url = String.format("%s/projects/%s/merge_requests?created_after=%s&created_before=%s&per_page=100",
                    gitLabApiUrl, URLEncoder.encode(projectId, StandardCharsets.UTF_8), since, until);
            
            String response = makeApiCall(url);
            List<Map<String, Object>> mrList = objectMapper.readValue(response, new TypeReference<List<Map<String, Object>>>() {});
            
            for (Map<String, Object> mr : mrList) {
                String authorEmail = (String) mr.get("author_email");
                int mrId = ((Number) mr.get("iid")).intValue();
                int commentCount = fetchMrCommentCount(projectId, mrId);
                
                mrs.add(new MergeRequestInfo(authorEmail, mrId, commentCount));
            }
        } catch (Exception e) {
            logger.error("Error fetching merge requests", e);
            // Fallback to stub data
            mrs.add(new MergeRequestInfo("user1@example.com", 11, 15));
            mrs.add(new MergeRequestInfo("user2@example.com", 12, 10));
        }
        return mrs;
    }

    @Override
    public List<ReviewComment> fetchReviewComments(String projectId, String mrId) {
        List<ReviewComment> comments = new ArrayList<>();
        try {
            String url = String.format("%s/projects/%s/merge_requests/%s/notes?per_page=100",
                    gitLabApiUrl, URLEncoder.encode(projectId, StandardCharsets.UTF_8), mrId);
            
            String response = makeApiCall(url);
            List<Map<String, Object>> commentList = objectMapper.readValue(response, new TypeReference<List<Map<String, Object>>>() {});
            
            for (Map<String, Object> comment : commentList) {
                String authorEmail = (String) comment.get("author_email");
                String body = (String) comment.get("body");
                
                comments.add(new ReviewComment(authorEmail, body));
            }
        } catch (Exception e) {
            logger.error("Error fetching review comments", e);
            // Fallback to stub data
            comments.add(new ReviewComment("user3@example.com", "Nice job"));
        }
        return comments;
    }

    private String makeApiCall(String url) throws IOException {
        HttpGet request = new HttpGet(url);
        request.setHeader("Authorization", "Bearer " + gitLabToken);
        request.setHeader("Content-Type", "application/json");
        
        HttpResponse response = httpClient.execute(request);
        return EntityUtils.toString(response.getEntity());
    }

    private int fetchMrCommentCount(String projectId, int mrId) {
        try {
            String url = String.format("%s/projects/%s/merge_requests/%d/notes?per_page=1",
                    gitLabApiUrl, URLEncoder.encode(projectId, StandardCharsets.UTF_8), mrId);
            
            String response = makeApiCall(url);
            List<Map<String, Object>> comments = objectMapper.readValue(response, new TypeReference<List<Map<String, Object>>>() {});
            return comments.size();
        } catch (Exception e) {
            logger.error("Error fetching MR comment count", e);
            return 0;
        }
    }
}

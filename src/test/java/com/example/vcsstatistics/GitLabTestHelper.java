package com.example.vcsstatistics;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.containers.GenericContainer;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GitLabTestHelper {
    
    private final String gitlabUrl;
    private final String accessToken;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public GitLabTestHelper(String gitlabUrl, String accessToken) {
        this.gitlabUrl = gitlabUrl;
        this.accessToken = accessToken;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }
    
    public int createTestProject(String projectName) throws Exception {
        String projectData = String.format("""
                {
                    "name": "%s",
                    "description": "Test project for VCS Statistics",
                    "visibility": "private",
                    "initialize_with_readme": true
                }
                """, projectName);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(gitlabUrl + "/api/v4/projects"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .POST(HttpRequest.BodyPublishers.ofString(projectData))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 201) {
            JsonNode projectNode = objectMapper.readTree(response.body());
            return projectNode.get("id").asInt();
        } else {
            throw new RuntimeException("프로젝트 생성 실패: " + response.statusCode() + " - " + response.body());
        }
    }
    
    public void createTestCommits(int projectId, String userEmail) throws Exception {
        // 테스트용 커밋 생성
        String commitData = String.format("""
                {
                    "branch": "main",
                    "commit_message": "Test commit by %s",
                    "actions": [
                        {
                            "action": "create",
                            "file_path": "test-file.txt",
                            "content": "Test content for commit"
                        }
                    ]
                }
                """, userEmail);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(gitlabUrl + "/api/v4/projects/" + projectId + "/repository/commits"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .POST(HttpRequest.BodyPublishers.ofString(commitData))
                .build();
        
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
    
    public int createTestMergeRequest(int projectId, String title, String sourceBranch, String targetBranch) throws Exception {
        String mrData = String.format("""
                {
                    "source_branch": "%s",
                    "target_branch": "%s",
                    "title": "%s",
                    "description": "Test merge request"
                }
                """, sourceBranch, targetBranch, title);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(gitlabUrl + "/api/v4/projects/" + projectId + "/merge_requests"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .POST(HttpRequest.BodyPublishers.ofString(mrData))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 201) {
            JsonNode mrNode = objectMapper.readTree(response.body());
            return mrNode.get("iid").asInt();
        } else {
            throw new RuntimeException("MR 생성 실패: " + response.statusCode() + " - " + response.body());
        }
    }
    
    public void createTestReviewComment(int projectId, int mrIid, String comment) throws Exception {
        String commentData = String.format("""
                {
                    "body": "%s"
                }
                """, comment);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(gitlabUrl + "/api/v4/projects/" + projectId + "/merge_requests/" + mrIid + "/notes"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .POST(HttpRequest.BodyPublishers.ofString(commentData))
                .build();
        
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
    
    public void setupTestData(int projectId, List<String> users) throws Exception {
        // 각 사용자별로 테스트 데이터 생성
        for (String userEmail : users) {
            // 커밋 생성
            createTestCommits(projectId, userEmail);
            
            // 브랜치 생성 및 MR 생성
            String branchName = "feature-" + userEmail.replace("@", "-");
            createBranch(projectId, branchName);
            int mrIid = createTestMergeRequest(projectId, "Test MR by " + userEmail, branchName, "main");
            
            // 리뷰 코멘트 생성
            createTestReviewComment(projectId, mrIid, "Great work by " + userEmail);
        }
    }
    
    private void createBranch(int projectId, String branchName) throws Exception {
        String branchData = String.format("""
                {
                    "branch": "%s",
                    "ref": "main"
                }
                """, branchName);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(gitlabUrl + "/api/v4/projects/" + projectId + "/repository/branches"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .POST(HttpRequest.BodyPublishers.ofString(branchData))
                .build();
        
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
package com.example.vcsstatistics;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GitLabStatTool {
    public static void main(String[] args) {
        VcsClient gitLabClient = new GitLabClient("GITLAB_TOKEN", "https://gitlab.example.com/api/v4");
        AiEvaluator aiClient = new OpenAiClient("OPENAI_API_KEY");
        // 혹은 OllamaClient 사용
        // AiEvaluator aiClient = new OllamaClient("http://localhost:11411");

        StatsService statsService = new StatsService(gitLabClient);
        Map<String, UserStat> statMap = statsService.generateStats("123", "2023-01-01", "2023-12-31",
                                                 Arrays.asList("user1@example.com"));

        // 예시 호출
        String codeReviewResult = aiClient.evaluateCodeCleanliness(Arrays.asList("diff ..."));
        System.out.println(codeReviewResult);
    }
}

package com.example.vcsstatistics;

public class GitHubStatTool {

    public static void main(String[] args) {
        VcsClient githubClient = new GitHubClient("GITHUB_TOKEN",
                                                  "https://api.github.com");
        StatsService statsService = new StatsService(githubClient);
        // 나머지 흐름은 GitLabStatTool 과 동일
    }
}

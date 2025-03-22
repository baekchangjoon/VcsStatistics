package com.example.vcsstatistics;

import java.util.List;

public class OllamaClient implements AiEvaluator {

    private final String ollamaEndpoint;

    public OllamaClient(String endpoint) {
        this.ollamaEndpoint = endpoint;
    }

    @Override
    public String evaluateCodeCleanliness(List<String> codeDiffs) {
        String prompt = "Check code cleanliness:\n" + String.join("\n", codeDiffs);
        return callOllamaApi(prompt);
    }

    @Override
    public String evaluateReviewQuality(List<String> reviewComments) {
        String prompt = "Check review quality:\n" + String.join("\n", reviewComments);
        return callOllamaApi(prompt);
    }

    private String callOllamaApi(String prompt) {
        // Ollama API 연동 로직 (Stub)
        // HTTP POST 또는 CLI 호출 등
        return "Result from Ollama (Stub)";
    }
}

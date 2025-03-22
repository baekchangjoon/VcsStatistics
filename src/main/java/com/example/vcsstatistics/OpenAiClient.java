package com.example.vcsstatistics;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.util.List;

public class OpenAiClient implements AiEvaluator {

    private final String openAiApiKey;

    public OpenAiClient(String openAiApiKey) {
        this.openAiApiKey = openAiApiKey;
    }

    @Override
    public String evaluateCodeCleanliness(List<String> codeDiffs) {
        String prompt = "Check code cleanliness:\n" + String.join("\n", codeDiffs);
        return callOpenAiApi(prompt);
    }

    @Override
    public String evaluateReviewQuality(List<String> reviewComments) {
        String prompt = "Check review quality:\n" + String.join("\n", reviewComments);
        return callOpenAiApi(prompt);
    }

    private String callOpenAiApi(String prompt) {
        try {
            URL url = new URL("https://api.openai.com/v1/chat/completions");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + openAiApiKey);
            conn.setDoOutput(true);
            // 요청 Body 작성(간단 Stub)
            try (OutputStream os = conn.getOutputStream()) {
                os.write(prompt.getBytes());
            }
            // 실제 응답 파싱 로직은 생략
            return "Result from OpenAI (Stub)";
        } catch(Exception e) {
            return "Error calling OpenAI";
        }
    }
}

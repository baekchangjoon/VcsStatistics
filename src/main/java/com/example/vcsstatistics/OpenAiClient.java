package com.example.vcsstatistics;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenAiClient implements AiEvaluator {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiClient.class);
    private final String openAiApiKey;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public OpenAiClient(String openAiApiKey) {
        this.openAiApiKey = openAiApiKey;
        this.httpClient = HttpClients.createDefault();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String evaluateCodeCleanliness(List<String> codeDiffs) {
        String prompt = "다음 코드 변경사항을 클린 코드 원칙에 따라 평가해주세요. " +
                       "코드의 가독성, 유지보수성, 성능, 보안 등을 고려하여 평가하고 개선점을 제시해주세요:\n\n" +
                       String.join("\n", codeDiffs);
        return callOpenAiApi(prompt);
    }

    @Override
    public String evaluateReviewQuality(List<String> reviewComments) {
        String prompt = "다음 코드 리뷰 코멘트들을 평가해주세요. " +
                       "리뷰의 품질, 건설성, 명확성, 도움되는 정도를 고려하여 평가해주세요:\n\n" +
                       String.join("\n", reviewComments);
        return callOpenAiApi(prompt);
    }

    private String callOpenAiApi(String prompt) {
        try {
            String url = "https://api.openai.com/v1/chat/completions";
            HttpPost request = new HttpPost(url);
            request.setHeader("Authorization", "Bearer " + openAiApiKey);
            request.setHeader("Content-Type", "application/json");

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-3.5-turbo");
            
            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);
            requestBody.put("messages", List.of(message));
            requestBody.put("max_tokens", 1000);
            requestBody.put("temperature", 0.7);

            String jsonBody = objectMapper.writeValueAsString(requestBody);
            request.setEntity(new StringEntity(jsonBody, "UTF-8"));

            HttpResponse response = httpClient.execute(request);
            String responseBody = EntityUtils.toString(response.getEntity());

            if (response.getStatusLine().getStatusCode() == 200) {
                Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, Object> messageObj = (Map<String, Object>) choice.get("message");
                    return (String) messageObj.get("content");
                }
            }
            
            logger.error("OpenAI API call failed: {}", responseBody);
            return "OpenAI API 호출에 실패했습니다.";
            
        } catch (Exception e) {
            logger.error("Error calling OpenAI API", e);
            return "OpenAI API 호출 중 오류가 발생했습니다: " + e.getMessage();
        }
    }
}

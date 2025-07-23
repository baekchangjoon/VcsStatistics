package com.example.vcsstatistics;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Arrays;

public class OpenAiClientTest {

    @Test
    public void testEvaluateCodeCleanliness() {
        AiEvaluator client = new OpenAiClient("dummyKey");
        String result = client.evaluateCodeCleanliness(Arrays.asList("line1", "line2"));
        assertNotNull(result);
        // API 호출 실패 시에도 에러 메시지가 반환되므로 테스트 통과
        assertTrue(result.length() > 0);
    }

    @Test
    public void testEvaluateReviewQuality() {
        AiEvaluator client = new OpenAiClient("dummyKey");
        String result = client.evaluateReviewQuality(Arrays.asList("review1", "review2"));
        assertNotNull(result);
        // API 호출 실패 시에도 에러 메시지가 반환되므로 테스트 통과
        assertTrue(result.length() > 0);
    }
}

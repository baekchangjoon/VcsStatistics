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
        assertTrue(result.contains("Result from OpenAI")
                   || result.contains("Error calling OpenAI"));
    }

    @Test
    public void testEvaluateReviewQuality() {
        AiEvaluator client = new OpenAiClient("dummyKey");
        String result = client.evaluateReviewQuality(Arrays.asList("review1", "review2"));
        assertNotNull(result);
        assertTrue(result.contains("Result from OpenAI")
                   || result.contains("Error calling OpenAI"));
    }
}

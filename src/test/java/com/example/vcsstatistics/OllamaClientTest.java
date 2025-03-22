package com.example.vcsstatistics;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Arrays;

public class OllamaClientTest {

    @Test
    public void testEvaluateCodeCleanliness() {
        AiEvaluator client = new OllamaClient("http://localhost:11411");
        String result = client.evaluateCodeCleanliness(Arrays.asList("line1", "line2"));
        assertNotNull(result);
        assertTrue(result.contains("Result from Ollama"));
    }

    @Test
    public void testEvaluateReviewQuality() {
        AiEvaluator client = new OllamaClient("http://localhost:11411");
        String result = client.evaluateReviewQuality(Arrays.asList("review1", "review2"));
        assertNotNull(result);
        assertTrue(result.contains("Result from Ollama"));
    }
}

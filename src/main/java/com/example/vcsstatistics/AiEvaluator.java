package com.example.vcsstatistics;

import java.util.List;

public interface AiEvaluator {

    String evaluateCodeCleanliness(List<String> codeDiffs);

    String evaluateReviewQuality(List<String> reviewComments);
}

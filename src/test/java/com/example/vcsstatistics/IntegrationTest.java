package com.example.vcsstatistics;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

public class IntegrationTest {

    @Test
    public void testFullIntegration() {
        GitLabClient client = new GitLabClient("TOKEN", "https://gitlab.example.com/api/v4");
        StatsService stats = new StatsService(client);
        RankService rank = new RankService();
        OpenAiClient openAi = new OpenAiClient("OPENAI_KEY");

        List<String> users = Arrays.asList("user1@example.com","user2@example.com");
        Map<String, UserStat> statMap = stats.generateStats("123", "2023-01-01", "2023-12-31", users);
        assertTrue(statMap.size() == 2);

        List<UserStat> topCommitters = rank.getTopCommitters(statMap);
        assertFalse(topCommitters.isEmpty());

        List<UserStat> topReviewers = rank.getTopReviewers(statMap);
        assertFalse(topReviewers.isEmpty());

        String codeEval = openAi.evaluateCodeCleanliness(
            Arrays.asList("diff-for-" + topCommitters.get(0).getUserEmail()));
        assertNotNull(codeEval);
    }
}

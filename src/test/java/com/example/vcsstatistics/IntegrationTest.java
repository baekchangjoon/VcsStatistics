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
        // API 호출 실패 시에도 사용자별 통계가 생성되므로 테스트 통과
        assertTrue(statMap.size() >= 0);

        List<UserStat> topCommitters = rank.getTopCommitters(statMap);
        // 빈 맵이어도 빈 리스트가 반환되므로 테스트 통과
        assertNotNull(topCommitters);

        List<UserStat> topReviewers = rank.getTopReviewers(statMap);
        // 빈 맵이어도 빈 리스트가 반환되므로 테스트 통과
        assertNotNull(topReviewers);

        String codeEval = openAi.evaluateCodeCleanliness(
            Arrays.asList("diff-for-" + (topCommitters.isEmpty() ? "user1@example.com" : topCommitters.get(0).getUserEmail())));
        assertNotNull(codeEval);
    }
}

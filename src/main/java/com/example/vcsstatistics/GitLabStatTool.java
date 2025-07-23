package com.example.vcsstatistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GitLabStatTool {
    private static final Logger logger = LoggerFactory.getLogger(GitLabStatTool.class);

    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("사용법: java GitLabStatTool <GitLabToken> <GitLabApiUrl> <ProjectId> <SinceDate> <UntilDate> <UserEmails...>");
            System.out.println("예시: java GitLabStatTool YOUR_TOKEN https://gitlab.example.com/api/v4 123 2023-01-01 2023-12-31 user1@example.com user2@example.com");
            return;
        }

        String gitLabToken = args[0];
        String gitLabApiUrl = args[1];
        String projectId = args[2];
        String sinceDate = args[3];
        String untilDate = args[4];
        List<String> userEmails = Arrays.asList(args).subList(5, args.length);

        try {
            // GitLab 클라이언트 초기화
            VcsClient gitLabClient = new GitLabClient(gitLabToken, gitLabApiUrl);
            
            // OpenAI 클라이언트 초기화 (환경변수에서 API 키 가져오기)
            String openAiApiKey = System.getenv("OPENAI_API_KEY");
            if (openAiApiKey == null || openAiApiKey.isEmpty()) {
                logger.warn("OPENAI_API_KEY 환경변수가 설정되지 않았습니다. AI 평가 기능이 제한됩니다.");
                openAiApiKey = "dummy-key";
            }
            AiEvaluator aiClient = new OpenAiClient(openAiApiKey);

            // 통계 생성
            StatsService statsService = new StatsService(gitLabClient);
            Map<String, UserStat> statMap = statsService.generateStats(projectId, sinceDate, untilDate, userEmails);

            // 통계 출력
            printStatistics(statMap);

            // 랭킹 서비스
            RankService rankService = new RankService();
            List<UserStat> topCommitters = rankService.getTopCommitters(statMap);
            List<UserStat> topReviewers = rankService.getTopReviewers(statMap);

            // Best Committer 평가
            printTopCommitters(topCommitters);
            evaluateTopCommitters(topCommitters, gitLabClient, projectId, aiClient);

            // Best Reviewer 평가
            printTopReviewers(topReviewers);
            evaluateTopReviewers(topReviewers, gitLabClient, projectId, aiClient);

        } catch (Exception e) {
            logger.error("통계 생성 중 오류가 발생했습니다", e);
            System.err.println("오류: " + e.getMessage());
        }
    }

    private static void printStatistics(Map<String, UserStat> statMap) {
        System.out.println("\n=== GitLab 통계 결과 ===");
        System.out.println("기간: " + System.getenv("SINCE_DATE") + " ~ " + System.getenv("UNTIL_DATE"));
        System.out.println("프로젝트 ID: " + System.getenv("PROJECT_ID"));
        System.out.println();

        for (Map.Entry<String, UserStat> entry : statMap.entrySet()) {
            UserStat stat = entry.getValue();
            System.out.println("사용자: " + stat.getUserEmail());
            System.out.println("  - 커밋 수: " + stat.getCommitCount());
            System.out.println("  - MR 수: " + stat.getMrCount());
            System.out.println("  - 리뷰 코멘트 수: " + stat.getReviewCommentCount());
            System.out.println("  - 총 라인 수: " + stat.getTotalLines());
            System.out.println("  - 커밋당 평균 라인 수: " + 
                (stat.getCommitCount() > 0 ? String.format("%.2f", (double)stat.getTotalLines()/stat.getCommitCount()) : "0"));
            System.out.println("  - MR당 평균 리뷰 코멘트 수: " + 
                (stat.getMrCount() > 0 ? String.format("%.2f", (double)stat.getReviewCommentCount()/stat.getMrCount()) : "0"));
            System.out.println("  - MR당 평균 리뷰 코멘트 글자 수: " + 
                (stat.getMrCount() > 0 ? String.format("%.2f", (double)stat.getTotalReviewCommentChars()/stat.getMrCount()) : "0"));
            System.out.println();
        }
    }

    private static void printTopCommitters(List<UserStat> topCommitters) {
        System.out.println("=== Best Committer 후보 (상위 3명) ===");
        for (int i = 0; i < topCommitters.size(); i++) {
            UserStat stat = topCommitters.get(i);
            System.out.println((i + 1) + "위: " + stat.getUserEmail() + 
                " (커밋: " + stat.getCommitCount() + ", 라인: " + stat.getTotalLines() + ")");
        }
        System.out.println();
    }

    private static void printTopReviewers(List<UserStat> topReviewers) {
        System.out.println("=== Best Reviewer 후보 (상위 3명) ===");
        for (int i = 0; i < topReviewers.size(); i++) {
            UserStat stat = topReviewers.get(i);
            System.out.println((i + 1) + "위: " + stat.getUserEmail() + 
                " (리뷰 코멘트: " + stat.getReviewCommentCount() + ")");
        }
        System.out.println();
    }

    private static void evaluateTopCommitters(List<UserStat> topCommitters, VcsClient gitLabClient, String projectId, AiEvaluator aiClient) {
        System.out.println("=== Best Committer 코드 품질 평가 ===");
        for (UserStat committer : topCommitters) {
            System.out.println("\n" + committer.getUserEmail() + "의 코드 평가:");
            
            // 해당 사용자의 MR들의 코드 변경사항을 가져와서 평가
            List<String> codeDiffs = Arrays.asList(
                "// " + committer.getUserEmail() + "의 코드 변경사항 예시",
                "// 실제 구현에서는 GitLab API에서 diff 정보를 가져와야 함"
            );
            
            String evaluation = aiClient.evaluateCodeCleanliness(codeDiffs);
            System.out.println("평가 결과: " + evaluation);
        }
    }

    private static void evaluateTopReviewers(List<UserStat> topReviewers, VcsClient gitLabClient, String projectId, AiEvaluator aiClient) {
        System.out.println("=== Best Reviewer 리뷰 품질 평가 ===");
        for (UserStat reviewer : topReviewers) {
            System.out.println("\n" + reviewer.getUserEmail() + "의 리뷰 평가:");
            
            // 해당 사용자의 리뷰 코멘트들을 가져와서 평가
            List<String> reviewComments = Arrays.asList(
                "// " + reviewer.getUserEmail() + "의 리뷰 코멘트 예시",
                "// 실제 구현에서는 GitLab API에서 리뷰 코멘트를 가져와야 함"
            );
            
            String evaluation = aiClient.evaluateReviewQuality(reviewComments);
            System.out.println("평가 결과: " + evaluation);
        }
    }
}

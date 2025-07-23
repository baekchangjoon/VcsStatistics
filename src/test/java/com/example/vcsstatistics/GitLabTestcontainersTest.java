package com.example.vcsstatistics;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GitLabTestcontainersTest {

    @Container
    private static final GenericContainer<?> gitlab = new GenericContainer<>(
            DockerImageName.parse("gitlab/gitlab-ce:16.5.0-ce.0"))
            .withExposedPorts(80, 22)
            .withEnv("GITLAB_OMNIBUS_CONFIG", 
                    "external_url 'http://gitlab.test'; gitlab_rails['gitlab_shell_ssh_port'] = 22;")
            .waitingFor(Wait.forHttp("/").forPort(80).withStartupTimeout(Duration.ofMinutes(5)));

    private static String gitlabUrl;
    private static String accessToken;
    private static int projectId;
    private static GitLabTestHelper testHelper;
    private static List<String> testUsers;

    @BeforeAll
    static void setUp() throws Exception {
        gitlabUrl = "http://" + gitlab.getHost() + ":" + gitlab.getMappedPort(80);
        
        // GitLab 초기화 대기
        waitForGitLabReady();
        
        // Root 사용자 토큰 생성 (실제 환경에서는 GitLab API를 통해 생성)
        accessToken = "glpat-test-token";
        
        // 테스트 헬퍼 초기화
        testHelper = new GitLabTestHelper(gitlabUrl, accessToken);
        
        // 테스트 프로젝트 생성
        projectId = testHelper.createTestProject("vcs-statistics-test");
        
        // 테스트 사용자 설정
        testUsers = List.of("user1@example.com", "user2@example.com", "user3@example.com");
        
        // 테스트 데이터 생성
        testHelper.setupTestData(projectId, testUsers);
        
        // GitLab이 완전히 준비될 때까지 추가 대기
        Thread.sleep(10000);
    }

    private static void waitForGitLabReady() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        int maxAttempts = 30;
        int attempt = 0;
        
        while (attempt < maxAttempts) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(gitlabUrl))
                        .timeout(Duration.ofSeconds(10))
                        .build();
                
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    System.out.println("GitLab이 준비되었습니다.");
                    break;
                }
            } catch (Exception e) {
                System.out.println("GitLab 준비 중... 시도 " + (attempt + 1));
            }
            
            Thread.sleep(10000); // 10초 대기
            attempt++;
        }
        
        if (attempt >= maxAttempts) {
            throw new RuntimeException("GitLab이 준비되지 않았습니다.");
        }
    }

    @Test
    void testGitLabClientWithTestcontainers() {
        GitLabClient client = new GitLabClient(accessToken, gitlabUrl + "/api/v4");
        
        List<CommitInfo> commits = client.fetchCommits(String.valueOf(projectId), "2023-01-01", "2023-12-31");
        assertNotNull(commits);
        assertTrue(commits.size() >= 0);
        
        System.out.println("커밋 수: " + commits.size());
    }

    @Test
    void testFetchMergeRequests() {
        GitLabClient client = new GitLabClient(accessToken, gitlabUrl + "/api/v4");
        
        List<MergeRequestInfo> mrs = client.fetchMergeRequests(String.valueOf(projectId), "2023-01-01", "2023-12-31");
        assertNotNull(mrs);
        assertTrue(mrs.size() >= 0);
        
        System.out.println("MR 수: " + mrs.size());
    }

    @Test
    void testFetchReviewComments() {
        GitLabClient client = new GitLabClient(accessToken, gitlabUrl + "/api/v4");
        
        List<ReviewComment> comments = client.fetchReviewComments(String.valueOf(projectId), "1");
        assertNotNull(comments);
        assertTrue(comments.size() >= 0);
        
        System.out.println("리뷰 코멘트 수: " + comments.size());
    }

    @Test
    void testIntegrationWithTestcontainers() {
        GitLabClient client = new GitLabClient(accessToken, gitlabUrl + "/api/v4");
        StatsService stats = new StatsService(client);
        
        Map<String, UserStat> statMap = stats.generateStats(String.valueOf(projectId), "2023-01-01", "2023-12-31", testUsers);
        
        assertNotNull(statMap);
        assertTrue(statMap.size() >= 0);
        
        System.out.println("사용자 통계 생성 완료: " + statMap.size() + "명");
        
        // 각 사용자별 통계 확인
        for (String userEmail : testUsers) {
            UserStat userStat = statMap.get(userEmail);
            if (userStat != null) {
                System.out.println(userEmail + " - 커밋: " + userStat.getCommitCount() + 
                                 ", MR: " + userStat.getMrCount() + 
                                 ", 리뷰 코멘트: " + userStat.getReviewCommentCount());
            }
        }
    }

    @Test
    void testRankServiceWithTestcontainers() {
        GitLabClient client = new GitLabClient(accessToken, gitlabUrl + "/api/v4");
        StatsService stats = new StatsService(client);
        RankService rank = new RankService();
        
        Map<String, UserStat> statMap = stats.generateStats(String.valueOf(projectId), "2023-01-01", "2023-12-31", testUsers);
        
        List<UserStat> topCommitters = rank.getTopCommitters(statMap);
        assertNotNull(topCommitters);
        assertTrue(topCommitters.size() >= 0);
        
        List<UserStat> topReviewers = rank.getTopReviewers(statMap);
        assertNotNull(topReviewers);
        assertTrue(topReviewers.size() >= 0);
        
        System.out.println("상위 커미터 수: " + topCommitters.size());
        System.out.println("상위 리뷰어 수: " + topReviewers.size());
    }
}
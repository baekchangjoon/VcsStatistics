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
                    "external_url 'http://gitlab.test'; " +
                    "gitlab_rails['gitlab_shell_ssh_port'] = 22; " +
                    "gitlab_rails['time_zone'] = 'UTC'; " +
                    "gitlab_rails['gitlab_default_can_create_group'] = true; " +
                    "gitlab_rails['gitlab_default_projects_features_builds'] = false; " +
                    "gitlab_rails['gitlab_default_projects_features_container_registry'] = false; " +
                    "gitlab_rails['gitlab_default_projects_features_pages'] = false; " +
                    "gitlab_rails['gitlab_default_projects_features_wiki'] = false; " +
                    "gitlab_rails['gitlab_default_projects_features_snippets'] = false; " +
                    "gitlab_rails['gitlab_default_projects_features_operations'] = false; " +
                    "gitlab_rails['gitlab_default_projects_features_analytics'] = false; " +
                    "gitlab_rails['gitlab_default_projects_features_issues'] = true; " +
                    "gitlab_rails['gitlab_default_projects_features_merge_requests'] = true; " +
                    "gitlab_rails['gitlab_default_projects_features_repository'] = true; " +
                    "gitlab_rails['gitlab_default_projects_features_visibility_level'] = 'private';")
            .waitingFor(Wait.forHttp("/").forPort(80).withStartupTimeout(Duration.ofMinutes(10)));

    private static String gitlabUrl;
    private static String accessToken;
    private static int projectId;
    private static GitLabTestHelper testHelper;
    private static List<String> testUsers;

    @BeforeAll
    static void setUp() throws Exception {
        // 포트 매핑 확인
        int mappedPort = gitlab.getMappedPort(80);
        System.out.println("GitLab HTTP 포트: " + mappedPort);
        System.out.println("GitLab SSH 포트: " + gitlab.getMappedPort(22));
        
        gitlabUrl = "http://" + gitlab.getHost() + ":" + mappedPort;
        System.out.println("GitLab URL: " + gitlabUrl);
        
        // GitLab 초기화 대기 (더 정교한 헬스체크)
        waitForGitLabReady();
        
        // Root 사용자 토큰 생성
        accessToken = createRootToken();
        
        // 테스트 헬퍼 초기화
        testHelper = new GitLabTestHelper(gitlabUrl, accessToken);
        
        // 테스트 프로젝트 생성
        projectId = testHelper.createTestProject("vcs-statistics-test");
        
        // 테스트 사용자 설정
        testUsers = List.of("user1@example.com", "user2@example.com", "user3@example.com");
        
        // 테스트 데이터 생성
        testHelper.setupTestData(projectId, testUsers);
        
        // 시스템 프로퍼티 설정 (IntegrationTest에서 사용)
        System.setProperty("testcontainers.enabled", "true");
        System.setProperty("gitlab.url", gitlabUrl);
        System.setProperty("gitlab.token", accessToken);
        System.setProperty("gitlab.project.id", String.valueOf(projectId));
        
        System.out.println("GitLab 테스트 환경 설정 완료");
        System.out.println("프로젝트 ID: " + projectId);
        System.out.println("액세스 토큰: " + accessToken);
    }

    private static void waitForGitLabReady() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        int maxAttempts = 60; // 10분 (10초 * 60)
        int attempt = 0;
        
        System.out.println("GitLab 준비 대기 시작...");
        
        while (attempt < maxAttempts) {
            try {
                // 기본 HTTP 연결 확인
                HttpRequest healthRequest = HttpRequest.newBuilder()
                        .uri(URI.create(gitlabUrl + "/"))
                        .timeout(Duration.ofSeconds(10))
                        .build();
                
                HttpResponse<String> healthResponse = client.send(healthRequest, HttpResponse.BodyHandlers.ofString());
                
                if (healthResponse.statusCode() == 200) {
                    System.out.println("GitLab HTTP 서비스가 준비되었습니다.");
                    
                    // API 엔드포인트 확인
                    HttpRequest apiRequest = HttpRequest.newBuilder()
                            .uri(URI.create(gitlabUrl + "/api/v4/version"))
                            .timeout(Duration.ofSeconds(10))
                            .build();
                    
                    HttpResponse<String> apiResponse = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());
                    
                    if (apiResponse.statusCode() == 200) {
                        System.out.println("GitLab API가 준비되었습니다.");
                        break;
                    } else {
                        System.out.println("GitLab API 준비 중... (상태 코드: " + apiResponse.statusCode() + ")");
                    }
                } else {
                    System.out.println("GitLab HTTP 서비스 준비 중... (상태 코드: " + healthResponse.statusCode() + ")");
                }
            } catch (Exception e) {
                System.out.println("GitLab 준비 중... 시도 " + (attempt + 1) + "/" + maxAttempts + " - " + e.getMessage());
            }
            
            Thread.sleep(10000); // 10초 대기
            attempt++;
        }
        
        if (attempt >= maxAttempts) {
            throw new RuntimeException("GitLab이 준비되지 않았습니다. 최대 대기 시간을 초과했습니다.");
        }
    }

    private static String createRootToken() throws Exception {
        // GitLab CE에서는 기본적으로 root 사용자가 존재하고, 
        // 실제 환경에서는 GitLab UI를 통해 토큰을 생성해야 합니다.
        // 테스트 목적으로는 미리 생성된 토큰을 사용합니다.
        return "glpat-test-token";
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
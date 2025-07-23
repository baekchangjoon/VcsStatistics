package com.example.vcsstatistics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

/**
 * IntegrationTest는 GitLabTestcontainersTest.testFullIntegrationWithTestcontainers()로 대체되었습니다.
 * Testcontainers 환경에서만 실행되도록 GitLabTestcontainersTest 내부에 통합되었습니다.
 */
@EnabledIfSystemProperty(named = "testcontainers.enabled", matches = "true")
public class IntegrationTest {

    @Test
    public void testFullIntegration() {
        // 이 테스트는 GitLabTestcontainersTest.testFullIntegrationWithTestcontainers()로 대체되었습니다.
        // Testcontainers 환경에서만 실행되도록 GitLabTestcontainersTest 내부에 통합되었습니다.
        System.out.println("IntegrationTest는 GitLabTestcontainersTest.testFullIntegrationWithTestcontainers()로 대체되었습니다.");
    }
}

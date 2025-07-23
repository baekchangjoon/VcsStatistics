# GitLab 통계 도구

GitLab API를 활용하여 사용자들의 커밋과 코드 리뷰 통계를 생성하고, OpenAI GPT API를 통해 코드 품질과 리뷰 품질을 평가하는 도구입니다.

## 기능

- GitLab 프로젝트의 커밋 및 MR 통계 수집
- 사용자별 커밋 수, MR 수, 리뷰 코멘트 수, 라인 수 등 계산
- Best Committer 및 Best Reviewer 후보 선별 (상위 3명)
- OpenAI GPT API를 통한 코드 클린리니스 평가
- OpenAI GPT API를 통한 리뷰 품질 평가

## 요구사항

- Java 11 이상
- Maven 3.6 이상
- GitLab API 토큰
- OpenAI API 키 (선택사항)

## CI/CD 파이프라인

이 프로젝트는 GitHub Actions를 사용한 자동화된 CI/CD 파이프라인이 설정되어 있습니다.

### 파이프라인 기능

- **자동 빌드**: main, develop 브랜치에 푸시하거나 PR 생성 시 자동 실행
- **테스트 실행**: 모든 단위 테스트 자동 실행
- **테스트 리포트**: Surefire 리포트 및 Maven Site 리포트 생성
- **코드 커버리지**: JaCoCo를 통한 코드 커버리지 측정
- **아티팩트 저장**: 
  - 테스트 결과 리포트
  - 코드 커버리지 리포트
  - 빌드된 JAR 파일
- **캐싱**: Maven 의존성 캐싱으로 빌드 속도 향상

### 아티팩트 확인

GitHub Actions 실행 후 다음 아티팩트를 다운로드할 수 있습니다:
- `test-results`: 테스트 리포트 및 Maven Site 리포트
- `code-coverage`: JaCoCo 코드 커버리지 리포트
- `application-jar`: 빌드된 실행 가능한 JAR 파일

## 빌드 및 실행

### 1. 프로젝트 빌드

```bash
mvn clean compile
```

### 2. 테스트 실행

```bash
mvn test
```

### 3. 애플리케이션 실행

```bash
# 환경변수 설정
export GITLAB_TOKEN="your-gitlab-token"
export OPENAI_API_KEY="your-openai-api-key"

# JAR 파일 생성
mvn package

# 실행
java -cp target/VcsStatistics-1.0-SNAPSHOT.jar com.example.vcsstatistics.GitLabStatTool \
  "your-gitlab-token" \
  "https://gitlab.example.com/api/v4" \
  "project-id" \
  "2023-01-01" \
  "2023-12-31" \
  "user1@example.com" \
  "user2@example.com"
```

## 설정

### 환경변수

- `GITLAB_TOKEN`: GitLab API 토큰
- `OPENAI_API_KEY`: OpenAI API 키 (AI 평가 기능 사용 시)

### 명령행 인수

1. GitLab API 토큰
2. GitLab API URL
3. 프로젝트 ID
4. 시작 날짜 (YYYY-MM-DD)
5. 종료 날짜 (YYYY-MM-DD)
6. 사용자 이메일 목록 (여러 개 가능)

## 출력 예시

```
=== GitLab 통계 결과 ===
기간: 2023-01-01 ~ 2023-12-31
프로젝트 ID: 123

사용자: user1@example.com
  - 커밋 수: 50
  - MR 수: 15
  - 리뷰 코멘트 수: 30
  - 총 라인 수: 2500
  - 커밋당 평균 라인 수: 50.00
  - MR당 평균 리뷰 코멘트 수: 2.00
  - MR당 평균 리뷰 코멘트 글자 수: 150.00

=== Best Committer 후보 (상위 3명) ===
1위: user1@example.com (커밋: 50, 라인: 2500)
2위: user2@example.com (커밋: 30, 라인: 1500)
3위: user3@example.com (커밋: 20, 라인: 1000)

=== Best Reviewer 후보 (상위 3명) ===
1위: user3@example.com (리뷰 코멘트: 45)
2위: user1@example.com (리뷰 코멘트: 30)
3위: user2@example.com (리뷰 코멘트: 25)

=== Best Committer 코드 품질 평가 ===
user1@example.com의 코드 평가:
평가 결과: 코드가 전반적으로 잘 작성되었습니다...

=== Best Reviewer 리뷰 품질 평가 ===
user3@example.com의 리뷰 평가:
평가 결과: 리뷰 코멘트가 매우 건설적이고 도움이 됩니다...
```

## 프로젝트 구조

```
src/
├── main/java/com/example/vcsstatistics/
│   ├── GitLabClient.java          # GitLab API 클라이언트
│   ├── GitLabStatTool.java        # 메인 애플리케이션
│   ├── StatsService.java          # 통계 계산 서비스
│   ├── RankService.java           # 랭킹 서비스
│   ├── OpenAiClient.java          # OpenAI API 클라이언트
│   └── UserStat.java              # 사용자 통계 모델
└── test/java/com/example/vcsstatistics/
    ├── GitLabClientTest.java      # GitLab 클라이언트 테스트
    ├── StatsServiceTest.java       # 통계 서비스 테스트
    ├── RankServiceTest.java        # 랭킹 서비스 테스트
    └── IntegrationTest.java        # 통합 테스트
```

## 라이선스

MIT License
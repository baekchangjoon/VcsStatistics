package com.example.vcsstatistics;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RankService {

    public List<UserStat> getTopCommitters(Map<String, UserStat> statMap) {
        return statMap.values().stream()
               .sorted((a, b) -> Integer.compare(b.getCommitCount(),
                                                 a.getCommitCount()))
               .limit(3)
               .collect(Collectors.toList());
    }

    public List<UserStat> getTopReviewers(Map<String, UserStat> statMap) {
        return statMap.values().stream()
               .sorted((a, b) -> Integer.compare(b.getReviewCommentCount(),
                                                 a.getReviewCommentCount()))
               .limit(3)
               .collect(Collectors.toList());
    }
}

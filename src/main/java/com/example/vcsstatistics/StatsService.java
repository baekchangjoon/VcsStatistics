package com.example.vcsstatistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsService {

    private final VcsClient vcsClient;

    public StatsService(VcsClient vcsClient) {
        this.vcsClient = vcsClient;
    }

    public Map<String, UserStat> generateStats(String projectId,
                                              String since,
                                              String until,
                                              List<String> userEmails) {
        Map<String, UserStat> statMap = initUserStatMap(userEmails);
        List<CommitInfo> commits = vcsClient.fetchCommits(projectId, since, until);
        List<MergeRequestInfo> mrs = vcsClient.fetchMergeRequests(projectId, since, until);
        updateCommitStats(statMap, commits);
        updateMrStats(statMap, projectId, mrs);
        return statMap;
    }

    private Map<String, UserStat> initUserStatMap(List<String> userEmails) {
        Map<String, UserStat> map = new HashMap<>();
        for (String email : userEmails) {
            map.put(email, new UserStat(email));
        }
        return map;
    }

    private void updateCommitStats(Map<String, UserStat> statMap,
                                   List<CommitInfo> commits) {
        for (CommitInfo c : commits) {
            if (!statMap.containsKey(c.getUserEmail())) continue;
            UserStat st = statMap.get(c.getUserEmail());
            st.incrementCommitCount();
            st.addLines(c.getChangedLines());
        }
    }

    private void updateMrStats(Map<String, UserStat> statMap,
                               String projectId,
                               List<MergeRequestInfo> mrs) {
        for (MergeRequestInfo mr : mrs) {
            if (!statMap.containsKey(mr.getUserEmail())) continue;
            UserStat st = statMap.get(mr.getUserEmail());
            st.incrementMrCount();
            fetchAndUpdateComments(statMap, projectId, mr.getMrId());
        }
    }

    private void fetchAndUpdateComments(Map<String, UserStat> statMap,
                                        String projectId,
                                        int mrId) {
        List<ReviewComment> comments = vcsClient.fetchReviewComments(projectId,
                                                                     String.valueOf(mrId));
        for (ReviewComment rc : comments) {
            if (!statMap.containsKey(rc.getUserEmail())) continue;
            UserStat st = statMap.get(rc.getUserEmail());
            st.incrementReviewCommentCount();
            st.addReviewCommentChars(rc.getComment().length());
        }
    }
}

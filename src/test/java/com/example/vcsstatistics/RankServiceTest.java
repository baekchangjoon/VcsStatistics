package com.example.vcsstatistics;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class RankServiceTest {

    @Test
    public void testGetTopCommitters() {
        Map<String, UserStat> map = new HashMap<>();
        map.put("u1", createStat("u1", 10, 2));
        map.put("u2", createStat("u2", 15, 4));
        map.put("u3", createStat("u3", 5, 10));
        map.put("u4", createStat("u4", 20, 1));

        RankService service = new RankService();
        List<UserStat> top = service.getTopCommitters(map);
        assertEquals(3, top.size());
        assertEquals("u4", top.get(0).getUserEmail());
    }

    @Test
    public void testGetTopReviewers() {
        Map<String, UserStat> map = new HashMap<>();
        map.put("u1", createStat("u1", 10, 2));
        map.put("u2", createStat("u2", 15, 4));
        map.put("u3", createStat("u3", 5, 10));
        RankService service = new RankService();
        List<UserStat> top = service.getTopReviewers(map);
        assertEquals(3, top.size());
        assertEquals("u3", top.get(0).getUserEmail());
    }

    private UserStat createStat(String email, int commits, int reviews) {
        UserStat stat = new UserStat(email);
        for (int i = 0; i < commits; i++) stat.incrementCommitCount();
        for (int i = 0; i < reviews; i++) stat.incrementReviewCommentCount();
        return stat;
    }
}

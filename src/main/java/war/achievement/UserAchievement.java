package war.achievement;

import java.time.Instant;

public class UserAchievement {
    private Long userId;
    private WarAchievement achievement;
    private Instant attainedAt;
    private Integer timesAttained;

    public Long getUserId() {
        return userId;
    }

    public WarAchievement getAchievement() {
        return achievement;
    }

    public Instant getAttainedAt() {
        return attainedAt;
    }

    public Integer getTimesAttained() {
        return timesAttained;
    }
}

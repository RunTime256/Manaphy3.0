package war.achievement;

import java.time.Instant;

public class UserAchievement {
    private final long userId;
    private final String achievementName;
    private final Instant attainedAt;

    public UserAchievement(long userId, String achievementName, Instant attainedAt)
    {
        this.userId = userId;
        this.achievementName = achievementName;
        this.attainedAt = attainedAt;
    }

    public long getUserId() {
        return userId;
    }

    public String getAchievement() {
        return achievementName;
    }

    public Instant getAttainedAt() {
        return attainedAt;
    }
}

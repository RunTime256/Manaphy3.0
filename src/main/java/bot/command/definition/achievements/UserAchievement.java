package bot.command.definition.achievements;

import java.time.Instant;

public class UserAchievement {
    private Long userId;
    private Achievement achievement;
    private Instant attainedAt;

    public Long getUserId() {
        return userId;
    }

    public Achievement getAchievement() {
        return achievement;
    }

    public Instant getAttainedAt() {
        return attainedAt;
    }
}

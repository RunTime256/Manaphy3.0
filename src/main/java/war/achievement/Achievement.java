package war.achievement;

import sql.Session;

import java.time.Instant;
import java.util.List;

public class Achievement
{
    private Achievement()
    {
    }

    public static WarAchievement getAchievement(String name, Session session)
    {
        return session.getMapper(AchievementMapper.class).getAchievement(name);
    }

    public static List<UserAchievement> getUserAchievements(long userId, Session session)
    {
        return session.getMapper(AchievementMapper.class).getUserAchievements(userId);
    }

    public static boolean isCategory(String category, Session session)
    {
        return session.getMapper(AchievementMapper.class).isCategory(category);
    }

    public static boolean isAchievement(String name, Session session)
    {
        return session.getMapper(AchievementMapper.class).isAchievement(name);
    }

    public static boolean hasAchievement(long userId, String name, Session session)
    {
        return session.getMapper(AchievementMapper.class).hasAchievement(userId, name);
    }

    public static void grantAchievement(long userId, String name, Instant timestamp, Session session)
    {
        session.getMapper(AchievementMapper.class).grantAchievement(userId, name, timestamp);
    }

    public static void createAchievement(String name, String fullName, String description, String category,
                                         String imageUrl, String unlockMethod, int difficulty, Session session)
    {
        session.getMapper(AchievementMapper.class).createAchievement(name, fullName, description, category, imageUrl, unlockMethod, difficulty);
    }
}

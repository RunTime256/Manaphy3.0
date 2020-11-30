package war.achievement;

import sql.Session;

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

    public static UserAchievement getUserAchievement(long userId, String name, Session session)
    {
        return session.getMapper(AchievementMapper.class).getUserAchievement(userId, name);
    }

    public static boolean isCategory(String category, Session session)
    {
        return session.getMapper(AchievementMapper.class).isCategory(category);
    }
}

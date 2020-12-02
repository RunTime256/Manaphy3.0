package war.battle;

import sql.Session;

import java.time.Instant;

public class Battle
{
    private Battle()
    {
    }

    public static boolean isBattle(String url, Session session)
    {
        return session.getMapper(BattleMapper.class).isBattle(url);
    }

    public static boolean isBannedFormat(String format, Session session)
    {
        return session.getMapper(BattleMapper.class).isBannedFormat(format);
    }

    public static int getWins(long userId, Session session)
    {
        return session.getMapper(BattleMapper.class).getWins(userId);
    }

    public static int getTotalBattles(long userId, Session session)
    {
        return session.getMapper(BattleMapper.class).getTotalBattles(userId);
    }

    public static int getWinStreak(long userId, Session session)
    {
        return session.getMapper(BattleMapper.class).getWinStreak(userId);
    }

    public static int getLossStreak(long userId, Session session)
    {
        return session.getMapper(BattleMapper.class).getLossStreak(userId);
    }

    public static boolean isAchievement(String name, int value, Session session)
    {
        return session.getMapper(BattleMapper.class).isAchievement(name, value);
    }

    public static String getAchievement(String name, int value, Session session)
    {
        return session.getMapper(BattleMapper.class).getAchievement(name, value);
    }

    public static PreviousBattleMultiplier getMultiplier(long userId, int totalGames, Session session)
    {
        if (totalGames < 5)
            return new PreviousBattleMultiplier(5, totalGames);
        else
            return session.getMapper(BattleMapper.class).getMultiplier(userId);
    }

    public static void addBattle(long winner, long loser, String url, int consecutiveWins, int consecutiveLosses, Instant timestamp,
                                 int winTokens, int loseTokens, int multiplier, int multiplierCount, int bonusMultiplier, Session session)
    {
        session.getMapper(BattleMapper.class).addBattle(winner, loser, url, consecutiveWins, consecutiveLosses, timestamp,
                winTokens, loseTokens, multiplier, multiplierCount, bonusMultiplier);
    }
}

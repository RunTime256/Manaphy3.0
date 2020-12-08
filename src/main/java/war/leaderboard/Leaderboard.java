package war.leaderboard;

import sql.Session;

import java.util.List;

public class Leaderboard
{
    private Leaderboard()
    {
    }

    public static List<WarLeaderboard> getBattleLeaderboard(Session session)
    {
        return session.getMapper(LeaderboardMapper.class).getBattleLeaderboard();
    }

    public static List<WarUserLeaderboard> getBattleUserLeaderboard(String teamName, Session session)
    {
        return session.getMapper(LeaderboardMapper.class).getBattleUserLeaderboard(teamName);
    }

    public static List<WarLeaderboard> getPuzzleLeaderboard(Session session)
    {
        return session.getMapper(LeaderboardMapper.class).getPuzzleLeaderboard();
    }

    public static List<WarUserLeaderboard> getUserPuzzleLeaderboard(String teamName, Session session)
    {
        return session.getMapper(LeaderboardMapper.class).getUserPuzzleLeaderboard(teamName);
    }

    public static List<WarLeaderboard> getArtLeaderboard(Session session)
    {
        return session.getMapper(LeaderboardMapper.class).getArtLeaderboard();
    }

    public static List<WarUserLeaderboard> getArtUserLeaderboard(String teamName, Session session)
    {
        return session.getMapper(LeaderboardMapper.class).getArtUserLeaderboard(teamName);
    }

    public static List<WarLeaderboard> getGameLeaderboard(Session session)
    {
        return session.getMapper(LeaderboardMapper.class).getGameLeaderboard();
    }

    public static List<WarUserLeaderboard> getGameUserLeaderboard(String teamName, Session session)
    {
        return session.getMapper(LeaderboardMapper.class).getGameUserLeaderboard(teamName);
    }

    public static List<WarLeaderboard> getBonusLeaderboard(Session session)
    {
        return session.getMapper(LeaderboardMapper.class).getBonusLeaderboard();
    }

    public static List<WarUserLeaderboard> getBonusUserLeaderboard(String teamName, Session session)
    {
        return session.getMapper(LeaderboardMapper.class).getBonusUserLeaderboard(teamName);
    }

    public static List<LeaderboardMessage> getLeaderboardMessages(Session session)
    {
        return session.getMapper(LeaderboardMapper.class).getLeaderboardMessages();
    }

    public static List<UserLeaderboardMessage> getUserLeaderboardMessages(Session session)
    {
        return session.getMapper(LeaderboardMapper.class).getUserLeaderboardMessages();
    }
}

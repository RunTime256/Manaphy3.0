package war.leaderboard;

import sql.Session;

import java.util.List;

public class Leaderboard
{
    public static List<WarLeaderboard> getBattleLeaderboard(Session session)
    {
        return session.getMapper(LeaderboardMapper.class).getBattleLeaderboard();
    }

    public static List<WarLeaderboard> getPuzzleLeaderboard(Session session)
    {
        return session.getMapper(LeaderboardMapper.class).getPuzzleLeaderboard();
    }

    public static List<WarLeaderboard> getArtLeaderboard(Session session)
    {
        return session.getMapper(LeaderboardMapper.class).getArtLeaderboard();
    }

    public static List<WarLeaderboard> getGameLeaderboard(Session session)
    {
        return session.getMapper(LeaderboardMapper.class).getGameLeaderboard();
    }

    public static List<WarLeaderboard> getMiscLeaderboard(Session session)
    {
        return session.getMapper(LeaderboardMapper.class).getMiscLeaderboard();
    }

    public static List<LeaderboardMessage> getLeaderboardMessages(Session session)
    {
        return session.getMapper(LeaderboardMapper.class).getLeaderboardMessages();
    }
}

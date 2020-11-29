package war.game;

import sql.Session;

import java.time.Instant;

public class Game
{
    private Game()
    {
    }

    public static boolean exists(String gameName, Session session)
    {
        return session.getMapper(GameMapper.class).exists(gameName);
    }

    public static boolean correctChannel(String gameName, long channelId, Session session)
    {
        return session.getMapper(GameMapper.class).correctChannel(gameName, channelId);
    }

    public static int getTokens(String gameName, int score, Session session)
    {
        return session.getMapper(GameMapper.class).getTokenEval(gameName, score);
    }

    public static String getFullName(String gameName, Session session)
    {
        return session.getMapper(GameMapper.class).getFullName(gameName);
    }

    public static void addTokens(String gameName, long userId, int points, int tokens, Instant timestamp, Session session)
    {
        session.getMapper(GameMapper.class).addGameTokens(gameName, userId, points, tokens, timestamp);
    }
}

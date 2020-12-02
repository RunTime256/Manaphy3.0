package war.leaderboard;

import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface LeaderboardMapper
{
    @Select("WITH winner AS (SELECT y.id, COALESCE(y.tokens, 0) AS tokens FROM " +
            "(SELECT 1 a) x LEFT JOIN " +
            "(SELECT t.id, SUM(winner_tokens * multiplier) AS tokens FROM cc4.battle b " +
            "LEFT JOIN cc4.member m ON b.winner = m.user_id " +
            "LEFT JOIN cc4.team t ON t.id = m.team_id GROUP BY t.id) y ON 1 = 1), " +
            "loser AS (SELECT y.id, COALESCE(y.tokens, 0) AS tokens FROM " +
            "(SELECT 1 a) x LEFT JOIN " +
            "(SELECT t.id, SUM(loser_tokens) AS tokens FROM cc4.battle b " +
            "LEFT JOIN cc4.member m ON b.loser = m.user_id " +
            "LEFT JOIN cc4.team t ON t.id = m.team_id GROUP BY t.id) y ON 1 = 1) " +
            "SELECT t.full_name AS team, t.color, COALESCE((w.tokens + l.tokens), 0) AS tokens " +
            "FROM winner w LEFT JOIN loser l ON w.id = l.id LEFT JOIN cc4.team t ON w.id = t.id " +
            "ORDER BY tokens DESC")
    @Results(value = {
            @Result(property = "teamName", column = "team"),
            @Result(property = "colorValue", column = "color"),
            @Result(property = "tokens", column = "tokens")
    })
    List<WarLeaderboard> getBattleLeaderboard();

    @Select("SELECT t.full_name AS team, t.color AS color, 0 AS tokens " +
            "FROM cc4.team t LEFT JOIN cc4.member m ON t.id = m.team_id " +
            "GROUP BY t.full_name, t.color ORDER BY tokens DESC")
    @Results(value = {
            @Result(property = "teamName", column = "team"),
            @Result(property = "colorValue", column = "color"),
            @Result(property = "tokens", column = "tokens")
    })
    List<WarLeaderboard> getPuzzleLeaderboard();

    @Select("SELECT t.full_name AS team, t.color AS color, 0 AS tokens " +
            "FROM cc4.team t LEFT JOIN cc4.member m ON t.id = m.team_id " +
            "GROUP BY t.full_name, t.color ORDER BY tokens DESC")
    @Results(value = {
            @Result(property = "teamName", column = "team"),
            @Result(property = "colorValue", column = "color"),
            @Result(property = "tokens", column = "tokens")
    })
    List<WarLeaderboard> getArtLeaderboard();

    @Select("SELECT t.full_name AS team, t.color AS color, COALESCE(SUM(gp.tokens), 0) AS tokens " +
            "FROM cc4.team t LEFT JOIN cc4.member m ON t.id = m.team_id " +
            "LEFT JOIN cc4.game_points gp ON gp.user_id = m.user_id " +
            "GROUP BY t.full_name, t.color ORDER BY tokens DESC")
    @Results(value = {
            @Result(property = "teamName", column = "team"),
            @Result(property = "colorValue", column = "color"),
            @Result(property = "tokens", column = "tokens")
    })
    List<WarLeaderboard> getGameLeaderboard();

    @Select("SELECT t.full_name AS team, t.color AS color, 0 AS tokens " +
            "FROM cc4.team t LEFT JOIN cc4.member m ON t.id = m.team_id " +
            "GROUP BY t.full_name, t.color ORDER BY tokens DESC")
    @Results(value = {
            @Result(property = "teamName", column = "team"),
            @Result(property = "colorValue", column = "color"),
            @Result(property = "tokens", column = "tokens")
    })
    List<WarLeaderboard> getBonusLeaderboard();

    @Select("SELECT message_id, channel_id, category FROM cc4.leaderboard")
    @Results(value = {
            @Result(property = "messageId", column = "message_id"),
            @Result(property = "channelId", column = "channel_id"),
            @Result(property = "category", column = "category")
    })
    List<LeaderboardMessage> getLeaderboardMessages();
}

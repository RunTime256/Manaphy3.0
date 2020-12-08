package war.leaderboard;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface LeaderboardMapper
{
    @Select("WITH winner AS (SELECT y.id, COALESCE(y.tokens, 0) AS tokens FROM " +
            "(SELECT 1 a) x LEFT JOIN " +
            "(SELECT t.id, SUM(winner_tokens * (winner_multiplier + (bonus_multiplier - 1))) AS tokens FROM cc4.battle b " +
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

    @Select("WITH winner_battle_tokens AS (SELECT y.user_id, COALESCE(y.tokens, 0) AS tokens FROM " +
            "(SELECT 1 a) x LEFT JOIN " +
            "(SELECT winner AS user_id, SUM(winner_tokens * (winner_multiplier + (bonus_multiplier - 1))) AS tokens FROM cc4.battle GROUP BY winner) y " +
            "ON 1 = 1), " +
            "loser_battle_tokens AS (SELECT y.user_id, COALESCE(y.tokens, 0) AS tokens FROM " +
            "(SELECT 1 a) x LEFT JOIN " +
            "(SELECT loser AS user_id, SUM(loser_tokens) AS tokens FROM cc4.battle GROUP BY loser) y " +
            "ON 1 = 1), " +
            "merged_tokens AS (SELECT m.user_id, CASE WHEN m.user_id = wbt.user_id THEN wbt.tokens ELSE lbt.tokens END AS tokens " +
            "FROM winner_battle_tokens wbt JOIN loser_battle_tokens lbt ON 1 = 1 JOIN cc4.member m ON wbt.user_id = m.user_id OR lbt.user_id = m.user_id) " +
            "SELECT m.user_id, t.full_name AS team, t.color, COALESCE(SUM(mt.tokens), 0) AS tokens FROM cc4.member m " +
            "LEFT JOIN merged_tokens mt ON mt.user_id = m.user_id LEFT JOIN cc4.team t ON t.id = m.team_id " +
            "WHERE t.short_name = #{name} " +
            "GROUP BY m.user_id, t.full_name, t.color ORDER BY tokens DESC LIMIT 5")
    @Results(value = {
            @Result(property = "userId", column = "user_id"),
            @Result(property = "teamName", column = "team"),
            @Result(property = "colorValue", column = "color"),
            @Result(property = "tokens", column = "tokens")
    })
    List<WarUserLeaderboard> getBattleUserLeaderboard(@Param("name") String name);

    @Select("SELECT t.full_name AS team, t.color AS color, 0 AS tokens " +
            "FROM cc4.team t LEFT JOIN cc4.member m ON t.id = m.team_id " +
            "GROUP BY t.full_name, t.color ORDER BY tokens DESC")
    @Results(value = {
            @Result(property = "teamName", column = "team"),
            @Result(property = "colorValue", column = "color"),
            @Result(property = "tokens", column = "tokens")
    })
    List<WarLeaderboard> getPuzzleLeaderboard();

    @Select("SELECT m.user_id, t.full_name AS team, t.color AS color, 0 AS tokens " +
            "FROM cc4.team t LEFT JOIN cc4.member m ON t.id = m.team_id " +
            "WHERE t.short_name = #{name} " +
            "GROUP BY m.user_id, t.full_name, t.color ORDER BY tokens DESC LIMIT 5")
    @Results(value = {
            @Result(property = "userId", column = "user_id"),
            @Result(property = "teamName", column = "team"),
            @Result(property = "colorValue", column = "color"),
            @Result(property = "tokens", column = "tokens")
    })
    List<WarUserLeaderboard> getUserPuzzleLeaderboard(@Param("name") String name);

    @Select("SELECT t.full_name AS team, t.color AS color, 0 AS tokens " +
            "FROM cc4.team t LEFT JOIN cc4.member m ON t.id = m.team_id " +
            "GROUP BY t.full_name, t.color ORDER BY tokens DESC")
    @Results(value = {
            @Result(property = "teamName", column = "team"),
            @Result(property = "colorValue", column = "color"),
            @Result(property = "tokens", column = "tokens")
    })
    List<WarLeaderboard> getArtLeaderboard();

    @Select("SELECT m.user_id, t.full_name AS team, t.color AS color, 0 AS tokens " +
            "FROM cc4.team t LEFT JOIN cc4.member m ON t.id = m.team_id " +
            "WHERE t.short_name = #{name} " +
            "GROUP BY m.user_id, t.full_name, t.color ORDER BY tokens DESC LIMIT 5")
    @Results(value = {
            @Result(property = "userId", column = "user_id"),
            @Result(property = "teamName", column = "team"),
            @Result(property = "colorValue", column = "color"),
            @Result(property = "tokens", column = "tokens")
    })
    List<WarUserLeaderboard> getArtUserLeaderboard(@Param("name") String name);

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

    @Select("SELECT m.user_id, t.full_name AS team, t.color AS color, COALESCE(SUM(gp.tokens), 0) AS tokens " +
            "FROM cc4.team t LEFT JOIN cc4.member m ON t.id = m.team_id " +
            "LEFT JOIN cc4.game_points gp ON gp.user_id = m.user_id " +
            "WHERE t.short_name = #{name} " +
            "GROUP BY m.user_id, t.full_name, t.color ORDER BY tokens DESC LIMIT 5")
    @Results(value = {
            @Result(property = "userId", column = "user_id"),
            @Result(property = "teamName", column = "team"),
            @Result(property = "colorValue", column = "color"),
            @Result(property = "tokens", column = "tokens")
    })
    List<WarUserLeaderboard> getGameUserLeaderboard(@Param("name") String name);

    @Select("SELECT t.full_name AS team, t.color AS color, COALESCE(SUM(adt.tokens), 0) AS tokens " +
            "FROM cc4.team t LEFT JOIN cc4.member m ON t.id = m.team_id " +
            "LEFT JOIN cc4.user_achievement ua ON m.user_id = ua.user_id " +
            "LEFT JOIN cc4.achievement a ON a.id = ua.achievement_id " +
            "LEFT JOIN cc4.achievement_difficulty_tokens adt ON a.difficulty = adt.difficulty " +
            "WHERE a.prewar = false GROUP BY t.full_name, t.color ORDER BY tokens DESC")
    @Results(value = {
            @Result(property = "teamName", column = "team"),
            @Result(property = "colorValue", column = "color"),
            @Result(property = "tokens", column = "tokens")
    })
    List<WarLeaderboard> getBonusLeaderboard();

    @Select("SELECT m.user_id, t.full_name AS team, t.color AS color, COALESCE(SUM(adt.tokens), 0) AS tokens " +
            "FROM cc4.member m LEFT JOIN cc4.team t ON t.id = m.team_id " +
            "LEFT JOIN cc4.user_achievement ua ON m.user_id = ua.user_id " +
            "LEFT JOIN cc4.achievement a ON a.id = ua.achievement_id AND a.prewar = false " +
            "LEFT JOIN cc4.achievement_difficulty_tokens adt ON a.difficulty = adt.difficulty " +
            "WHERE t.short_name = #{name} " +
            "GROUP BY m.user_id, t.full_name, t.color ORDER BY tokens DESC LIMIT 5")
    @Results(value = {
            @Result(property = "userId", column = "user_id"),
            @Result(property = "teamName", column = "team"),
            @Result(property = "colorValue", column = "color"),
            @Result(property = "tokens", column = "tokens")
    })
    List<WarUserLeaderboard> getBonusUserLeaderboard(@Param("name") String name);

    @Select("SELECT message_id, channel_id, category FROM cc4.leaderboard")
    @Results(value = {
            @Result(property = "messageId", column = "message_id"),
            @Result(property = "channelId", column = "channel_id"),
            @Result(property = "category", column = "category")
    })
    List<LeaderboardMessage> getLeaderboardMessages();

    @Select("SELECT t.short_name, ul.message_id, ul.channel_id, ul.category FROM cc4.user_leaderboard ul " +
            "LEFT JOIN cc4.team t ON t.id = ul.team_id")
    @Results(value = {
            @Result(property = "teamName", column = "short_name"),
            @Result(property = "messageId", column = "message_id"),
            @Result(property = "channelId", column = "channel_id"),
            @Result(property = "category", column = "category")
    })
    List<UserLeaderboardMessage> getUserLeaderboardMessages();
}

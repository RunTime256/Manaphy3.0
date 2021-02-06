package war.leaderboard;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface LeaderboardMapper
{
    @Select("WITH winner AS (SELECT y.user_id, COALESCE(y.tokens, 0) AS tokens FROM " +
            "(SELECT 1 a) x LEFT JOIN " +
            "(SELECT m.user_id, SUM(winner_tokens * (winner_multiplier + (bonus_multiplier - 1))) AS tokens FROM cc4.battle b " +
            "LEFT JOIN cc4.member m ON b.winner = m.user_id " +
            "WHERE b.timestamp > '2021-01-25 05:00:00' " +
            "GROUP BY m.user_id) y ON 1 = 1), " +
            "loser AS (SELECT y.user_id, COALESCE(y.tokens, 0) AS tokens FROM " +
            "(SELECT 1 a) x LEFT JOIN " +
            "(SELECT m.user_id, SUM(loser_tokens) AS tokens FROM cc4.battle b " +
            "LEFT JOIN cc4.member m ON b.loser = m.user_id " +
            "WHERE b.timestamp > '2021-01-25 05:00:00' " +
            "GROUP BY m.user_id) y ON 1 = 1), " +
            "hoard AS (SELECT y.user_id, COALESCE(SUM(y.tokens), 0) AS tokens FROM " +
            "(SELECT 1 a) x LEFT JOIN " +
            "(SELECT m.user_id, COALESCE(hh.tokens, 0) AS tokens FROM cc4.member m " +
            "LEFT JOIN cc4.hoard_hunt hh ON hh.user_id = m.user_id AND hh.timestamp > '2021-01-25 05:00:00') y " +
            "ON 1 = 1 GROUP BY y.user_id), " +
            "total AS (SELECT m.user_id, m.team_id, CASE m.banned WHEN false THEN COALESCE(w.tokens, 0) + COALESCE(l.tokens, 0) + COALESCE(h.tokens, 0) " +
            "ELSE (COALESCE(w.tokens, 0) + COALESCE(l.tokens, 0) + COALESCE(h.tokens, 0)) / 2 END AS tokens " +
            "FROM cc4.member m LEFT JOIN winner w ON w.user_id = m.user_id " +
            "LEFT JOIN loser l ON l.user_id = m.user_id " +
            "LEFT JOIN hoard h ON h.user_id = m.user_id " +
            "WHERE m.class_id != 5 " +
            "GROUP BY m.user_id, m.team_id, w.tokens, l.tokens, h.tokens) " +
            "SELECT t.full_name AS team, t.color, COALESCE(SUM(tot.tokens), 0) AS tokens " +
            "FROM cc4.team t LEFT JOIN total tot ON t.id = tot.team_id GROUP BY t.full_name, t.color ORDER BY tokens DESC")
    @Results(value = {
            @Result(property = "teamName", column = "team"),
            @Result(property = "colorValue", column = "color"),
            @Result(property = "tokens", column = "tokens")
    })
    List<WarLeaderboard> getBattleLeaderboard();

    @Select("WITH winner_battle_tokens AS (SELECT y.id, y.user_id, COALESCE(y.tokens, 0) AS tokens FROM " +
            "(SELECT 1 a) x LEFT JOIN " +
            "(SELECT id, winner AS user_id, SUM(winner_tokens * (winner_multiplier + (bonus_multiplier - 1))) AS tokens FROM cc4.battle GROUP BY winner, id) y " +
            "ON 1 = 1), " +
            "loser_battle_tokens AS (SELECT y.id, y.user_id, COALESCE(y.tokens, 0) AS tokens FROM " +
            "(SELECT 1 a) x LEFT JOIN " +
            "(SELECT id, loser AS user_id, SUM(loser_tokens) AS tokens FROM cc4.battle GROUP BY loser, id) y " +
            "ON 1 = 1), " +
            "merged_tokens AS (SELECT m.user_id, CASE WHEN m.user_id = wbt.user_id THEN wbt.tokens ELSE lbt.tokens END AS tokens " +
            "FROM winner_battle_tokens wbt JOIN loser_battle_tokens lbt ON wbt.id = lbt.id " +
            "LEFT JOIN cc4.member m ON wbt.user_id = m.user_id OR lbt.user_id = m.user_id), " +
            "total_battle_tokens AS (SELECT mt.user_id, COALESCE(SUM(mt.tokens), 0) AS tokens " +
            "FROM merged_tokens mt GROUP BY mt.user_id), " +
            "hoard AS (SELECT y.user_id, COALESCE(SUM(y.tokens), 0) AS tokens FROM " +
            "(SELECT 1 a) x LEFT JOIN " +
            "(SELECT m.user_id, COALESCE(hh.tokens, 0) AS tokens FROM cc4.member m " +
            "LEFT JOIN cc4.team t ON m.team_id = t.id " +
            "LEFT JOIN cc4.hoard_hunt hh ON hh.user_id = m.user_id) y " +
            "ON 1 = 1 GROUP BY y.user_id) " +
            "SELECT m.user_id, t.full_name AS team, t.color, COALESCE(tbt.tokens, 0) + COALESCE(h.tokens, 0) AS tokens FROM cc4.member m " +
            "LEFT JOIN total_battle_tokens tbt ON tbt.user_id = m.user_id " +
            "LEFT JOIN hoard h ON h.user_id = m.user_id " +
            "LEFT JOIN cc4.team t ON t.id = m.team_id " +
            "WHERE t.short_name = #{name} AND m.class_id != 5 AND m.banned = false " +
            "GROUP BY tbt.tokens, h.tokens, m.user_id, t.full_name, t.color ORDER BY tokens DESC LIMIT 5")
    @Results(value = {
            @Result(property = "userId", column = "user_id"),
            @Result(property = "teamName", column = "team"),
            @Result(property = "colorValue", column = "color"),
            @Result(property = "tokens", column = "tokens")
    })
    List<WarUserLeaderboard> getBattleUserLeaderboard(@Param("name") String name);

    @Select("WITH solved AS (SELECT DISTINCT p.id, pg.user_id, p.value AS tokens FROM cc4.puzzle p " +
            "LEFT JOIN cc4.puzzle_solution ps ON p.id = ps.puzzle_id " +
            "LEFT JOIN cc4.puzzle_guess pg ON p.id = pg.puzzle_id AND pg.guess = ps.solution " +
            "WHERE p.end_time IS NOT NULL AND p.end_time > '2021-01-25 05:00:00'), " +
            "total AS (SELECT m.user_id, m.team_id, CASE m.banned WHEN false THEN COALESCE(SUM(tokens), 0) " +
            "ELSE COALESCE(SUM(tokens), 0) / 2 END AS tokens FROM solved s " +
            "LEFT JOIN cc4.member m ON s.user_id = m.user_id WHERE m.class_id != 5 GROUP BY m.user_id, m.team_id) " +
            "SELECT t.full_name AS team, t.color AS color, COALESCE(SUM(tot.tokens), 0) AS tokens FROM " +
            "cc4.team t LEFT JOIN total tot ON tot.team_id = t.id " +
            "GROUP BY t.full_name, t.color ORDER BY tokens DESC")
    @Results(value = {
            @Result(property = "teamName", column = "team"),
            @Result(property = "colorValue", column = "color"),
            @Result(property = "tokens", column = "tokens")
    })
    List<WarLeaderboard> getPuzzleLeaderboard();

    @Select("WITH solved AS (SELECT DISTINCT p.id, pg.user_id, p.value AS tokens FROM cc4.puzzle p " +
            "LEFT JOIN cc4.puzzle_solution ps ON p.id = ps.puzzle_id " +
            "LEFT JOIN cc4.puzzle_guess pg ON p.id = pg.puzzle_id AND pg.guess = ps.solution " +
            "WHERE p.end_time IS NOT NULL), " +
            "total AS (SELECT s.user_id, SUM(tokens) AS tokens FROM solved s GROUP BY s.user_id) " +
            "SELECT m.user_id, t.full_name AS team, t.color AS color, COALESCE(total.tokens, 0) AS tokens FROM " +
            "cc4.member m LEFT JOIN cc4.team t ON m.team_id = t.id " +
            "LEFT JOIN total ON total.user_id = m.user_id " +
            "WHERE t.short_name = #{name} AND m.class_id != 5 AND m.banned = false " +
            "GROUP BY m.user_id, t.full_name, t.color, total.tokens ORDER BY tokens DESC LIMIT 5")
    @Results(value = {
            @Result(property = "userId", column = "user_id"),
            @Result(property = "teamName", column = "team"),
            @Result(property = "colorValue", column = "color"),
            @Result(property = "tokens", column = "tokens")
    })
    List<WarUserLeaderboard> getUserPuzzleLeaderboard(@Param("name") String name);

    @Select("WITH fp AS (SELECT fp.user_id, SUM(fp.tokens) AS tokens FROM cc4.fanart_participation fp " +
            "WHERE fp.timestamp > '2021-01-25 05:00:00' GROUP BY fp.user_id), " +
            "fb AS (SELECT fb.user_id, SUM(fb.tokens) AS tokens FROM cc4.fanart_bonus fb " +
            "WHERE fb.timestamp > '2021-01-25 05:00:00' GROUP BY fb.user_id), " +
            "cp AS (SELECT cp.user_id, SUM(c.participation_tokens) AS tokens " +
            "FROM cc4.contest_participant cp LEFT JOIN cc4.contest c ON c.id = cp.contest_id " +
            "WHERE c.end_time > '2021-01-25 05:00:00' GROUP BY cp.user_id), " +
            "cw AS (SELECT cw.user_id, SUM(cwt.tokens) AS tokens " +
            "FROM cc4.contest_winner cw LEFT JOIN cc4.contest_winner_tokens cwt ON cwt.contest_id = cw.contest_id AND cwt.place = cw.place " +
            "LEFT JOIN cc4.contest c ON c.id = cwt.contest_id " +
            "WHERE c.end_time > '2021-01-25 05:00:00' GROUP BY cw.user_id), " +
            "total AS (SELECT m.user_id, m.team_id, CASE m.banned WHEN false THEN COALESCE(fp.tokens, 0) + COALESCE(fb.tokens, 0) + COALESCE(cp.tokens, 0) + COALESCE(cw.tokens, 0) " +
            "ELSE (COALESCE(fp.tokens, 0) + COALESCE(fb.tokens, 0) + COALESCE(cp.tokens, 0) + COALESCE(cw.tokens, 0)) / 2 END AS tokens " +
            "FROM cc4.member m LEFT JOIN fp ON fp.user_id = m.user_id LEFT JOIN fb ON fb.user_id = m.user_id " +
            "LEFT JOIN cp ON cp.user_id = m.user_id LEFT JOIN cw ON cw.user_id = m.user_id " +
            "GROUP BY m.user_id, m.team_id, fp.tokens, fb.tokens, cp.tokens, cw.tokens) " +
            "SELECT t.full_name AS team, t.color, " +
            "COALESCE(SUM(tot.tokens), 0) AS tokens " +
            "FROM cc4.team t LEFT JOIN total tot ON t.id = tot.team_id " +
            "GROUP BY t.full_name, t.color ORDER BY tokens DESC")
    @Results(value = {
            @Result(property = "teamName", column = "team"),
            @Result(property = "colorValue", column = "color"),
            @Result(property = "tokens", column = "tokens")
    })
    List<WarLeaderboard> getArtLeaderboard();

    @Select("WITH fp AS (SELECT m.user_id, SUM(fp.tokens) AS tokens FROM cc4.fanart_participation fp " +
            "LEFT JOIN cc4.member m ON fp.user_id = m.user_id GROUP BY m.user_id), " +
            "fb AS (SELECT m.user_id, SUM(fb.tokens) AS tokens FROM cc4.fanart_bonus fb " +
            "LEFT JOIN cc4.member m ON fb.user_id = m.user_id GROUP BY m.user_id), " +
            "cp AS (SELECT m.user_id, SUM(c.participation_tokens) AS tokens " +
            "FROM cc4.contest_participant cp LEFT JOIN cc4.contest c ON c.id = cp.contest_id " +
            "LEFT JOIN cc4.member m ON cp.user_id = m.user_id GROUP BY m.user_id), " +
            "cw AS (SELECT m.user_id, SUM(cwt.tokens) AS tokens " +
            "FROM cc4.contest_winner cw LEFT JOIN cc4.contest_winner_tokens cwt ON cwt.contest_id = cw.contest_id AND cwt.place = cw.place " +
            "LEFT JOIN cc4.contest c ON c.id = cwt.contest_id " +
            "LEFT JOIN cc4.member m ON cw.user_id = m.user_id GROUP BY m.user_id) " +
            "SELECT m.user_id, t.full_name AS team, t.color, " +
            "(COALESCE(fp.tokens, 0) + COALESCE(fb.tokens, 0) + COALESCE(cp.tokens, 0) + COALESCE(cw.tokens, 0)) AS tokens " +
            "FROM cc4.member m LEFT JOIN cc4.team t ON m.team_id = t.id LEFT JOIN fp ON m.user_id = fp.user_id " +
            "LEFT JOIN fb ON m.user_id = fb.user_id LEFT JOIN cp ON m.user_id = cp.user_id " +
            "LEFT JOIN cw ON m.user_id = cw.user_id WHERE t.short_name = #{name} AND m.class_id != 5 AND m.banned = false " +
            "GROUP BY m.user_id, t.full_name, t.color, fp.tokens, fb.tokens, cp.tokens, cw.tokens ORDER BY tokens DESC LIMIT 5")
    @Results(value = {
            @Result(property = "userId", column = "user_id"),
            @Result(property = "teamName", column = "team"),
            @Result(property = "colorValue", column = "color"),
            @Result(property = "tokens", column = "tokens")
    })
    List<WarUserLeaderboard> getArtUserLeaderboard(@Param("name") String name);

    @Select("WITH total AS (SELECT m.user_id, m.team_id, CASE m.banned WHEN false THEN COALESCE(SUM(gp.tokens), 0) " +
            "ELSE COALESCE(SUM(gp.tokens), 0) / 2 END AS tokens " +
            "FROM cc4.member m LEFT JOIN cc4.game_points gp ON gp.user_id = m.user_id " +
            "WHERE gp.timestamp > '2021-01-25 05:00:00' AND m.class_id != 5 " +
            "GROUP BY m.user_id, m.team_id) " +
            "SELECT t.full_name, t.color, COALESCE(SUM(tot.tokens), 0) AS tokens " +
            "FROM cc4.team t LEFT JOIN total tot ON tot.team_id = t.id " +
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
            "WHERE t.short_name = #{name} AND m.class_id != 5 AND m.banned = false " +
            "GROUP BY m.user_id, t.full_name, t.color ORDER BY tokens DESC LIMIT 5")
    @Results(value = {
            @Result(property = "userId", column = "user_id"),
            @Result(property = "teamName", column = "team"),
            @Result(property = "colorValue", column = "color"),
            @Result(property = "tokens", column = "tokens")
    })
    List<WarUserLeaderboard> getGameUserLeaderboard(@Param("name") String name);

    @Select("WITH total AS (SELECT m.user_id, m.team_id, CASE m.banned WHEN false THEN COALESCE(SUM(adt.tokens), 0) " +
            "ELSE COALESCE(SUM(adt.tokens), 0) / 2 END AS tokens " +
            "FROM cc4.team t LEFT JOIN cc4.member m ON t.id = m.team_id " +
            "LEFT JOIN cc4.user_achievement ua ON m.user_id = ua.user_id " +
            "LEFT JOIN cc4.achievement a ON a.id = ua.achievement_id " +
            "LEFT JOIN cc4.achievement_difficulty_tokens adt ON a.difficulty = adt.difficulty " +
            "WHERE ua.timestamp > '2021-01-25 05:00:00' AND m.class_id != 5 " +
            "GROUP BY m.user_id, m.team_id) " +
            "SELECT t.full_name, t.color, COALESCE(SUM(tot.tokens), 0) AS tokens " +
            "FROM cc4.team t LEFT JOIN total tot ON tot.team_id = t.id " +
            "GROUP BY t.full_name, t.color ORDER BY tokens DESC")
    @Results(value = {
            @Result(property = "teamName", column = "team"),
            @Result(property = "colorValue", column = "color"),
            @Result(property = "tokens", column = "tokens")
    })
    List<WarLeaderboard> getBonusLeaderboard();

    @Select("SELECT m.user_id, t.full_name AS team, t.color AS color, COALESCE(SUM(adt.tokens), 0) AS tokens " +
            "FROM cc4.member m LEFT JOIN cc4.team t ON t.id = m.team_id " +
            "LEFT JOIN cc4.user_achievement ua ON m.user_id = ua.user_id " +
            "LEFT JOIN cc4.achievement a ON a.id = ua.achievement_id " +
            "LEFT JOIN cc4.achievement_difficulty_tokens adt ON a.difficulty = adt.difficulty " +
            "WHERE t.short_name = #{name} AND m.class_id != 5 AND m.banned = false " +
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

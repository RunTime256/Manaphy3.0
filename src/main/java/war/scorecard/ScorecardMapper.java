package war.scorecard;

import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

public interface ScorecardMapper
{
    @Select("WITH winner_battle_tokens AS (SELECT y.id, y.user_id, COALESCE(y.tokens, 0) AS tokens FROM " +
            "(SELECT 1 a) x LEFT JOIN " +
            "(SELECT id, winner AS user_id, SUM(winner_tokens * (winner_multiplier + (bonus_multiplier - 1))) AS tokens " +
            "FROM cc4.battle GROUP BY winner, id) y ON 1 = 1), " +
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
            "ON 1 = 1 GROUP BY y.user_id), " +
            "battle_tokens AS (SELECT m.user_id, COALESCE(tbt.tokens, 0) + COALESCE(h.tokens, 0) AS tokens FROM cc4.member m " +
            "LEFT JOIN total_battle_tokens tbt ON tbt.user_id = m.user_id " +
            "LEFT JOIN hoard h ON h.user_id = m.user_id " +
            "LEFT JOIN cc4.team t ON t.id = m.team_id " +
            "GROUP BY tbt.tokens, h.tokens, m.user_id), " +
            "solved AS (SELECT DISTINCT p.id, pg.user_id, p.value AS tokens FROM cc4.puzzle p " +
            "LEFT JOIN cc4.puzzle_solution ps ON p.id = ps.puzzle_id " +
            "LEFT JOIN cc4.puzzle_guess pg ON p.id = pg.puzzle_id AND pg.guess = ps.solution " +
            "WHERE p.prewar = false AND p.end_time IS NOT NULL), " +
            "total AS (SELECT s.user_id, SUM(tokens) AS tokens FROM solved s GROUP BY s.user_id), " +
            "puzzle_tokens AS (SELECT m.user_id, COALESCE(total.tokens, 0) AS tokens FROM " +
            "cc4.member m LEFT JOIN cc4.team t ON m.team_id = t.id " +
            "LEFT JOIN total ON total.user_id = m.user_id " +
            "GROUP BY m.user_id, total.tokens), " +
            "fp AS (SELECT m.user_id, SUM(fp.tokens) AS tokens FROM cc4.fanart_participation fp " +
            "LEFT JOIN cc4.member m ON fp.user_id = m.user_id GROUP BY m.user_id), " +
            "fb AS (SELECT m.user_id, SUM(fb.tokens) AS tokens FROM cc4.fanart_bonus fb " +
            "LEFT JOIN cc4.member m ON fb.user_id = m.user_id GROUP BY m.user_id), " +
            "cp AS (SELECT m.user_id, SUM(c.participation_tokens) AS tokens " +
            "FROM cc4.contest_participant cp LEFT JOIN cc4.contest c ON c.id = cp.contest_id " +
            "LEFT JOIN cc4.member m ON cp.user_id = m.user_id GROUP BY m.user_id), " +
            "cw AS (SELECT m.user_id, SUM(cwt.tokens) AS tokens " +
            "FROM cc4.contest_winner cw LEFT JOIN cc4.contest_winner_tokens cwt ON cwt.contest_id = cw.contest_id AND cwt.place = cw.place " +
            "LEFT JOIN cc4.contest c ON c.id = cwt.contest_id " +
            "LEFT JOIN cc4.member m ON cw.user_id = m.user_id GROUP BY m.user_id), " +
            "art_tokens AS (SELECT m.user_id, " +
            "(COALESCE(fp.tokens, 0) + COALESCE(fb.tokens, 0) + COALESCE(cp.tokens, 0) + COALESCE(cw.tokens, 0)) AS tokens " +
            "FROM cc4.member m LEFT JOIN cc4.team t ON m.team_id = t.id LEFT JOIN fp ON m.user_id = fp.user_id " +
            "LEFT JOIN fb ON m.user_id = fb.user_id LEFT JOIN cp ON m.user_id = cp.user_id " +
            "LEFT JOIN cw ON m.user_id = cw.user_id " +
            "GROUP BY m.user_id, fp.tokens, fb.tokens, cp.tokens, cw.tokens), " +
            "game_tokens AS (SELECT gp.user_id, COALESCE(SUM(gp.tokens), 0) AS tokens " +
            "FROM cc4.game_points gp GROUP BY user_id), " +
            "bonus_tokens AS (SELECT ua.user_id, COALESCE(SUM(adt.tokens), 0) AS tokens " +
            "FROM cc4.user_achievement ua LEFT JOIN cc4.achievement a ON a.id = ua.achievement_id " +
            "LEFT JOIN cc4.achievement_difficulty_tokens adt ON a.difficulty = adt.difficulty " +
            "WHERE a.prewar = false GROUP BY user_id), " +
            "user_id AS (SELECT #{userId} AS user_id) " +
            "SELECT u.user_id, COALESCE(bat.tokens, 0) AS battle_tokens, " +
            "COALESCE(pt.tokens, 0) AS puzzle_tokens, COALESCE(at.tokens, 0) AS art_tokens, " +
            "COALESCE(gt.tokens, 0) AS game_tokens, COALESCE(bt.tokens, 0) AS bonus_tokens " +
            "FROM user_id u LEFT JOIN battle_tokens bat ON u.user_id = bat.user_id " +
            "LEFT JOIN puzzle_tokens pt ON u.user_id = pt.user_id " +
            "LEFT JOIN art_tokens at ON u.user_id = at.user_id " +
            "LEFT JOIN game_tokens gt ON u.user_id = gt.user_id " +
            "LEFT JOIN bonus_tokens bt ON u.user_id = bt.user_id")
    @Results(value = {
            @Result(property = "userId", column = "user_id"),
            @Result(property = "battleTokens", column = "battle_tokens"),
            @Result(property = "puzzleTokens", column = "puzzle_tokens"),
            @Result(property = "artTokens", column = "art_tokens"),
            @Result(property = "gameTokens", column = "game_tokens"),
            @Result(property = "bonusTokens", column = "bonus_tokens")
    })
    WarScorecard getScorecard(long userId);
}

package war.scorecard;

import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

public interface ScorecardMapper
{
    @Select("WITH winner_battle_tokens AS (SELECT COALESCE(y.tokens, 0) AS tokens FROM " +
            "(SELECT 1 a) x LEFT JOIN " +
            "(SELECT SUM(winner_tokens * (winner_multiplier + (bonus_multiplier - 1))) AS tokens FROM cc4.battle WHERE winner = #{userId} GROUP BY winner) y " +
            "ON 1 = 1), " +
            "loser_battle_tokens AS (SELECT COALESCE(y.tokens, 0) AS tokens FROM " +
            "(SELECT 1 a) x LEFT JOIN " +
            "(SELECT SUM(loser_tokens) AS tokens FROM cc4.battle WHERE loser = #{userId} GROUP BY loser) y " +
            "ON 1 = 1), " +
            "puzzle_tokens AS (SELECT 0 AS tokens), " +
            "fp AS (SELECT fp.user_id, SUM(fp.tokens) AS tokens FROM cc4.fanart_participation fp " +
            "GROUP BY fp.user_id), " +
            "fb AS (SELECT fb.user_id, SUM(fb.tokens) AS tokens FROM cc4.fanart_bonus fb " +
            "GROUP BY fb.user_id), " +
            "cp AS (SELECT cp.user_id, SUM(c.participation_tokens) AS tokens " +
            "FROM cc4.contest_participant cp LEFT JOIN cc4.contest c ON c.id = cp.contest_id " +
            "GROUP BY cp.user_id), " +
            "cw AS (SELECT cw.user_id, SUM(cwt.tokens) AS tokens " +
            "FROM cc4.contest_winner cw LEFT JOIN cc4.contest_winner_tokens cwt ON cwt.contest_id = cw.contest_id AND cwt.place = cw.place " +
            "GROUP BY cw.user_id), " +
            "art_tokens AS (SELECT (COALESCE(fp.tokens, 0) + COALESCE(fb.tokens, 0) + COALESCE(cp.tokens, 0) + COALESCE(cw.tokens, 0)) AS tokens " +
            "FROM cc4.member m LEFT JOIN cc4.team t ON m.team_id = t.id " +
            "LEFT JOIN fp ON m.user_id = fp.user_id " +
            "LEFT JOIN fb ON m.user_id = fb.user_id " +
            "LEFT JOIN cp ON m.user_id = cp.user_id " +
            "LEFT JOIN cw ON m.user_id = cw.user_id " +
            "WHERE m.user_id = #{userId}), " +
            "game_tokens AS (SELECT COALESCE(SUM(gp.tokens), 0) AS tokens FROM cc4.game_points gp WHERE gp.user_id = #{userId}), " +
            "bonus_tokens AS (SELECT COALESCE(SUM(adt.tokens), 0) AS tokens FROM cc4.user_achievement ua LEFT JOIN cc4.achievement a ON a.id = ua.achievement_id " +
            "LEFT JOIN cc4.achievement_difficulty_tokens adt ON a.difficulty = adt.difficulty " +
            "WHERE user_id = #{userId} AND a.prewar = false) " +
            "SELECT #{userId} AS user_id, wbt.tokens + lbt.tokens AS battle_tokens, pt.tokens AS puzzle_tokens, at.tokens AS art_tokens, gt.tokens AS game_tokens, bt.tokens AS bonus_tokens " +
            "FROM winner_battle_tokens wbt JOIN loser_battle_tokens lbt ON 1 = 1 JOIN puzzle_tokens pt ON 1 = 1 JOIN art_tokens at ON 1 = 1 JOIN game_tokens gt ON 1 = 1 JOIN bonus_tokens bt ON 1 = 1")
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

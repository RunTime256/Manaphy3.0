package war.scorecard;

import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

public interface ScorecardMapper
{
    @Select("WITH winner_battle_tokens AS (SELECT COALESCE(y.tokens, 0) AS tokens FROM " +
            "(SELECT 1 a) x LEFT JOIN " +
            "(SELECT SUM(winner_tokens * winner_multiplier) AS tokens FROM cc4.battle WHERE winner = #{userId} GROUP BY winner) y " +
            "ON 1 = 1), " +
            "loser_battle_tokens AS (SELECT COALESCE(y.tokens, 0) AS tokens FROM " +
            "(SELECT 1 a) x LEFT JOIN " +
            "(SELECT SUM(loser_tokens) AS tokens FROM cc4.battle WHERE loser = #{userId} GROUP BY loser) y " +
            "ON 1 = 1), " +
            "puzzle_tokens AS (SELECT 0 AS tokens), " +
            "art_tokens AS (SELECT 0 AS tokens), " +
            "game_tokens AS (SELECT COALESCE(SUM(gp.tokens), 0) AS tokens FROM cc4.game_points gp WHERE gp.user_id = #{userId}), " +
            "bonus_tokens AS (SELECT COALESCE(SUM(adt.tokens), 0) AS tokens FROM cc4.user_achievement ua LEFT JOIN cc4.achievement a ON a.id = ua.achievement_id " +
            "LEFT JOIN cc4.achievement_difficulty_tokens adt ON a.difficulty = adt.difficulty " +
            "WHERE user_id = #{userId}) " +
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

package war.scorecard;

import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

public interface ScorecardMapper
{
    @Select("WITH battle_tokens AS (SELECT 0 AS tokens), " +
            "puzzle_tokens AS (SELECT 0 AS tokens), " +
            "art_tokens AS (SELECT 0 AS tokens), " +
            "game_tokens AS (SELECT COALESCE(SUM(gp.tokens), 0) AS tokens FROM cc4.game_points gp WHERE gp.user_id = #{userId}), " +
            "misc_tokens AS (SELECT 0 AS tokens) " +
            "SELECT #{userId} AS user_id, bt.tokens AS battle_tokens, pt.tokens AS puzzle_tokens, at.tokens AS art_tokens, gt.tokens AS game_tokens, mt.tokens AS misc_tokens " +
            "FROM battle_tokens bt JOIN puzzle_tokens pt ON 1 = 1 JOIN art_tokens at ON 1 = 1 JOIN game_tokens gt ON 1 = 1 JOIN misc_tokens mt ON 1 = 1 ")
    @Results(value = {
            @Result(property = "userId", column = "user_id"),
            @Result(property = "battleTokens", column = "battle_tokens"),
            @Result(property = "puzzleTokens", column = "puzzle_tokens"),
            @Result(property = "artTokens", column = "art_tokens"),
            @Result(property = "gameTokens", column = "game_tokens"),
            @Result(property = "miscTokens", column = "misc_tokens")
    })
    WarScorecard getScorecard(long userId);
}

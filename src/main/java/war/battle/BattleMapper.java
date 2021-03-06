package war.battle;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.Instant;

public interface BattleMapper
{
    @Select("SELECT EXISTS(SELECT * FROM cc4.battle b WHERE b.url = #{url})")
    boolean isBattle(@Param("url") String url);

    @Select("SELECT EXISTS(SELECT * FROM cc4.banned_battle_format bbf WHERE bbf.format = #{format})")
    boolean isBannedFormat(@Param("format") String format);

    @Select("SELECT count(*) FROM cc4.battle b WHERE b.winner = #{userId}")
    int getWins(@Param("userId") long userId);

    @Select("SELECT count(*) FROM cc4.battle b WHERE b.loser = #{userId}")
    int getLosses(@Param("userId") long userId);

    @Select("SELECT count(*) FROM cc4.battle b WHERE b.winner = #{userId} OR b.loser = #{userId}")
    int getTotalBattles(@Param("userId") long userId);

    @Select("SELECT COALESCE(y.consecutive_wins, 0) FROM " +
            "(SELECT 1 a) x LEFT JOIN " +
            "(SELECT CASE WHEN b.winner = #{userId} THEN b.consecutive_wins ELSE 0 END AS consecutive_wins " +
            "FROM cc4.battle b WHERE b.winner = #{userId} OR b.loser = #{userId} ORDER BY b.timestamp DESC LIMIT 1) y " +
            "ON 1 = 1")
    int getWinStreak(@Param("userId") long userId);

    @Select("SELECT COALESCE(y.consecutive_losses, 0) FROM " +
            "(SELECT 1 a) x LEFT JOIN " +
            "(SELECT CASE WHEN b.loser = #{userId} THEN b.consecutive_losses ELSE 0 END AS consecutive_losses " +
            "FROM cc4.battle b WHERE b.winner = #{userId} OR b.loser = #{userId} ORDER BY b.timestamp DESC LIMIT 1) y " +
            "ON 1 = 1")
    int getLossStreak(@Param("userId") long userId);

    @Select("SELECT EXISTS(SELECT * FROM cc4.battle_achievement ba WHERE ba.name = #{name} AND ba.value = #{value})")
    boolean isAchievement(@Param("name") String name, @Param("value") int value);

    @Select("SELECT a.name FROM cc4.battle_achievement ba LEFT JOIN cc4.achievement a ON a.id = ba.achievement_id " +
            "WHERE ba.name = #{name} AND ba.value = #{value}")
    String getAchievement(@Param("name") String name, @Param("value") int value);

    @Select("SELECT y.timestamp, CASE WHEN (y.winner = #{userId}) THEN COALESCE(y.winner_multiplier, 0) ELSE COALESCE(y.loser_multiplier, 0) END AS multiplier, " +
            "CASE WHEN (y.winner = #{userId}) THEN COALESCE(y.winner_multiplier_count, 0) ELSE COALESCE(y.loser_multiplier_count, 0) END AS multiplier_count FROM " +
            "(SELECT 1 a) x LEFT JOIN " +
            "(SELECT b.winner, b.timestamp, b.winner_multiplier, b.winner_multiplier_count, b.loser_multiplier, b.loser_multiplier_count " +
            "FROM cc4.battle b WHERE winner = #{userId} OR loser = #{userId} ORDER BY b.timestamp DESC LIMIT 1) y " +
            "ON 1 = 1")
    PreviousBattleMultiplier getMultiplier(@Param("userId") long userId);

    @Select("SELECT format FROM cc4.battle_format")
    String getBonusFormat();

    @Insert("INSERT INTO cc4.battle (winner, loser, url, timestamp, consecutive_wins, consecutive_losses, " +
            "winner_tokens, loser_tokens, themed, winner_multiplier, winner_multiplier_count, bonus_multiplier, " +
            "loser_multiplier, loser_multiplier_count) " +
            "VALUES (#{winner}, #{loser}, #{url}, #{timestamp}, #{consecutiveWins}, #{consecutiveLosses}, " +
            "#{winTokens}, #{loseTokens}, false, #{winnerMultiplier}, #{winnerMultiplierCount}, #{bonusMultiplier}, " +
            "#{loserMultiplier}, #{loserMultiplierCount})")
    void addBattle(@Param("winner") long winner, @Param("loser") long loser, @Param("url") String url,
                   @Param("consecutiveWins") int consecutiveWins, @Param("consecutiveLosses") int consecutiveLosses,
                   @Param("timestamp") Instant timestamp, @Param("winTokens") int winTokens, @Param("loseTokens") int loseTokens,
                   @Param("winnerMultiplier") int winnerMultiplier, @Param("winnerMultiplierCount") int winnerMultiplierCount,
                   @Param("bonusMultiplier") int bonusMultiplier, @Param("loserMultiplier") int loserMultiplier,
                   @Param("loserMultiplierCount") int loserMultiplierCount);

    @Update("UPDATE cc4.battle_format SET format = #{format}")
    void updateBonusFormat(@Param("format") String format);

    @Delete("DELETE FROM cc4.battle WHERE url = #{url}")
    int deleteBattle(@Param("url") String url);
}

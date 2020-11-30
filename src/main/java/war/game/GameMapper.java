package war.game;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.Instant;

public interface GameMapper
{
    @Select("SELECT COALESCE(gpe.tokens, 0) FROM cc4.game g LEFT JOIN cc4.game_point_eval gpe ON g.id = gpe.game_id " +
            "WHERE g.name = #{gameName} AND gpe.min_points <= #{score} AND (gpe.max_points >= #{score} OR gpe.max_points IS NULL)")
    int getTokenEval(@Param("gameName") String gameName, @Param("score") int score);

    @Select("SELECT g.full_name FROM cc4.game g WHERE g.name = #{gameName}")
    String getFullName(@Param("gameName") String gameName);

    @Select("SELECT EXISTS (SELECT FROM cc4.game WHERE name = #{name})")
    boolean exists(@Param("name") String name);

    @Select("SELECT EXISTS (SELECT * FROM cc4.game WHERE name = #{name} AND channel_id = #{channelId});")
    boolean correctChannel(@Param("name") String name, @Param("channelId") Long channelId);

    @Insert("INSERT INTO cc4.game_points (game_id, user_id, points, tokens, timestamp, block) VALUES " +
            "((SELECT g.id FROM cc4.game g WHERE g.name = #{gameName}), #{userId}, #{points}, #{tokens}, #{timestamp}, " +
            "(SELECT current_block from cc4.block))")
    void addGameTokens(@Param("gameName") String gameName, @Param("userId") long userId, @Param("points") int points,
                       @Param("tokens") int tokens, @Param("timestamp")Instant timestamp);
}

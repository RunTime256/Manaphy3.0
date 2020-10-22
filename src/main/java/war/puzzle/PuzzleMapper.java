package war.puzzle;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.Instant;
import java.util.List;

public interface PuzzleMapper
{
    @Select("SELECT EXISTS (SELECT FROM cc4.puzzle WHERE name = #{name})")
    boolean exists(@Param("name") String name);

    @Select("SELECT EXISTS (SELECT FROM cc4.puzzle WHERE name = #{name} AND start_time < #{time} AND (end_time > #{time} OR end_time IS NULL))")
    boolean isActive(@Param("name") String name, @Param("time") Instant time);

    @Select("SELECT infinite FROM cc4.puzzle p WHERE p.name = #{name}")
    boolean isInfinite(@Param("name") String name);

    @Select("SELECT EXISTS (SELECT FROM cc4.puzzle p LEFT JOIN cc4.puzzle_guess pg ON p.id = pg.puzzle_id " +
            "WHERE p.name = #{name} AND pg.user_id = #{userId})")
    boolean hasGuessed(@Param("name") String name, @Param("userId") Long userId);

    @Select("SELECT EXISTS (SELECT FROM cc4.puzzle p LEFT JOIN cc4.puzzle_solution ps ON p.id = ps.puzzle_id " +
            "LEFT JOIN cc4.puzzle_guess pg ON ps.solution = pg.guess AND p.id = pg.puzzle_id " +
            "WHERE p.name = #{name} AND pg.user_id = #{userId})")
    boolean hasSolved(@Param("name") String name, @Param("userId") Long userId);

    @Select("WITH user_codes AS (SELECT * FROM cc4.puzzle p LEFT JOIN cc4.puzzle_code pc ON p.id = pc.puzzle_id " +
            "LEFT JOIN cc4.code c ON pc.code_id = c.id LEFT JOIN cc4.code_retrieved cr ON c.id = cr.code_id " +
            "WHERE p.name = #{name} AND c.id IS NOT NULL), " +
            "codes AS (SELECT * FROM cc4.puzzle p LEFT JOIN cc4.puzzle_code pc ON p.id = pc.puzzle_id " +
            "LEFT JOIN cc4.code c ON pc.code_id = c.id WHERE p.name = #{name} AND c.id IS NOT NULL) " +
            "SELECT (NOT EXISTS (SELECT * FROM user_codes) OR (SELECT count(*) FROM user_codes WHERE user_id = #{userId}) = (SELECT count(*) FROM codes))")
    boolean hasCodeRequirements(@Param("name") String name, @Param("userId") Long userId);

    @Select("SELECT server_id, role_id FROM cc4.puzzle p LEFT JOIN cc4.puzzle_role pr ON p.id = pr.puzzle_id " +
            "WHERE p.name = #{name} AND server_id IS NOT NULL AND role_id IS NOT NULL")
    List<PuzzleRoleRequirement> roleRequirements(@Param("name") String name);

    @Select("SELECT EXISTS (SELECT * FROM cc4.puzzle p LEFT JOIN cc4.puzzle_solution ps ON p.id = ps.puzzle_id" +
            " WHERE p.name = #{name} AND ps.solution = #{guess})")
    boolean correct(@Param("name") String name, @Param("guess") String guess);

    @Insert("INSERT INTO cc4.puzzle_guess( " +
            "puzzle_id, user_id, guess, time) " +
            "VALUES ((SELECT id FROM cc4.puzzle WHERE name = #{name}), #{userId}, #{guess}, #{time});")
    void addGuess(@Param("name") String name, @Param("guess") String guess, @Param("userId") Long userId, @Param("time") Instant time);
}

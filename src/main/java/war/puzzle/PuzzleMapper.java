package war.puzzle;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.Instant;
import java.util.List;

public interface PuzzleMapper
{
    @Select("SELECT EXISTS (SELECT FROM cc4.puzzle WHERE name = #{name})")
    boolean exists(@Param("name") String name);

    @Select("SELECT EXISTS (SELECT FROM cc4.puzzle WHERE name = #{name} AND start_time < #{time} AND (end_time > #{time} OR end_time IS NULL))")
    boolean isActive(@Param("name") String name, @Param("time") Instant time);

    @Select("SELECT EXISTS (SELECT FROM cc4.puzzle WHERE name = #{name} AND start_time > #{time})")
    boolean hasStarted(@Param("name") String name, @Param("time") Instant time);

    @Select("SELECT EXISTS (SELECT FROM cc4.puzzle WHERE name = #{name} AND end_time < #{time})")
    boolean hasEnded(@Param("name") String name, @Param("time") Instant time);

    @Select("SELECT infinite FROM cc4.puzzle p WHERE p.name = #{name}")
    boolean isInfinite(@Param("name") String name);

    @Select("SELECT (p.future AND (p.start_time IS NULL)) FROM cc4.puzzle p WHERE p.name = #{name}")
    boolean isFuture(@Param("name") String name);

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

    @Select("SELECT EXISTS (SELECT * FROM cc4.puzzle p LEFT JOIN cc4.puzzle_solution ps ON p.id = ps.puzzle_id " +
            "WHERE p.name = #{name} AND ps.solution = #{guess})")
    boolean correct(@Param("name") String name, @Param("guess") String guess);

    @Select("SELECT DISTINCT pg.user_id FROM cc4.puzzle p LEFT JOIN cc4.puzzle_solution ps ON p.id = ps.puzzle_id " +
            "RIGHT JOIN cc4.puzzle_guess pg ON p.id = pg.puzzle_id AND pg.guess = ps.solution WHERE p.name = #{name}")
    List<Long> puzzleSolvers(@Param("name") String name);

    @Select("SELECT DISTINCT pg.user_id FROM cc4.puzzle p LEFT JOIN cc4.puzzle_solution ps ON p.id = ps.puzzle_id " +
            "RIGHT JOIN cc4.puzzle_guess pg ON p.id = pg.puzzle_id WHERE p.name = #{name}")
    List<Long> puzzleGuessers(@Param("name") String name);

    @Select("SELECT DISTINCT p.name FROM cc4.puzzle p LEFT JOIN cc4.puzzle_solution ps ON p.id = ps.puzzle_id " +
            "LEFT JOIN cc4.puzzle_guess pg ON p.id = pg.puzzle_id " +
            "WHERE pg.user_id = #{userId} AND p.infinite = false AND p.prewar = false AND p.end_time IS NULL OR p.end_time > #{timestamp}")
    List<String> getGuessedPuzzles(@Param("userId") long userId, @Param("timestamp") Instant timestamp);

    @Select("SELECT DISTINCT p.name FROM cc4.puzzle p LEFT JOIN cc4.puzzle_solution ps ON p.id = ps.puzzle_id " +
            "LEFT JOIN cc4.puzzle_guess pg ON p.id = pg.puzzle_id AND pg.guess = ps.solution " +
            "WHERE pg.user_id = #{userId} AND p.infinite = false AND p.prewar = false AND p.end_time < #{timestamp}")
    List<String> getSolvedPuzzles(@Param("userId") long userId, @Param("timestamp") Instant timestamp);

    @Select("SELECT DISTINCT p.name FROM cc4.puzzle p LEFT JOIN cc4.puzzle_solution ps ON p.id = ps.puzzle_id " +
            "LEFT JOIN cc4.puzzle_guess pg ON p.id = pg.puzzle_id AND pg.guess = ps.solution " +
            "WHERE pg.user_id = #{userId} AND p.infinite = true AND p.prewar = false")
    List<String> getSolvedInfinitePuzzles(@Param("userId") long userId);

    @Select("WITH solved AS (SELECT p.id FROM cc4.puzzle p LEFT JOIN cc4.puzzle_solution ps ON p.id = ps.puzzle_id " +
            "RIGHT JOIN cc4.puzzle_guess pg ON p.id = pg.puzzle_id AND pg.guess = ps.solution " +
            "WHERE pg.user_id = #{userId} AND p.infinite = true AND p.prewar = false) " +
            "SELECT DISTINCT p.name FROM cc4.puzzle p LEFT JOIN cc4.puzzle_solution ps ON p.id = ps.puzzle_id " +
            "LEFT JOIN cc4.puzzle_guess pg ON p.id = pg.puzzle_id " +
            "WHERE NOT EXISTS(SELECT * FROM solved s WHERE s.id = p.id) AND (p.viewable = true OR " +
            "pg.user_id = #{userId} AND p.infinite = true AND p.prewar = false)")
    List<String> getUnsolvedDiscoveredInfinitePuzzles(@Param("userId") long userId);

    @Select("SELECT DISTINCT p.name FROM cc4.puzzle p LEFT JOIN cc4.puzzle_solution ps ON p.id = ps.puzzle_id " +
            "LEFT JOIN cc4.puzzle_guess pg ON p.id = pg.puzzle_id AND pg.guess = ps.solution " +
            "WHERE pg.user_id = #{userId} AND p.infinite = true AND p.prewar = true")
    List<String> getSolvedInfinitePrewarPuzzles(@Param("userId") long userId);

    @Select("WITH solved AS (SELECT p.id FROM cc4.puzzle p LEFT JOIN cc4.puzzle_solution ps ON p.id = ps.puzzle_id " +
            "RIGHT JOIN cc4.puzzle_guess pg ON p.id = pg.puzzle_id AND pg.guess = ps.solution " +
            "WHERE pg.user_id = #{userId} AND p.infinite = true) " +
            "SELECT DISTINCT p.name FROM cc4.puzzle p LEFT JOIN cc4.puzzle_solution ps ON p.id = ps.puzzle_id " +
            "LEFT JOIN cc4.puzzle_guess pg ON p.id = pg.puzzle_id " +
            "WHERE pg.user_id = #{userId} AND p.infinite = true AND p.prewar = true AND NOT EXISTS(SELECT * FROM solved s WHERE s.id = p.id)")
    List<String> getUnsolvedDiscoveredInfinitePrewarPuzzles(@Param("userId") long userId);

    @Select("SELECT EXISTS(SELECT * FROM cc4.puzzle_achievement pa LEFT JOIN cc4.puzzle p ON p.id = pa.puzzle_id " +
            "WHERE p.name = #{name})")
    boolean isAchievementPuzzle(@Param("name") String name);

    @Select("SELECT EXISTS(SELECT * FROM cc4.puzzle_multi_achievement pma LEFT JOIN cc4.puzzle p ON p.id = pma.puzzle_id " +
            "WHERE p.name = #{name})")
    boolean isMultiAchievementPuzzle(@Param("name") String name);

    @Select("WITH achieve AS (SELECT pma.achievement_id FROM cc4.puzzle op " +
            "LEFT JOIN cc4.puzzle_multi_achievement pma ON op.id = pma.puzzle_id WHERE op.name = #{name} LIMIT 1), " +
            "multi AS (SELECT pg.id FROM cc4.puzzle_multi_achievement pma LEFT JOIN cc4.puzzle p ON p.id = pma.puzzle_id " +
            "LEFT JOIN achieve a ON pma.achievement_id = a.achievement_id " +
            "LEFT JOIN cc4.puzzle_solution ps ON p.id = ps.puzzle_id " +
            "LEFT JOIN cc4.puzzle_guess pg ON p.id = pg.puzzle_id AND pg.guess = ps.solution AND pg.user_id = #{userId}) " +
            "SELECT (count(*) = 0) FROM multi m WHERE m.id IS NULL")
    boolean hasCompletedMultiAchievementPuzzle(@Param("name") String name, @Param("userId") Long userId);

    @Select("SELECT a.name FROM cc4.puzzle p LEFT JOIN cc4.puzzle_multi_achievement pma ON p.id = pma.puzzle_id " +
            "LEFT JOIN cc4.achievement a ON pma.achievement_id = a.id " +
            "WHERE p.name = #{name} LIMIT 1")
    String getMultiAchievement(@Param("name") String name);

    @Select("SELECT a.name FROM cc4.puzzle p LEFT JOIN cc4.puzzle_achievement pa ON p.id = pa.puzzle_id " +
            "LEFT JOIN cc4.achievement a ON a.id = pa.achievement_id WHERE p.name = #{name}")
    String getAchievement(@Param("name") String name);

    @Insert("INSERT INTO cc4.puzzle_guess( " +
            "puzzle_id, user_id, guess, time) " +
            "VALUES ((SELECT id FROM cc4.puzzle WHERE name = #{name}), #{userId}, #{guess}, #{time});")
    void addGuess(@Param("name") String name, @Param("guess") String guess, @Param("userId") Long userId, @Param("time") Instant time);

    @Update("UPDATE cc4.puzzle SET start_time = #{time} WHERE name = #{name}")
    void start(@Param("name") String name, @Param("time") Instant time);

    @Update("UPDATE cc4.puzzle SET end_time = #{time} WHERE name = #{name}")
    void end(@Param("name") String name, @Param("time") Instant time);
}

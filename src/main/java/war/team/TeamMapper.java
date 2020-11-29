package war.team;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.time.Instant;
import java.util.List;

public interface TeamMapper
{
    @Select("WITH team_tokens AS (SELECT t.short_name, COALESCE(SUM(p.value), 0) AS tokens FROM cc4.team t LEFT JOIN cc4.member m ON t.id = m.team_id " +
            "LEFT JOIN cc4.puzzle_guess pg ON m.user_id = pg.user_id " +
            "LEFT JOIN cc4.puzzle p ON p.id = pg.puzzle_id AND p.prewar = true " +
            "LEFT JOIN cc4.puzzle_solution ps ON p.id = ps.puzzle_id AND ps.solution = pg.guess " +
            "GROUP BY t.short_name) " +
            "SELECT t.role_id, t.short_name, 'test' AS full_name, t.welcome_text, t.leader_image, t.token_image, t.color as color, count(m.user_id) AS member_count, tt.tokens AS prewar_tokens " +
            "FROM cc4.team t LEFT JOIN cc4.member m ON t.id = m.team_id " +
            "LEFT JOIN cc4.class c ON c.id = m.class_id AND c.name = #{selectedClass} " +
            "LEFT JOIN team_tokens tt ON t.short_name = tt.short_name " +
            "GROUP BY t.role_id, t.short_name, t.welcome_text, t.leader_image, t.token_image, t.color, tt.tokens")
    @Results(value = {
            @Result(property = "roleId", column = "role_id"),
            @Result(property = "shortName", column = "short_name"),
            @Result(property = "fullName", column = "full_name"),
            @Result(property = "welcomeText", column = "welcome_text"),
            @Result(property = "leaderImage", column = "leader_image"),
            @Result(property = "colorValue", column = "color"),
            @Result(property = "memberCount", column = "member_count"),
            @Result(property = "prewarTokens", column = "prewar_tokens")
    })
    List<WarTeam> getTeamsByClass(@Param("selectedClass") String selectedClass);

    @Select("WITH team_tokens AS (SELECT t.short_name, COALESCE(SUM(p.value), 0) AS tokens FROM cc4.team t LEFT JOIN cc4.member m ON t.id = m.team_id " +
            "LEFT JOIN cc4.puzzle_guess pg ON m.user_id = pg.user_id " +
            "LEFT JOIN cc4.puzzle p ON p.id = pg.puzzle_id AND p.prewar = true " +
            "LEFT JOIN cc4.puzzle_solution ps ON p.id = ps.puzzle_id AND ps.solution = pg.guess " +
            "GROUP BY t.short_name) " +
            "SELECT t.role_id, t.short_name, 'test' AS full_name, t.welcome_text, t.leader_image, t.token_image, t.color as color, count(m.user_id) AS member_count, tt.tokens AS prewar_tokens " +
            "FROM cc4.team t LEFT JOIN cc4.member m ON t.id = m.team_id " +
            "LEFT JOIN team_tokens tt ON t.short_name = tt.short_name " +
            "WHERE m.user_id = #{userId} " +
            "GROUP BY t.role_id, t.short_name, t.welcome_text, t.leader_image, t.token_image, t.color, tt.tokens")
    @Results(value = {
            @Result(property = "roleId", column = "role_id"),
            @Result(property = "shortName", column = "short_name"),
            @Result(property = "fullName", column = "full_name"),
            @Result(property = "welcomeText", column = "welcome_text"),
            @Result(property = "leaderImage", column = "leader_image"),
            @Result(property = "tokenImage", column = "token_image"),
            @Result(property = "colorValue", column = "color"),
            @Result(property = "memberCount", column = "member_count"),
            @Result(property = "prewarTokens", column = "prewar_tokens")
    })
    WarTeam getTeam(@Param("userId") long userId);


    @Select("SELECT COALESCE(SUM(p.value), 0) AS tokens FROM cc4.team t LEFT JOIN cc4.member m ON t.id = m.team_id " +
            "LEFT JOIN cc4.puzzle_guess pg ON m.user_id = pg.user_id " +
            "LEFT JOIN cc4.puzzle p ON p.id = pg.puzzle_id " +
            "LEFT JOIN cc4.puzzle_solution ps ON p.id = ps.puzzle_id AND ps.solution = pg.guess " +
            "WHERE p.prewar = true AND t.short_name = #{shortName}")
    int getTeamTokens(@Param("shortName") String shortName);

    @Select("SELECT EXISTS(SELECT * FROM cc4.member m WHERE m.user_id = #{userId})")
    boolean isTeamMember(@Param("userId") long userId);

    @Select("SELECT m.banned FROM cc4.member m WHERE m.user_id = #{userId}")
    boolean isBanned(@Param("userId") long userId);

    @Insert("INSERT INTO cc4.member (user_id, team_id, class_id, join_time) VALUES " +
            "(#{userId}, (SELECT t.id FROM cc4.team t WHERE t.short_name = #{shortName}), " +
            "(SELECT c.id FROM cc4.class c WHERE c.name = #{selectedClass}), #{joinTime})")
    void addTeamMember(@Param("userId") long userId, @Param("shortName") String shortName,
                       @Param("selectedClass") String selectedClass, @Param("joinTime") Instant joinTime);
}

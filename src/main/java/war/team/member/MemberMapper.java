package war.team.member;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface MemberMapper
{
    @Select("SELECT COALESCE(SUM(p.value), 0) AS tokens FROM cc4.puzzle p LEFT JOIN cc4.puzzle_solution ps ON p.id = ps.puzzle_id " +
            "LEFT JOIN cc4.puzzle_guess pg ON ps.solution = pg.guess AND p.id = pg.puzzle_id " +
            "WHERE pg.user_id = #{userId} AND p.prewar = true")
    int getPrewarTokens(long userId);

    @Update("UPDATE cc4.member SET banned = #{ban} WHERE user_id = #{userId}")
    int updateBanStatus(@Param("userId") long userId, @Param("ban") boolean ban);
}

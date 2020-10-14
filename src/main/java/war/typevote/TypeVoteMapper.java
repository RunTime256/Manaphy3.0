package war.typevote;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.Instant;
import java.util.List;

public interface TypeVoteMapper
{
    @Select("SELECT DISTINCT tv.type FROM cc4.puzzle p LEFT JOIN cc4.puzzle_solution ps ON p.id = ps.puzzle_id " +
            "RIGHT JOIN cc4.puzzle_guess pg ON p.id = pg.puzzle_id LEFT JOIN cc4.type_vote tv ON p.id = tv.puzzle_id " +
            "LEFT JOIN cc4.type_vote_selection tvs ON tv.id = tvs.type_id " +
            "WHERE pg.user_id = #{userId} AND tv.type IS NOT NULL AND tvs.id IS NULL")
    List<String> getAvailableTypes(@Param("userId") long userId);

    @Select("SELECT DISTINCT tv.type FROM cc4.puzzle p LEFT JOIN cc4.puzzle_solution ps ON p.id = ps.puzzle_id " +
            "RIGHT JOIN cc4.puzzle_guess pg ON p.id = pg.puzzle_id LEFT JOIN cc4.type_vote tv ON p.id = tv.puzzle_id " +
            "LEFT JOIN cc4.type_vote_selection tvs ON tv.id = tvs.type_id " +
            "WHERE pg.user_id = #{userId} AND tv.type IS NOT NULL AND tvs.id IS NOT NULL")
    List<String> getVotedTypes(@Param("userId") long userId);

    @Select("SELECT count(DISTINCT tv.type) FROM cc4.puzzle p LEFT JOIN cc4.puzzle_solution ps ON p.id = ps.puzzle_id " +
            "RIGHT JOIN cc4.puzzle_guess pg ON p.id = pg.puzzle_id LEFT JOIN cc4.type_vote tv ON p.id = tv.puzzle_id " +
            "LEFT JOIN cc4.type_vote_selection tvs ON tv.id = tvs.type_id " +
            "WHERE pg.user_id = #{userId} AND tv.type IS NOT NULL AND tvs.id IS NOT NULL")
    int getRemainingTypeVoteCount(@Param("userId") long userId);

    @Insert("INSERT INTO cc4.type_vote_selection (type_id, user_id, time) VALUES " +
            "((SELECT id FROM cc4.type_vote WHERE type = #{type}), #{userId}, #{time})")
    void addTypeVote(@Param("type") String type, @Param("userId") long userId, @Param("time") Instant time);
}

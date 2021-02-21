package war.contest;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ContestMapper
{
    @Select("SELECT EXISTS(SELECT * FROM cc4.contest WHERE name = #{name})")
    boolean isContest(@Param("name") String name);

    @Select("SELECT EXISTS(SELECT * FROM cc4.contest_winner_tokens cwt LEFT JOIN cc4.contest c ON c.id = cwt.contest_id " +
            "WHERE c.name = #{name} AND cwt.place = #{place})")
    boolean isPlace(@Param("name") String name, @Param("place") int place);

    @Insert("INSERT INTO cc4.contest_participant (contest_id, user_id) VALUES " +
            "((SELECT id FROM cc4.contest WHERE name = #{name}), #{userId})")
    void addParticipant(@Param("name") String name, @Param("userId") long userId);

    @Insert("")
    void addWinner(@Param("name") String name, @Param("userId") long userId, @Param("place") int place);
}

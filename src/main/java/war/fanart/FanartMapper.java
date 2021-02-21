package war.fanart;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

public interface FanartMapper
{
    @Insert("INSERT INTO cc4.fanart_participation (user_id, tokens) VALUES (#{userId}, 25)")
    void addParticipant(@Param("userId") long userId);

    @Insert("INSERT INTO cc4.fanart_bonus (user_id, tokens) VALUES (#{userId}, 10)")
    void addBonus(@Param("userId") long userId);
}

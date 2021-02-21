package war.code;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface CodeMapper
{
    @Select("SELECT EXISTS (SELECT * FROM cc4.code WHERE code = #{code});")
    boolean exists(@Param("code") String code);

    @Select("SELECT EXISTS (SELECT * FROM cc4.code WHERE code = #{code} AND channel_id = #{channelId});")
    boolean correctChannel(@Param("code") String code, @Param("channelId") Long channelId);

    @Select("SELECT EXISTS (SELECT * FROM cc4.code c LEFT JOIN cc4.code_retrieved cr ON c.id = cr.code_id " +
            "WHERE c.code = #{code} AND cr.user_id = #{userId});")
    boolean hasCode(@Param("code") String code, @Param("userId") Long userId);

    @Insert("INSERT INTO cc4.code_retrieved( " +
            "code_id, user_id) " +
            "VALUES ((SELECT id FROM cc4.code WHERE code = #{code}), #{userId});")
    void addCode(@Param("code") String code, @Param("userId") Long userId);
}

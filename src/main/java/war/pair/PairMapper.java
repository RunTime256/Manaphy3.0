package war.pair;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface PairMapper
{
    @Select("SELECT p.value FROM cc4.pair p WHERE p.key = #{key}")
    String getValue(@Param("key") String key);
}

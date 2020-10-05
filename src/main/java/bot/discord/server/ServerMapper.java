package bot.discord.server;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ServerMapper
{
    @Select("SELECT guild_id FROM manaphy.bot.guild g WHERE g.name = #{guildName}")
    Long getServer(@Param("guildName") String guildName);
}

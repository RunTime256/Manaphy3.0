package bot.discord.role;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface RoleMapper
{
    @Select("SELECT role_id FROM manaphy.bot.role r JOIN manaphy.bot.guild g ON r.guild_id = g.guild_id WHERE " +
            "g.name = #{guildName} AND r.name = #{roleName}")
    Long getRole(@Param("guildName") String guildName, @Param("roleName") String roleName);
}

package bot.discord.role;

import org.apache.ibatis.annotations.Select;

public interface RoleMapper
{
    @Select("SELECT role_id from manaphy.bot.role r JOIN manaphy.bot.guild g ON r.guild_id = g.guild_id WHERE " +
            "g.name = #{guildName} AND r.name = #{roleName}")
    Long getRole(String guildName, String roleName);
}

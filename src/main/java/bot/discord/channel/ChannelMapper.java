package bot.discord.channel;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ChannelMapper
{
    @Select("SELECT channel_id FROM manaphy.bot.channel c JOIN manaphy.bot.guild g ON c.guild_id = g.guild_id WHERE " +
            "g.name = #{guildName} AND c.name = #{channelName}")
    Long getChannel(@Param("guildName") String guildName, @Param("channelName") String channelName);
}

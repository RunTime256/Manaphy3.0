package bot.discord;

import org.apache.ibatis.annotations.Select;

public interface BotMapper
{
    @Select("SELECT name, token, prefix FROM manaphy.bot.tokens WHERE name = #{name}")
    Bot getBot(String name);
}

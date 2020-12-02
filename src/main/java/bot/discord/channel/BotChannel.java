package bot.discord.channel;

import sql.Session;

public class BotChannel
{
    private BotChannel()
    {
    }

    public static Long getChannelId(String server, String channelName, Session session)
    {
        return session.getMapper(ChannelMapper.class).getChannel(server, channelName);
    }
}

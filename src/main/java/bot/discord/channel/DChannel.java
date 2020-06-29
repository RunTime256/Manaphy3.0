package bot.discord.channel;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;

import java.util.Optional;

public class DChannel
{
    private DChannel()
    {
    }

    public static TextChannel getChannel(DiscordApi api, Long channelId)
    {
        Optional<TextChannel> channel = api.getTextChannelById(channelId);
        return channel.orElse(null);
    }
}

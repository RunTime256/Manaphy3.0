package bot.discord.channel;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;

import java.util.List;
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

    public static TextChannel getChannel(DiscordApi api, long id)
    {
        TextChannel channel = null;
        if (id != 0L)
        {
            Channel serverChannel = api.getChannelById(id).orElse(null);
            if (serverChannel != null)
                channel = serverChannel.asTextChannel().orElse(null);
        }
        return channel;
    }

    public static ServerTextChannel getServerChannel(Server server, long id, String name)
    {
        ServerTextChannel channel = null;
        if (id != 0L)
        {
            channel = server.getTextChannelById(id).orElse(null);
            if (channel != null && channel.asChannelCategory().isPresent())
                channel = null;
        }
        else
        {
            List<ServerTextChannel> list = server.getTextChannelsByName(name);
            if (!list.isEmpty())
            {
                channel = list.get(0);
                if (channel != null && channel.asChannelCategory().isPresent())
                    channel = null;
            }
        }
        return channel;
    }

    public static ServerVoiceChannel getVoiceChannel(Server server, long id, String name)
    {
        ServerVoiceChannel channel = null;
        if (id != 0L)
        {
            channel = server.getVoiceChannelById(id).orElse(null);
        }
        else
        {
            List<ServerVoiceChannel> list = server.getVoiceChannelsByName(name);
            if (!list.isEmpty())
            {
                channel = list.get(0);
            }
        }
        return channel;
    }
}

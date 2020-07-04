package bot.discord.category;

import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.server.Server;

import java.util.List;

public class DCategory
{
    private DCategory()
    {
    }

    public static ChannelCategory getCategory(Server server, long id, String name)
    {
        ServerChannel channel = null;
        if (id != 0L)
        {
            channel = server.getChannelById(id).orElse(null);
        }
        else
        {
            List<ServerChannel> list = server.getChannelsByName(name);
            if (!list.isEmpty())
                channel = list.get(0);
        }

        ChannelCategory category = null;

        if (channel != null)
            category = channel.asChannelCategory().orElse(null);

        return category;
    }
}

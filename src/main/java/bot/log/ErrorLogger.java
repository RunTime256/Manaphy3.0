package bot.log;

import bot.discord.channel.DChannel;
import bot.discord.message.DMessage;
import exception.BotException;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;

import java.awt.Color;

public class ErrorLogger
{
    private final DiscordApi api;
    private final long channelId;

    public ErrorLogger(DiscordApi api, long channelId)
    {
        this.api = api;
        this.channelId = channelId;
    }

    public void log(long userId, Exception e)
    {
        TextChannel channel = DChannel.getChannel(api, channelId);
        if (!(e instanceof BotException) || ((BotException) e).getColor() == Color.RED)
            DMessage.sendMessage(channel, userId, e, true);
    }

    public void log(String command, long userId, Exception e)
    {
        TextChannel channel = DChannel.getChannel(api, channelId);
        if (!(e instanceof BotException) || ((BotException) e).getColor() == Color.RED)
            DMessage.sendMessage(channel, command, userId, e, true);
    }
}

package bot.discord.message;

import exception.BotException;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.Color;
import java.util.concurrent.CompletableFuture;

public class DMessage
{
    private DMessage()
    {
    }

    public static CompletableFuture<Message> sendMessage(TextChannel channel, String message)
    {
        return channel.sendMessage(message);
    }

    public static CompletableFuture<Message> sendMessage(TextChannel channel, EmbedBuilder builder)
    {
        return channel.sendMessage(builder);
    }

    public static CompletableFuture<Message> sendMessage(TextChannel channel, Exception e, boolean log)
    {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Exception occurred: " + e.getClass().getName());
        if (e instanceof BotException)
        {
            BotException exception = (BotException)e;
            builder.setColor(exception.getColor()).setDescription(exception.getMessage());
        }
        else
        {
            if (log)
                builder.setDescription(e.getMessage());
            builder.setColor(Color.RED);
        }
        return channel.sendMessage(builder);
    }
}

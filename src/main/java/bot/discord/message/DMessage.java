package bot.discord.message;

import exception.BotException;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.Color;
import java.io.IOException;
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

    public static CompletableFuture<Message> sendMessage(TextChannel channel, Long userId, Exception e, boolean log)
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
                builder.addField("Caused by User ID", String.valueOf(userId)).addField("Details", e.getMessage());
            builder.setColor(Color.RED);
        }
        return channel.sendMessage(builder);
    }

    public static CompletableFuture<Message> sendMessage(TextChannel channel, String command, Long userId, Exception e, boolean log)
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
                builder.setDescription(command).addField("Caused by User ID", String.valueOf(userId)).addField("Details", e.getMessage());
            builder.setColor(Color.RED);
        }
        return channel.sendMessage(builder);
    }

    public static CompletableFuture<Message> sendMessage(TextChannel channel, String message, MessageAttachment attachment)
    {
        try
        {
            return channel.sendMessage(message, attachment.downloadAsInputStream(), attachment.getFileName());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static CompletableFuture<Message> sendMessage(TextChannel channel, MessageAttachment attachment)
    {
        try
        {
            return channel.sendMessage(attachment.downloadAsInputStream(), attachment.getFileName());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}

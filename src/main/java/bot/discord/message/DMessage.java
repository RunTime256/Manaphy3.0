package bot.discord.message;

import bot.discord.user.DUser;
import exception.BotException;
import exception.bot.command.InvalidCommandException;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import java.awt.Color;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class DMessage
{
    private DMessage()
    {
    }

    public static CompletableFuture<Message> sendPrivateMessage(DiscordApi api, long userId, String message)
    {
        User user = DUser.getUser(api, userId);
        return user.sendMessage(message);
    }

    public static CompletableFuture<Message> sendPrivateMessage(DiscordApi api, long userId, EmbedBuilder builder)
    {
        User user = DUser.getUser(api, userId);
        return user.sendMessage(builder);
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
        String title;
        if (e instanceof BotException)
        {
            if (e instanceof InvalidCommandException)
                title = "Error with command:";
            else
                title = "Exception occurred: " + e.getClass().getSimpleName();

            BotException exception = (BotException)e;
            builder.setColor(exception.getColor()).setDescription(exception.getMessage());
        }
        else
        {
            title = "Exception occurred: " + e.getClass().getSimpleName();
            if (log)
                builder.addField("Caused by User ID", String.valueOf(userId)).addField("Details", e.getMessage());
            builder.setColor(Color.RED);
        }
        builder.setTitle(title);
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

package bot.discord.information;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.time.Instant;
import java.util.Optional;

public class MessageReceivedInformation
{
    private final MessageCreateEvent event;

    public MessageReceivedInformation(MessageCreateEvent messageCreateEvent)
    {
        this.event = messageCreateEvent;
    }

    public DiscordApi getApi()
    {
        return event.getApi();
    }

    public User getUser()
    {
        Optional<User> user = event.getMessageAuthor().asUser();
        return user.orElse(null);
    }

    public TextChannel getChannel()
    {
        return event.getChannel();
    }

    public Message getMessage()
    {
        return event.getMessage();
    }

    public Server getServer()
    {
        return event.getServer().orElse(null);
    }

    public String getContent()
    {
        return event.getMessageContent();
    }

    public Instant getTime()
    {
        return event.getMessage().getCreationTimestamp();
    }

    public void delete()
    {
        event.getMessage().delete();
    }
}

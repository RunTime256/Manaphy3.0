package bot.discord.listener;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

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
}

package bot.discord.information;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.reaction.ReactionAddEvent;

public class ReactionReceivedInformation
{
    private final ReactionAddEvent reactionAddEvent;

    public ReactionReceivedInformation(ReactionAddEvent reactionAddEvent)
    {
        this.reactionAddEvent = reactionAddEvent;
    }

    public String getUnicodeReaction()
    {
        return reactionAddEvent.getEmoji().asUnicodeEmoji().orElse(null);
    }

    public User getUser()
    {
        return reactionAddEvent.getUser();
    }

    public TextChannel getChannel()
    {
        return reactionAddEvent.getChannel();
    }

    public long getMessageId()
    {
        return reactionAddEvent.getMessageId();
    }
}

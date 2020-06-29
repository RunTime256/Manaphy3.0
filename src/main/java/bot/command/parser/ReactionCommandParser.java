package bot.command.parser;

import bot.command.ReactionCommand;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;

public class ReactionCommandParser
{
    private final User user;
    private final TextChannel channel;
    private final String reaction;
    private final ReactionCommand command;

    public ReactionCommandParser(User user, TextChannel channel, String reaction, ReactionCommand command)
    {
        this.user = user;
        this.channel = channel;
        this.reaction = reaction;
        this.command = command;
    }

    public ReactionCommand getCommand(User user, TextChannel channel, String reaction)
    {
        if (this.user.getId() == user.getId() && this.channel.getId() == channel.getId() && this.reaction.equals(reaction))
            return command;
        else
            return null;
    }
}

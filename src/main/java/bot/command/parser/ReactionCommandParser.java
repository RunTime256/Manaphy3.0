package bot.command.parser;

import bot.command.ReactionCommand;

public class ReactionCommandParser
{
    private final long userId;
    private final long channelId;
    private final long messageId;
    private final String reaction;
    private final ReactionCommand command;

    public ReactionCommandParser(long userId, long channelId, long messageId, String reaction, ReactionCommand command)
    {
        this.userId = userId;
        this.channelId = channelId;
        this.messageId = messageId;
        this.reaction = reaction;
        this.command = command;
    }

    public ReactionCommand getCommand(long userId, long channelId, long messageId, String reaction)
    {
        if (this.userId == userId && this.channelId == channelId && this.messageId == messageId && this.reaction.equals(reaction))
            return command;
        else
            return null;
    }
}

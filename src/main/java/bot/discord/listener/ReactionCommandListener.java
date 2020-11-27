package bot.discord.listener;

import bot.command.ReactionCommand;
import bot.command.parser.ReactionCommandParser;
import bot.discord.information.ReactionReceivedInformation;
import bot.discord.message.DMessage;
import bot.log.ErrorLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.listener.message.reaction.ReactionAddListener;
import sql.Session;
import sql.SessionFactory;

public class ReactionCommandListener implements ReactionAddListener
{
    private static final Logger logger = LogManager.getLogger(ReactionCommandListener.class);
    private final ReactionCommandParser parser;
    private final DiscordApi api;
    private static ErrorLogger errorLogger;

    public ReactionCommandListener(long userId, long channelId, long messageId, String reaction, DiscordApi api, ReactionCommand command)
    {
        this.parser = new ReactionCommandParser(userId, channelId, messageId, reaction, command);
        this.api = api;
    }

    public static void setLogger(ErrorLogger logger)
    {
        errorLogger = logger;
    }

    @Override
    public void onReactionAdd(ReactionAddEvent reactionAddEvent)
    {
        ReactionReceivedInformation info = new ReactionReceivedInformation(reactionAddEvent);
        String emoji = info.getUnicodeReaction();
        if (emoji != null)
        {
            ReactionCommand command = parser.getCommand(info.getUser().getId(), info.getChannel().getId(), info.getMessageId(), info.getUnicodeReaction());
            if (command == null)
            {
                return;
            }

            executeCommand(info, command);
        }
    }

    private void executeCommand(ReactionReceivedInformation info, ReactionCommand command)
    {
        try (Session session = SessionFactory.getSession())
        {
            try
            {
                command.execute(api, info, session);
                session.commit();
            }
            catch (Exception e)
            {
                logger.fatal("Exception occurred", e);
                if (errorLogger != null)
                    errorLogger.log(info.getUser().getId(), e);
                DMessage.sendMessage(info.getChannel(), null, e, false);
                session.rollback();
            }
        }
    }
}

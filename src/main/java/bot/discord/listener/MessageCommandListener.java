package bot.discord.listener;

import bot.command.MessageCommand;
import bot.command.parser.MessageCommandParser;
import bot.command.verification.RoleCheck;
import bot.discord.message.DMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import sql.Session;
import sql.SessionFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageCommandListener implements MessageCreateListener
{
    private static final Logger logger = LogManager.getLogger(MessageCommandListener.class);
    private final MessageCommandParser parser;
    private final String prefix;
    private final boolean botCommand;
    private final DiscordApi api;

    public MessageCommandListener(String prefix, DiscordApi api)
    {
        this(prefix, false, api);
    }

    public MessageCommandListener(String prefix, boolean botCommand, DiscordApi api)
    {
        parser = new MessageCommandParser();
        this.prefix = prefix;
        this.botCommand = botCommand;
        this.api = api;
    }

    public void addCommand(MessageCommand command)
    {
        parser.addCommand(command);
    }

    public void onMessageCreate(MessageCreateEvent messageCreateEvent)
    {
        String message = messageCreateEvent.getMessageContent();
        if (message != null && message.startsWith(prefix) &&
                (!botCommand && !messageCreateEvent.getMessageAuthor().isBotUser() ||
                        botCommand && messageCreateEvent.getMessageAuthor().isBotUser() && messageCreateEvent.getMessageAuthor().isYourself()))
        {
            String commandString = message.substring(prefix.length());
            List<String> vars = new ArrayList<>(Arrays.asList(commandString.split(" ")));
            MessageCommand command = parser.getCommand(vars);

            if (command == null)
            {
                return;
            }

            MessageReceivedInformation info = new MessageReceivedInformation(messageCreateEvent);
            executeCommand(info, vars, command);
        }
    }

    private void executeCommand(MessageReceivedInformation info, List<String> vars, MessageCommand command)
    {
        User user = info.getUser();
        if (user == null)
        {
            logger.error("User was null");
            return;
        }

        try (Session session = SessionFactory.getSession())
        {
            if (RoleCheck.hasPermission(session, api, user, command.getRequirement()))
            {
                try
                {
                    command.execute(api, info, vars, session);
                    session.commit();
                }
                catch (Exception e)
                {
                    logger.fatal("Exception occurred", e);
                    DMessage.sendMessage(info.getChannel(), e);
                    session.rollback();
                }
            }
        }
    }
}
